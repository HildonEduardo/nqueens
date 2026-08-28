package com.hdlp.thenqueens.ui.game

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeFormatTest {

    @Test
    fun `formats zero, sub-minute, and over-a-minute times`() {
        assertEquals("0:00", formatElapsed(0L))
        assertEquals("0:07", formatElapsed(7_499L))
        assertEquals("1:05", formatElapsed(65_000L))
        assertEquals("10:00", formatElapsed(600_000L))
    }
}
