package com.hdlp.thenqueens.ui.setup

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.core.view.WindowCompat
import com.hdlp.thenqueens.R
import com.hdlp.thenqueens.domain.NQueensRules
import com.hdlp.thenqueens.ui.preview.NQueensPreview
import com.hdlp.thenqueens.ui.preview.NQueensPreviewSurface
import com.hdlp.thenqueens.ui.theme.DarkSquare
import com.hdlp.thenqueens.ui.theme.LightSquare
import com.hdlp.thenqueens.ui.theme.NQueensTheme
import com.hdlp.thenqueens.ui.theme.QueenColor
import kotlin.math.ceil

const val MAX_PRESET_SIZE = 12

private val TitleScrimHeight = 64.dp
private val StatusScrimFade = 24.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SetupScreen(
    onStart: (Int) -> Unit,
    onLeaderboards: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedSize by rememberSaveable { mutableIntStateOf(8) }

    LightStatusBarIcons()

    BoxWithConstraints(modifier.fillMaxSize()) {
        val dimens = NQueensTheme.dimens
        val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        // The header bleeds under the status bar, so its height is measured on top of that
        // inset: what stays visible below the bar is the same at any status bar height.
        val headerHeight =
            min(dimens.headerMaxHeight, maxHeight * dimens.headerMaxHeightFraction) + statusBarHeight
        // The scroll makes the column's height unbounded, so the min height re-creates
        // what weight(1f) used to do: center the content in the leftover viewport when
        // it fits, and only scroll (e.g. in landscape) when it doesn't.
        val minContentHeight = (maxHeight - headerHeight).coerceAtLeast(0.dp)
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ChessHeader(
                height = headerHeight,
                statusBarHeight = statusBarHeight,
                modifier = Modifier.fillMaxWidth(),
            )
            Column(
                modifier =
                    Modifier
                        .widthIn(max = dimens.contentMaxWidth)
                        .fillMaxWidth()
                        .heightIn(min = minContentHeight)
                        .padding(dimens.spacingL),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    stringResource(R.string.setup_title),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Spacer(Modifier.height(dimens.spacingS))
                Text(
                    stringResource(R.string.setup_subtitle, selectedSize),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(dimens.spacingL))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimens.spacingS, Alignment.CenterHorizontally),
                ) {
                    (NQueensRules.MIN_BOARD_SIZE..MAX_PRESET_SIZE).forEach { size ->
                        FilterChip(
                            selected = size == selectedSize,
                            onClick = { selectedSize = size },
                            label = { Text(stringResource(R.string.board_size_option, size)) },
                        )
                    }
                }
                Spacer(Modifier.height(dimens.spacingXl))
                Button(onClick = { onStart(selectedSize) }) {
                    Text(stringResource(R.string.start_game))
                }
                Spacer(Modifier.height(dimens.spacingS))
                TextButton(onClick = onLeaderboards) {
                    Text(stringResource(R.string.leaderboard_title))
                }
            }
        }
    }
}

@Composable
private fun ChessHeader(
    height: Dp,
    statusBarHeight: Dp,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.height(height)) {
        // The last checker row overdraws past the header when its height is not a
        // multiple of the cell size; Canvas does not clip on its own.
        Canvas(Modifier.matchParentSize().clipToBounds()) {
            val cell = 40.dp.toPx()
            val cols = ceil(size.width / cell).toInt()
            val rows = ceil(size.height / cell).toInt()
            repeat(rows) { row ->
                repeat(cols) { column ->
                    drawRect(
                        color = if ((row + column) % 2 == 0) LightSquare else DarkSquare,
                        topLeft = Offset(column * cell, row * cell),
                        size = Size(cell, cell),
                    )
                }
            }
            // The status bar draws over the checkers, whose colors are fixed while the
            // system flips icon appearance with the theme; this band is tied to the inset
            // (not a fraction of the header) so the icons keep the same dark backdrop.
            val statusScrimHeight = statusBarHeight.toPx()
            drawRect(
                color = QueenColor.copy(alpha = 0.72f),
                size = Size(size.width, statusScrimHeight),
            )
            drawRect(
                brush =
                    Brush.verticalGradient(
                        0f to QueenColor.copy(alpha = 0.72f),
                        1f to Color.Transparent,
                        startY = statusScrimHeight,
                        endY = statusScrimHeight + StatusScrimFade.toPx(),
                    ),
                topLeft = Offset(0f, statusScrimHeight),
                size = Size(size.width, StatusScrimFade.toPx()),
            )
            // The decorative gradient scales with the header, so short headers compress
            // its dark band under the title; this band is fixed-height so the title
            // stays legible at any header size.
            val titleScrimTop = size.height - TitleScrimHeight.toPx()
            drawRect(
                brush =
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        1f to QueenColor.copy(alpha = 0.88f),
                        startY = titleScrimTop,
                        endY = size.height,
                    ),
                topLeft = Offset(0f, titleScrimTop),
                size = Size(size.width, TitleScrimHeight.toPx()),
            )
        }
        // Only the checkers bleed under the status bar; the title and glyph lay out
        // against the visible header, unchanged by the inset.
        Box(Modifier.matchParentSize().padding(top = statusBarHeight)) {
            // 0.48 reproduces the original 96sp glyph at the full 200dp header height.
            val queenFontSize = with(LocalDensity.current) { ((height - statusBarHeight) * 0.48f).toSp() }
            Text(
                text = "♛",
                fontSize = queenFontSize,
                color = QueenColor.copy(alpha = 0.55f),
                modifier =
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 28.dp)
                        .graphicsLayer { rotationZ = -10f },
            )
            Text(
                text = stringResource(R.string.header_title),
                style =
                    MaterialTheme.typography.headlineMedium.copy(
                        shadow = Shadow(color = QueenColor, offset = Offset(0f, 2f), blurRadius = 8f),
                    ),
                fontWeight = FontWeight.Bold,
                color = LightSquare,
                modifier =
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 24.dp, vertical = 18.dp),
            )
        }
    }
}

// The hero's checkers are fixed colors, so the system's theme-driven icon appearance can
// land light-on-light there; the status scrim is always dark, so pin light icons for as
// long as this screen is on top.
@Composable
private fun LightStatusBarIcons() {
    val activity = LocalActivity.current ?: return
    DisposableEffect(activity) {
        val window = activity.window
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        val previous = controller.isAppearanceLightStatusBars
        controller.isAppearanceLightStatusBars = false
        onDispose { controller.isAppearanceLightStatusBars = previous }
    }
}

@NQueensPreview
@Composable
private fun SetupScreenPreview() {
    NQueensPreviewSurface {
        SetupScreen(onStart = {}, onLeaderboards = {})
    }
}
