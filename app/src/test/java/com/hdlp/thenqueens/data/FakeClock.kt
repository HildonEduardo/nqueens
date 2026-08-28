package com.hdlp.thenqueens.data

class FakeClock(var nowMillis: Long = 0L) : GameClock {
    override fun elapsedRealtimeMillis(): Long = nowMillis
}
