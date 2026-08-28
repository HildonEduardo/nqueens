package com.hdlp.thenqueens.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
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
}
