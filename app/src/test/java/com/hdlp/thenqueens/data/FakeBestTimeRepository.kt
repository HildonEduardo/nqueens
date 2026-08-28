package com.hdlp.thenqueens.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeBestTimeRepository : BestTimeRepository {

    val bestTimes = MutableStateFlow<Map<Int, Long>>(emptyMap())
    val saveCalls = mutableListOf<Pair<Int, Long>>()

    override fun observeBestTime(boardSize: Int): Flow<Long?> =
        bestTimes.map { it[boardSize] }

    override suspend fun saveIfBetter(boardSize: Int, elapsedMillis: Long) {
        saveCalls += boardSize to elapsedMillis
        bestTimes.update { current ->
            val existing = current[boardSize]
            if (existing == null || elapsedMillis < existing) {
                current + (boardSize to elapsedMillis)
            } else {
                current
            }
        }
    }
}
