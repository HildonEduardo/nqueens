package com.hdlp.thenqueens.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

fun fontScaleFor(width: WindowWidthClass): Float = when (width) {
    WindowWidthClass.Compact -> 1f
    WindowWidthClass.Medium -> 1.05f
    WindowWidthClass.Expanded -> 1.15f
}

fun Typography.scaledBy(factor: Float): Typography =
    if (factor == 1f) {
        this
    } else {
        Typography(
            displayLarge = displayLarge.scaledBy(factor),
            displayMedium = displayMedium.scaledBy(factor),
            displaySmall = displaySmall.scaledBy(factor),
            headlineLarge = headlineLarge.scaledBy(factor),
            headlineMedium = headlineMedium.scaledBy(factor),
            headlineSmall = headlineSmall.scaledBy(factor),
            titleLarge = titleLarge.scaledBy(factor),
            titleMedium = titleMedium.scaledBy(factor),
            titleSmall = titleSmall.scaledBy(factor),
            bodyLarge = bodyLarge.scaledBy(factor),
            bodyMedium = bodyMedium.scaledBy(factor),
            bodySmall = bodySmall.scaledBy(factor),
            labelLarge = labelLarge.scaledBy(factor),
            labelMedium = labelMedium.scaledBy(factor),
            labelSmall = labelSmall.scaledBy(factor),
        )
    }

// Unspecified units must pass through untouched — TextUnit arithmetic throws on them.
private fun TextStyle.scaledBy(factor: Float): TextStyle = copy(
    fontSize = if (fontSize.isSp) fontSize * factor else fontSize,
    lineHeight = if (lineHeight.isSp) lineHeight * factor else lineHeight,
)
