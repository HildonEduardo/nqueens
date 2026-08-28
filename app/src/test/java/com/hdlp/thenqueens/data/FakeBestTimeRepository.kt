package com.hdlp.thenqueens.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeBestTimeRepository : BestTimeRepository {

    val topTimes = MutableStateFlow<Map<Int, List<Long>>>(emptyMap())
    val saveCalls = mutableListOf<Pair<Int, Long>>()

    override fun observeBestTime(boardSize: Int): Flow<Long?> =
        topTimes.map { it[boardSize]?.firstOrNull() }

    override fun observeTopTimes(boardSize: Int): Flow<List<Long>> =
        topTimes.map { it[boardSize].orEmpty() }

    override suspend fun saveIfBetter(boardSize: Int, elapsedMillis: Long) {
        saveCalls += boardSize to elapsedMillis
        topTimes.update { current ->
            val updated =
                (current[boardSize].orEmpty() + elapsedMillis)
                    .sorted()
                    .take(BestTimeRepository.TOP_TIMES_COUNT)
            current + (boardSize to updated)
        }
    }
}
