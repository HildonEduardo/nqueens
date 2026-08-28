package com.hdlp.thenqueens.ui.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import app.cash.turbine.test
import com.hdlp.thenqueens.MainDispatcherRule
import com.hdlp.thenqueens.data.FakeBestTimeRepository
import com.hdlp.thenqueens.data.FakeClock
import com.hdlp.thenqueens.domain.BoardPosition
import com.hdlp.thenqueens.domain.GameStatus
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class GameViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val clock = FakeClock()
    private val repository = FakeBestTimeRepository()
    private val createdViewModels = mutableListOf<GameViewModel>()

    // The ticker never stops while a game is IN_PROGRESS; without cancelling each
    // ViewModel's scope before runTest returns, its cleanup spins the virtual clock forever.
    private fun runVmTest(block: suspend TestScope.() -> Unit) = runTest {
        try {
            block()
        } finally {
            createdViewModels.forEach { it.viewModelScope.cancel() }
        }
    }

    private fun viewModel(boardSize: Int = 4): GameViewModel =
        GameViewModel(SavedStateHandle(mapOf("boardSize" to boardSize)), clock, repository)
            .also { createdViewModels += it }

    private fun GameViewModel.tap(row: Int, column: Int) =
        onAction(GameAction.CellTapped(BoardPosition(row, column)))

    private fun GameViewModel.solveFourByFour() {
        tap(0, 1); tap(1, 3); tap(2, 0); tap(3, 2)
    }

    @Test
    fun `tap places a queen and starts the game`() = runVmTest {
        val vm = viewModel()
        vm.tap(0, 1)
        assertEquals(setOf(BoardPosition(0, 1)), vm.state.value.queens)
        assertEquals(GameStatus.IN_PROGRESS, vm.state.value.status)
        assertEquals(3, vm.state.value.queensLeft)
    }

    @Test
    fun `tapping an occupied cell removes the queen`() = runVmTest {
        val vm = viewModel()
        vm.tap(0, 1)
        vm.tap(0, 1)
        assertEquals(emptySet<BoardPosition>(), vm.state.value.queens)
    }

    @Test
    fun `placement stops at the queen limit but removal still works`() = runVmTest {
        val vm = viewModel()
        vm.tap(0, 0); vm.tap(0, 1); vm.tap(0, 2); vm.tap(0, 3)
        vm.tap(1, 0)
        assertEquals(4, vm.state.value.queens.size)
        vm.tap(0, 0)
        assertEquals(3, vm.state.value.queens.size)
    }

    @Test
    fun `out of bounds taps are ignored`() = runVmTest {
        val vm = viewModel()
        vm.tap(-1, 0)
        vm.tap(0, 4)
        assertEquals(emptySet<BoardPosition>(), vm.state.value.queens)
        assertEquals(GameStatus.NOT_STARTED, vm.state.value.status)
    }

    @Test
    fun `conflicts are recalculated on add and remove`() = runVmTest {
        val vm = viewModel()
        vm.tap(0, 0)
        vm.tap(0, 2)
        assertEquals(setOf(BoardPosition(0, 0), BoardPosition(0, 2)), vm.state.value.conflicts)
        vm.tap(0, 2)
        assertEquals(emptySet<BoardPosition>(), vm.state.value.conflicts)
    }

    @Test
    fun `ticker updates elapsed time while in progress`() = runVmTest {
        val vm = viewModel()
        vm.tap(0, 1)
        clock.nowMillis = 1_500L
        advanceTimeBy(GameViewModel.TICK_MILLIS + 1)
        assertEquals(1_500L, vm.state.value.elapsedMillis)
    }

    @Test
    fun `solving transitions to solved with final elapsed time`() = runVmTest {
        val vm = viewModel()
        vm.tap(0, 1); vm.tap(1, 3); vm.tap(2, 0)
        clock.nowMillis = 7_000L
        vm.tap(3, 2)
        assertTrue(vm.state.value.isSolved)
        assertEquals(7_000L, vm.state.value.elapsedMillis)
    }

    @Test
    fun `solving saves the best time exactly once`() = runVmTest {
        val vm = viewModel()
        clock.nowMillis = 0L
        vm.solveFourByFour()
        advanceUntilIdle()
        assertEquals(listOf(4 to vm.state.value.elapsedMillis), repository.saveCalls)
    }

    @Test
    fun `taps after solved are ignored`() = runVmTest {
        val vm = viewModel()
        vm.solveFourByFour()
        vm.tap(0, 0)
        assertEquals(4, vm.state.value.queens.size)
        assertTrue(vm.state.value.isSolved)
    }

    @Test
    fun `reset restores the initial state but keeps the best time`() = runVmTest {
        repository.bestTimes.value = mapOf(4 to 9_000L)
        val vm = viewModel()
        advanceUntilIdle()
        vm.tap(0, 0); vm.tap(0, 1)
        vm.onAction(GameAction.ResetClicked)
        assertEquals(GameUiState(boardSize = 4, bestTimeMillis = 9_000L), vm.state.value)
    }

    @Test
    fun `timer restarts from zero after reset`() = runVmTest {
        val vm = viewModel()
        vm.tap(0, 1)
        clock.nowMillis = 5_000L
        vm.onAction(GameAction.ResetClicked)
        vm.tap(0, 1)
        clock.nowMillis = 6_000L
        advanceTimeBy(GameViewModel.TICK_MILLIS + 1)
        assertEquals(1_000L, vm.state.value.elapsedMillis)
    }

    @Test
    fun `best time flow is reflected in state`() = runVmTest {
        repository.bestTimes.value = mapOf(4 to 8_000L)
        val vm = viewModel()
        advanceUntilIdle()
        assertEquals(8_000L, vm.state.value.bestTimeMillis)
    }

    @Test
    fun `placing a safe queen emits QueenPlaced`() = runVmTest {
        val vm = viewModel()
        vm.effects.test {
            advanceUntilIdle()
            vm.tap(0, 1)
            assertEquals(GameEffect.QueenPlaced, awaitItem())
        }
    }

    @Test
    fun `placing a conflicting queen emits ConflictCreated`() = runVmTest {
        val vm = viewModel()
        vm.effects.test {
            advanceUntilIdle()
            vm.tap(0, 0)
            assertEquals(GameEffect.QueenPlaced, awaitItem())
            vm.tap(0, 2)
            assertEquals(GameEffect.ConflictCreated, awaitItem())
        }
    }

    @Test
    fun `solving emits Victory`() = runVmTest {
        val vm = viewModel()
        vm.effects.test {
            advanceUntilIdle()
            vm.solveFourByFour()
            assertEquals(GameEffect.QueenPlaced, awaitItem())
            assertEquals(GameEffect.QueenPlaced, awaitItem())
            assertEquals(GameEffect.QueenPlaced, awaitItem())
            assertEquals(GameEffect.Victory, awaitItem())
        }
    }

    @Test
    fun `removing a queen emits no effect`() = runVmTest {
        val vm = viewModel()
        vm.effects.test {
            advanceUntilIdle()
            vm.tap(0, 1)
            assertEquals(GameEffect.QueenPlaced, awaitItem())
            vm.tap(0, 1)
            expectNoEvents()
        }
    }
}
