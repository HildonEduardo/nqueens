package com.hdlp.thenqueens.ui.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.hdlp.thenqueens.MainDispatcherRule
import com.hdlp.thenqueens.data.FakeBestTimeRepository
import com.hdlp.thenqueens.data.FakeClock
import com.hdlp.thenqueens.domain.BoardPosition
import com.hdlp.thenqueens.domain.GameStatus
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class GameViewModelRestorationTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val clock = FakeClock()
    private val repository = FakeBestTimeRepository()
    private val createdViewModels = mutableListOf<GameViewModel>()

    private fun runVmTest(block: suspend TestScope.() -> Unit) = runTest {
        try {
            block()
        } finally {
            createdViewModels.forEach { it.viewModelScope.cancel() }
        }
    }

    private fun viewModel(handle: SavedStateHandle): GameViewModel =
        GameViewModel(handle, clock, repository).also { createdViewModels += it }

    private fun handle(boardSize: Int = 4) = SavedStateHandle(mapOf("boardSize" to boardSize))

    @Test
    fun `an in-progress game round-trips through SavedStateHandle`() = runVmTest {
        val handle = handle()
        val original = viewModel(handle)
        original.onAction(GameAction.CellTapped(BoardPosition(0, 0)))
        original.onAction(GameAction.CellTapped(BoardPosition(0, 2)))

        val restored = viewModel(handle)
        assertEquals(setOf(BoardPosition(0, 0), BoardPosition(0, 2)), restored.state.value.queens)
        assertEquals(setOf(BoardPosition(0, 0), BoardPosition(0, 2)), restored.state.value.conflicts)
        assertEquals(GameStatus.IN_PROGRESS, restored.state.value.status)
    }

    @Test
    fun `restored timer keeps counting from the original start`() = runVmTest {
        val handle = handle()
        viewModel(handle).onAction(GameAction.CellTapped(BoardPosition(0, 0)))

        clock.nowMillis = 4_000L
        val restored = viewModel(handle)
        advanceTimeBy(GameViewModel.TICK_MILLIS + 1)
        assertEquals(4_000L, restored.state.value.elapsedMillis)
    }

    @Test
    fun `a solved game restores as solved with its final time`() = runVmTest {
        val handle = handle()
        val original = viewModel(handle)
        original.onAction(GameAction.CellTapped(BoardPosition(0, 1)))
        original.onAction(GameAction.CellTapped(BoardPosition(1, 3)))
        original.onAction(GameAction.CellTapped(BoardPosition(2, 0)))
        clock.nowMillis = 6_000L
        original.onAction(GameAction.CellTapped(BoardPosition(3, 2)))

        clock.nowMillis = 99_000L
        val restored = viewModel(handle)
        assertTrue(restored.state.value.isSolved)
        assertEquals(6_000L, restored.state.value.elapsedMillis)
    }

    @Test
    fun `a reset game restores as fresh`() = runVmTest {
        val handle = handle()
        val original = viewModel(handle)
        original.onAction(GameAction.CellTapped(BoardPosition(0, 0)))
        original.onAction(GameAction.ResetClicked)

        val restored = viewModel(handle)
        assertEquals(GameUiState(boardSize = 4), restored.state.value)
    }

    @Test
    fun `a fresh handle starts a fresh game`() = runVmTest {
        val vm = viewModel(handle())
        assertEquals(GameUiState(boardSize = 4), vm.state.value)
    }
}
