package com.hdlp.thenqueens.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Material's canonical window breakpoints, owned here so the design system stays the
// single source of truth for adaptivity — no library dependency.
val WidthMediumBreakpoint = 600.dp
val WidthExpandedBreakpoint = 840.dp
val HeightMediumBreakpoint = 480.dp
val HeightExpandedBreakpoint = 900.dp

enum class WindowWidthClass { Compact, Medium, Expanded }

enum class WindowHeightClass { Compact, Medium, Expanded }

data class WindowSizeClasses(
    val width: WindowWidthClass,
    val height: WindowHeightClass,
)

fun windowWidthClass(width: Dp): WindowWidthClass = when {
    width < WidthMediumBreakpoint -> WindowWidthClass.Compact
    width < WidthExpandedBreakpoint -> WindowWidthClass.Medium
    else -> WindowWidthClass.Expanded
}

fun windowHeightClass(height: Dp): WindowHeightClass = when {
    height < HeightMediumBreakpoint -> WindowHeightClass.Compact
    height < HeightExpandedBreakpoint -> WindowHeightClass.Medium
    else -> WindowHeightClass.Expanded
}

val LocalWindowSizeClasses = compositionLocalOf {
    WindowSizeClasses(WindowWidthClass.Compact, WindowHeightClass.Medium)
}

// A landscape phone is Expanded by width but still a phone: a compact height cannot
// absorb large-screen spacing and type, so token and type choices fall back to Compact.
fun tokenWidthClass(width: WindowWidthClass, height: WindowHeightClass): WindowWidthClass =
    if (height == WindowHeightClass.Compact) WindowWidthClass.Compact else width
