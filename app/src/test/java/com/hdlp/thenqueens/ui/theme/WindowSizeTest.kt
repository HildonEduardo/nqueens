package com.hdlp.thenqueens.ui.theme

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class WindowSizeTest {

    @Test
    fun `width classes split at the material breakpoints`() {
        assertEquals(WindowWidthClass.Compact, windowWidthClass(0.dp))
        assertEquals(WindowWidthClass.Compact, windowWidthClass(599.dp))
        assertEquals(WindowWidthClass.Medium, windowWidthClass(600.dp))
        assertEquals(WindowWidthClass.Medium, windowWidthClass(839.dp))
        assertEquals(WindowWidthClass.Expanded, windowWidthClass(840.dp))
        assertEquals(WindowWidthClass.Expanded, windowWidthClass(1920.dp))
    }

    @Test
    fun `height classes split at the material breakpoints`() {
        assertEquals(WindowHeightClass.Compact, windowHeightClass(0.dp))
        assertEquals(WindowHeightClass.Compact, windowHeightClass(479.dp))
        assertEquals(WindowHeightClass.Medium, windowHeightClass(480.dp))
        assertEquals(WindowHeightClass.Medium, windowHeightClass(899.dp))
        assertEquals(WindowHeightClass.Expanded, windowHeightClass(900.dp))
    }

    @Test
    fun `token width class falls back to compact when height is compact`() {
        assertEquals(
            WindowWidthClass.Compact,
            tokenWidthClass(WindowWidthClass.Expanded, WindowHeightClass.Compact),
        )
        assertEquals(
            WindowWidthClass.Expanded,
            tokenWidthClass(WindowWidthClass.Expanded, WindowHeightClass.Medium),
        )
        assertEquals(
            WindowWidthClass.Compact,
            tokenWidthClass(WindowWidthClass.Compact, WindowHeightClass.Expanded),
        )
    }
}
