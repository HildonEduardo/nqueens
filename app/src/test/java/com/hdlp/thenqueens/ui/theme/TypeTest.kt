package com.hdlp.thenqueens.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class TypeTest {

    @Test
    fun `identity scale returns the same typography instance`() {
        assertSame(Typography, Typography.scaledBy(1f))
    }

    @Test
    fun `scaling multiplies font size and line height`() {
        val scaled = Typography.scaledBy(1.15f)
        assertEquals(16f * 1.15f, scaled.bodyLarge.fontSize.value, 0.001f)
        assertTrue(scaled.bodyLarge.fontSize.isSp)
        assertEquals(24f * 1.15f, scaled.bodyLarge.lineHeight.value, 0.001f)
        assertTrue(scaled.bodyLarge.lineHeight.isSp)
    }

    @Test
    fun `font scale grows with the width class`() {
        assertEquals(1f, fontScaleFor(WindowWidthClass.Compact), 0f)
        assertEquals(1.05f, fontScaleFor(WindowWidthClass.Medium), 0f)
        assertEquals(1.15f, fontScaleFor(WindowWidthClass.Expanded), 0f)
    }
}
