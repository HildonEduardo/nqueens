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
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@HiltViewModel
class GameViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val clock: GameClock,
    private val bestTimeRepository: BestTimeRepository,
) : ViewModel() {

    private val boardSize: Int = checkNotNull(savedStateHandle["boardSize"])

    private val _state = MutableStateFlow(GameUiState(boardSize = boardSize))
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private var startRealtimeMillis: Long? = null
    private var tickerJob: Job? = null

    init {
        viewModelScope.launch {
            bestTimeRepository.observeBestTime(boardSize).collect { best ->
                _state.update { it.copy(bestTimeMillis = best) }
            }
        }
    }

    fun onAction(action: GameAction) {
        when (action) {
            is GameAction.CellTapped -> onCellTapped(action.position)
            GameAction.ResetClicked, GameAction.PlayAgainClicked -> reset()
        }
    }

    private fun onCellTapped(position: BoardPosition) {
        val current = _state.value
        if (current.status == GameStatus.SOLVED) return
        if (position.row !in 0 until boardSize || position.column !in 0 until boardSize) return

        val queens = when {
            position in current.queens -> current.queens - position
            current.queens.size < boardSize -> current.queens + position
            else -> return
        }
        if (current.status == GameStatus.NOT_STARTED) startTimer()

        val conflicts = NQueensRules.conflictingQueens(queens)
        val solved = queens.size == boardSize && conflicts.isEmpty()
        val elapsed = elapsedNow()
        _state.update {
            it.copy(
                queens = queens,
                conflicts = conflicts,
                status = if (solved) GameStatus.SOLVED else GameStatus.IN_PROGRESS,
                elapsedMillis = elapsed,
            )
        }
        if (solved) onSolved(elapsed)
    }

    private fun onSolved(elapsedMillis: Long) {
        stopTicker()
        viewModelScope.launch { bestTimeRepository.saveIfBetter(boardSize, elapsedMillis) }
    }

    private fun reset() {
        stopTicker()
        startRealtimeMillis = null
        _state.update { GameUiState(boardSize = boardSize, bestTimeMillis = it.bestTimeMillis) }
    }

    private fun startTimer() {
        startRealtimeMillis = clock.elapsedRealtimeMillis()
        startTicker()
    }

    private fun startTicker() {
        tickerJob = viewModelScope.launch {
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

    private fun elapsedNow(): Long =
        startRealtimeMillis?.let { (clock.elapsedRealtimeMillis() - it).coerceAtLeast(0L) } ?: 0L

    companion object {
        const val TICK_MILLIS = 100L
    }
}
