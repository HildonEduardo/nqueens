package com.hdlp.thenqueens.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import app.cash.turbine.test
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DataStoreBestTimeRepositoryTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private fun TestScope.repository(): DataStoreBestTimeRepository =
        DataStoreBestTimeRepository(
            PreferenceDataStoreFactory.create(scope = backgroundScope) {
                tmp.newFile("test.preferences_pb")
            },
        )

    @Test
    fun `no best time initially`() = runTest {
        repository().observeBestTime(4).test {
            assertEquals(null, awaitItem())
        }
    }

    @Test
    fun `saves first time and improvements only`() = runTest {
        val repository = repository()
        repository.observeBestTime(4).test {
            assertEquals(null, awaitItem())
            repository.saveIfBetter(4, 9_000L)
            assertEquals(9_000L, awaitItem())
            repository.saveIfBetter(4, 12_000L)
            repository.saveIfBetter(4, 5_000L)
            assertEquals(5_000L, awaitItem())
        }
    }

    @Test
    fun `best times are isolated per board size`() = runTest {
        val repository = repository()
        repository.saveIfBetter(4, 9_000L)
        repository.saveIfBetter(5, 3_000L)
        repository.observeBestTime(4).test {
            assertEquals(9_000L, awaitItem())
        }
    }

    @Test
    fun `keeps only the three fastest times, sorted`() = runTest {
        val repository = repository()
        repository.saveIfBetter(4, 9_000L)
        repository.saveIfBetter(4, 5_000L)
        repository.saveIfBetter(4, 12_000L)
        repository.saveIfBetter(4, 7_000L)
        repository.observeTopTimes(4).test {
            assertEquals(listOf(5_000L, 7_000L, 9_000L), awaitItem())
        }
    }

    @Test
    fun `an oversized stored list is capped on read`() = runTest {
        val dataStore =
            PreferenceDataStoreFactory.create(scope = backgroundScope) {
                tmp.newFile("oversized.preferences_pb")
            }
        dataStore.edit { it[stringPreferencesKey("best_times_4")] = "1000,2000,3000,4000,5000" }
        val repository = DataStoreBestTimeRepository(dataStore)
        repository.observeTopTimes(4).test {
            assertEquals(listOf(1_000L, 2_000L, 3_000L), awaitItem())
        }
    }

    @Test
    fun `a pre-leaderboard single best time seeds the top list`() = runTest {
        val dataStore =
            PreferenceDataStoreFactory.create(scope = backgroundScope) {
                tmp.newFile("legacy.preferences_pb")
            }
        dataStore.edit { it[longPreferencesKey("best_time_4")] = 8_000L }
        val repository = DataStoreBestTimeRepository(dataStore)
        repository.observeTopTimes(4).test {
            assertEquals(listOf(8_000L), awaitItem())
            repository.saveIfBetter(4, 6_000L)
            assertEquals(listOf(6_000L, 8_000L), awaitItem())
        }
    }
}
