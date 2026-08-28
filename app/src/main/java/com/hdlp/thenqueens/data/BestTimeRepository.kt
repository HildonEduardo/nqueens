package com.hdlp.thenqueens.data

import kotlinx.coroutines.flow.Flow

interface BestTimeRepository {
    fun observeBestTime(boardSize: Int): Flow<Long?>
    suspend fun saveIfBetter(boardSize: Int, elapsedMillis: Long)
}
