package com.hdlp.thenqueens.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimens(
    val spacingXs: Dp,
    val spacingS: Dp,
    val spacingM: Dp,
    val spacingL: Dp,
    val spacingXl: Dp,
    val contentMaxWidth: Dp,
    val headerMaxHeight: Dp,
    val headerMaxHeightFraction: Float,
)

// Compact must equal the values screens hardcoded before tokens existed, so phones
// render pixel-identical to the pre-design-system app.
private val CompactDimens = Dimens(
    spacingXs = 4.dp,
    spacingS = 8.dp,
    spacingM = 16.dp,
    spacingL = 24.dp,
    spacingXl = 32.dp,
    contentMaxWidth = Dp.Infinity,
    headerMaxHeight = 200.dp,
    headerMaxHeightFraction = 0.25f,
)

fun dimensFor(width: WindowWidthClass): Dimens = when (width) {
    WindowWidthClass.Compact -> CompactDimens
    WindowWidthClass.Medium -> CompactDimens.copy(contentMaxWidth = 600.dp)
    WindowWidthClass.Expanded ->
        CompactDimens.copy(
            spacingM = 24.dp,
            spacingL = 32.dp,
            spacingXl = 40.dp,
            contentMaxWidth = 600.dp,
        )
}

val LocalDimens = compositionLocalOf { CompactDimens }
