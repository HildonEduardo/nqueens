package com.hdlp.thenqueens.ui.leaderboard

import app.cash.turbine.test
import com.hdlp.thenqueens.MainDispatcherRule
import com.hdlp.thenqueens.data.FakeBestTimeRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LeaderboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeBestTimeRepository()

    @Test
    fun `only board sizes with victories appear, in size order`() = runTest {
        repository.topTimes.value =
            mapOf(
                8 to listOf(9_000L, 12_000L),
                4 to listOf(5_000L),
            )
        val vm = LeaderboardViewModel(repository)
        vm.entries.test {
            assertEquals(emptyList<LeaderboardEntry>(), awaitItem())
            assertEquals(
                listOf(
                    LeaderboardEntry(4, listOf(5_000L)),
                    LeaderboardEntry(8, listOf(9_000L, 12_000L)),
                ),
                awaitItem(),
            )
        }
    }

    @Test
    fun `new victories update the leaderboard`() = runTest {
        val vm = LeaderboardViewModel(repository)
        vm.entries.test {
            assertEquals(emptyList<LeaderboardEntry>(), awaitItem())
            repository.saveIfBetter(4, 7_000L)
            assertEquals(listOf(LeaderboardEntry(4, listOf(7_000L))), awaitItem())
        }
    }
}
