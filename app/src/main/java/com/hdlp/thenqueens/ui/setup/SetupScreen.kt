package com.hdlp.thenqueens.ui.setup

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hdlp.thenqueens.R
import com.hdlp.thenqueens.domain.NQueensRules
import com.hdlp.thenqueens.ui.theme.DarkSquare
import com.hdlp.thenqueens.ui.theme.LightSquare
import com.hdlp.thenqueens.ui.theme.QueenColor
import kotlin.math.ceil

const val MAX_PRESET_SIZE = 12

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SetupScreen(
    onStart: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedSize by rememberSaveable { mutableIntStateOf(8) }

    Column(modifier.fillMaxSize()) {
        ChessHeader(Modifier.fillMaxWidth())
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                stringResource(R.string.setup_title),
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.setup_subtitle, selectedSize),
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(24.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            ) {
                (NQueensRules.MIN_BOARD_SIZE..MAX_PRESET_SIZE).forEach { size ->
                    FilterChip(
                        selected = size == selectedSize,
                        onClick = { selectedSize = size },
                        label = { Text(stringResource(R.string.board_size_option, size)) },
                    )
                }
            }
            Spacer(Modifier.height(32.dp))
            Button(onClick = { onStart(selectedSize) }) {
                Text(stringResource(R.string.start_game))
            }
        }
    }
}

// The header keeps the board's own palette in both themes, the way a physical
// chessboard looks the same at night; the scrim keeps title and status bar readable.
@Composable
private fun ChessHeader(modifier: Modifier = Modifier) {
    Box(modifier = modifier.height(200.dp)) {
        Canvas(Modifier.matchParentSize()) {
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
            drawRect(
                brush =
                    Brush.verticalGradient(
                        0f to QueenColor.copy(alpha = 0.50f),
                        0.35f to Color.Transparent,
                        0.55f to Color.Transparent,
                        1f to QueenColor.copy(alpha = 0.88f),
                    ),
            )
        }
        Text(
            text = "♛",
            fontSize = 96.sp,
            color = QueenColor.copy(alpha = 0.55f),
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 28.dp)
                    .graphicsLayer { rotationZ = -10f },
        )
        Text(
            text = stringResource(R.string.header_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = LightSquare,
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 24.dp, vertical = 18.dp),
        )
    }
}
