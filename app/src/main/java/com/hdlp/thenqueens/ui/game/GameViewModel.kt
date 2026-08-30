package com.hdlp.thenqueens.ui.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdlp.thenqueens.data.BestTimeRepository
import com.hdlp.thenqueens.data.GameClock
import com.hdlp.thenqueens.domain.BoardPosition
import com.hdlp.thenqueens.domain.GameStatus
import com.hdlp.thenqueens.domain.NQueensRules
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel
    @Inject
    constructor(
        private val savedStateHandle: SavedStateHandle,
        private val clock: GameClock,
        private val bestTimeRepository: BestTimeRepository,
    ) : ViewModel() {
        private val boardSize: Int = checkNotNull(savedStateHandle["boardSize"])

        private val _state = MutableStateFlow(restoredState())
        val state: StateFlow<GameUiState> = _state.asStateFlow()

        // No replay: a sound that nobody heard should not be replayed after rotation.
        private val _effects =
            MutableSharedFlow<GameEffect>(
                extraBufferCapacity = 8,
                onBufferOverflow = BufferOverflow.DROP_OLDEST,
            )
        val effects: SharedFlow<GameEffect> = _effects.asSharedFlow()

        private var startRealtimeMillis: Long? = savedStateHandle[KEY_START_REALTIME]
        private var pausedAtRealtimeMillis: Long? = savedStateHandle[KEY_PAUSED_AT]
        private var tickerJob: Job? = null

        // Board snapshots before/after the present one. In-memory only: the ViewModel
        // outlives rotation, and losing history on process death is an accepted trade-off.
        private val past = ArrayDeque<Set<BoardPosition>>()
        private val future = ArrayDeque<Set<BoardPosition>>()

        init {
            viewModelScope.launch {
                bestTimeRepository.observeBestTime(boardSize).collect { best ->
                    _state.update { it.copy(bestTimeMillis = best) }
                }
            }
            if (_state.value.status == GameStatus.IN_PROGRESS && pausedAtRealtimeMillis == null) {
                startTicker()
            }
        }

        fun onAction(action: GameAction) {
            when (action) {
                is GameAction.CellTapped -> onCellTapped(action.position)
                GameAction.UndoClicked -> undo()
                GameAction.RedoClicked -> redo()
                GameAction.ResetClicked, GameAction.PlayAgainClicked -> reset()
                GameAction.PauseRequested -> pause()
                GameAction.ResumeRequested -> resume()
            }
        }

        private fun onCellTapped(position: BoardPosition) {
            val current = _state.value
            if (current.status == GameStatus.SOLVED || pausedAtRealtimeMillis != null) return
            if (position.row !in 0 until boardSize || position.column !in 0 until boardSize) return

            val queens =
                when {
                    position in current.queens -> current.queens - position
                    current.queens.size < boardSize -> current.queens + position
                    else -> return
                }
            if (current.status == GameStatus.NOT_STARTED) {
                startTimer()
            }
            past.addLast(current.queens)
            future.clear()

            val conflicts = NQueensRules.conflictingQueens(queens)
            val solved = queens.size == boardSize && conflicts.isEmpty()
            val elapsed = elapsedNow()
            _state.update {
                it.copy(
                    queens = queens,
                    conflicts = conflicts,
                    status = if (solved) GameStatus.SOLVED else GameStatus.IN_PROGRESS,
                    elapsedMillis = elapsed,
                    canUndo = !solved,
                    canRedo = false,
                )
            }
            persist()
            if (position in queens) {
                _effects.tryEmit(
                    when {
                        solved -> GameEffect.Victory
                        position in conflicts -> GameEffect.ConflictCreated
                        else -> GameEffect.QueenPlaced
                    },
                )
            }
            if (solved) onSolved(elapsed)
        }

        private fun onSolved(elapsedMillis: Long) {
            stopTicker()
            viewModelScope.launch { bestTimeRepository.saveIfBetter(boardSize, elapsedMillis) }
        }

        private fun undo() {
            if (_state.value.status == GameStatus.SOLVED || pausedAtRealtimeMillis != null) return
            val previous = past.removeLastOrNull() ?: return
            future.addLast(_state.value.queens)
            restoreFromHistory(previous)
        }

        private fun redo() {
            if (_state.value.status == GameStatus.SOLVED || pausedAtRealtimeMillis != null) return
            val next = future.removeLastOrNull() ?: return
            past.addLast(_state.value.queens)
            restoreFromHistory(next)
        }

        // History never holds a solved board (the solving tap blocks further undo/redo),
        // so restoring only re-derives conflicts and leaves status and the clock untouched.
        private fun restoreFromHistory(queens: Set<BoardPosition>) {
            _state.update {
                it.copy(
                    queens = queens,
                    conflicts = NQueensRules.conflictingQueens(queens),
                    canUndo = past.isNotEmpty(),
                    canRedo = future.isNotEmpty(),
                )
            }
            persist()
        }

        private fun reset() {
            stopTicker()
            startRealtimeMillis = null
            pausedAtRealtimeMillis = null
            past.clear()
            future.clear()
            _state.update { GameUiState(boardSize = boardSize, bestTimeMillis = it.bestTimeMillis) }
            persist()
        }

        private fun pause() {
            if (_state.value.status != GameStatus.IN_PROGRESS || pausedAtRealtimeMillis != null) return
            stopTicker()
            pausedAtRealtimeMillis = clock.elapsedRealtimeMillis()
            _state.update { it.copy(elapsedMillis = elapsedNow()) }
            persist()
        }

        private fun resume() {
            val pausedAt = pausedAtRealtimeMillis ?: return
            // Shift the start forward by the pause span so paused time never counts.
            startRealtimeMillis = startRealtimeMillis?.plus(clock.elapsedRealtimeMillis() - pausedAt)
            pausedAtRealtimeMillis = null
            persist()
            startTicker()
        }

        private fun restoredState(): GameUiState {
            val encoded =
                savedStateHandle.get<IntArray>(KEY_QUEENS)
                    ?: return GameUiState(boardSize = boardSize)
            val queens = encoded.map { BoardPosition(it / boardSize, it % boardSize) }.toSet()
            val status =
                GameStatus.valueOf(
                    savedStateHandle[KEY_STATUS] ?: GameStatus.NOT_STARTED.name,
                )
            return GameUiState(
                boardSize = boardSize,
                queens = queens,
                conflicts = NQueensRules.conflictingQueens(queens),
                status = status,
                // The ticker won't run while solved or paused, so restore the frozen value.
                elapsedMillis =
                    if (status == GameStatus.SOLVED || savedStateHandle.get<Long>(KEY_PAUSED_AT) != null) {
                        savedStateHandle[KEY_ELAPSED] ?: 0L
                    } else {
                        0L
                    },
            )
        }

        private fun persist() {
            val snapshot = _state.value
            savedStateHandle[KEY_QUEENS] =
                snapshot.queens.map { it.row * boardSize + it.column }.toIntArray()
            savedStateHandle[KEY_STATUS] = snapshot.status.name
            savedStateHandle[KEY_START_REALTIME] = startRealtimeMillis
            savedStateHandle[KEY_PAUSED_AT] = pausedAtRealtimeMillis
            savedStateHandle[KEY_ELAPSED] = snapshot.elapsedMillis
        }

        private fun startTimer() {
            startRealtimeMillis = clock.elapsedRealtimeMillis()
            startTicker()
        }

        private fun startTicker() {
            tickerJob =
                viewModelScope.launch {
                    while (isActive) {
                        delay(TICK_MILLIS)
                        _state.update { it.copy(elapsedMillis = elapsedNow()) }
                    }
                }
        }

        private fun stopTicker() {
            tickerJob?.cancel()
            tickerJob = null
        }

        private fun elapsedNow(): Long = startRealtimeMillis?.let { (clock.elapsedRealtimeMillis() - it).coerceAtLeast(0L) } ?: 0L

        companion object {
            const val TICK_MILLIS = 100L
            internal const val KEY_QUEENS = "queens"
            internal const val KEY_STATUS = "status"
            internal const val KEY_START_REALTIME = "startRealtime"
            internal const val KEY_PAUSED_AT = "pausedAt"
            internal const val KEY_ELAPSED = "elapsed"
        }
    }
