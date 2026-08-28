package com.hdlp.thenqueens.data

import kotlinx.coroutines.flow.Flow

interface BestTimeRepository {
    fun observeBestTime(boardSize: Int): Flow<Long?>
    fun observeTopTimes(boardSize: Int): Flow<List<Long>>
    suspend fun saveIfBetter(boardSize: Int, elapsedMillis: Long)

    companion object {
        const val TOP_TIMES_COUNT = 3
    }
}
