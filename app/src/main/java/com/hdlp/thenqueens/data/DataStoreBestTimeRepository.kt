package com.hdlp.thenqueens.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hdlp.thenqueens.data.BestTimeRepository.Companion.TOP_TIMES_COUNT
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class DataStoreBestTimeRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : BestTimeRepository {

    override fun observeBestTime(boardSize: Int): Flow<Long?> =
        observeTopTimes(boardSize).map { it.firstOrNull() }.distinctUntilChanged()

    override fun observeTopTimes(boardSize: Int): Flow<List<Long>> =
        dataStore.data.map { it.topTimes(boardSize) }.distinctUntilChanged()

    override suspend fun saveIfBetter(boardSize: Int, elapsedMillis: Long) {
        dataStore.edit { preferences ->
            val updated =
                (preferences.topTimes(boardSize) + elapsedMillis)
                    .sorted()
                    .take(TOP_TIMES_COUNT)
            preferences[topTimesKey(boardSize)] = updated.joinToString(",")
        }
    }

    // Falls back to the single-best key that predates the leaderboard. Capping on read
    // keeps the top-N invariant even against data written with a larger cap.
    private fun Preferences.topTimes(boardSize: Int): List<Long> =
        this[topTimesKey(boardSize)]?.split(',')?.mapNotNull(String::toLongOrNull)?.take(TOP_TIMES_COUNT)
            ?: listOfNotNull(this[legacyBestTimeKey(boardSize)])

    private fun topTimesKey(boardSize: Int) = stringPreferencesKey("best_times_$boardSize")

    private fun legacyBestTimeKey(boardSize: Int) = longPreferencesKey("best_time_$boardSize")
}
