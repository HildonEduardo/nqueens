package com.hdlp.thenqueens.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreBestTimeRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : BestTimeRepository {

    override fun observeBestTime(boardSize: Int): Flow<Long?> =
        dataStore.data.map { it[key(boardSize)] }

    override suspend fun saveIfBetter(boardSize: Int, elapsedMillis: Long) {
        dataStore.edit { preferences ->
            val current = preferences[key(boardSize)]
            if (current == null || elapsedMillis < current) {
                preferences[key(boardSize)] = elapsedMillis
            }
        }
    }

    private fun key(boardSize: Int) = longPreferencesKey("best_time_$boardSize")
}
