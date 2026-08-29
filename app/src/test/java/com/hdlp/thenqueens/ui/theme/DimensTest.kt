package com.hdlp.thenqueens.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class DimensTest {

    @Test
    fun `compact keeps the current phone values and no width cap`() {
        val dimens = dimensFor(WindowWidthClass.Compact)
        assertEquals(4.dp, dimens.spacingXs)
        assertEquals(8.dp, dimens.spacingS)
        assertEquals(16.dp, dimens.spacingM)
        assertEquals(24.dp, dimens.spacingL)
        assertEquals(32.dp, dimens.spacingXl)
        assertEquals(Dp.Infinity, dimens.contentMaxWidth)
        assertEquals(200.dp, dimens.headerMaxHeight)
        assertEquals(0.25f, dimens.headerMaxHeightFraction, 0f)
    }

    @Test
    fun `medium only caps content width`() {
        val expected = dimensFor(WindowWidthClass.Compact).copy(contentMaxWidth = 600.dp)
        assertEquals(expected, dimensFor(WindowWidthClass.Medium))
    }

    @Test
    fun `expanded caps content width and widens screen spacing`() {
        val dimens = dimensFor(WindowWidthClass.Expanded)
        assertEquals(600.dp, dimens.contentMaxWidth)
        assertEquals(24.dp, dimens.spacingM)
        assertEquals(32.dp, dimens.spacingL)
        assertEquals(40.dp, dimens.spacingXl)
    }
}
