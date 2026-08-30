package com.hdlp.thenqueens.ui.game

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.hdlp.thenqueens.R
import com.hdlp.thenqueens.domain.BoardPosition
import com.hdlp.thenqueens.ui.theme.DarkSquare
import com.hdlp.thenqueens.ui.theme.HintColor
import com.hdlp.thenqueens.ui.theme.LightSquare
import com.hdlp.thenqueens.ui.theme.QueenColor

@Composable
fun Board(
    boardSize: Int,
    queens: Set<BoardPosition>,
    conflicts: Set<BoardPosition>,
    hintPosition: BoardPosition?,
    onCellTapped: (BoardPosition) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier.aspectRatio(1f)) {
        // Every cell is the same size, so the glyph size is measured once here instead
        // of with a BoxWithConstraints per cell (n² subcompositions).
        val queenFontSize = with(LocalDensity.current) { (maxWidth / boardSize * 0.6f).toSp() }
        Column(Modifier.fillMaxSize()) {
            repeat(boardSize) { row ->
                Row(Modifier.weight(1f)) {
                    repeat(boardSize) { column ->
                        val position = BoardPosition(row, column)
                        BoardCell(
                            position = position,
                            hasQueen = position in queens,
                            isConflicting = position in conflicts,
                            isHint = position == hintPosition,
                            queenFontSize = queenFontSize,
                            onTapped = { onCellTapped(position) },
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BoardCell(
    position: BoardPosition,
    hasQueen: Boolean,
    isConflicting: Boolean,
    isHint: Boolean,
    queenFontSize: TextUnit,
    onTapped: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLightSquare = (position.row + position.column) % 2 == 0
    val description =
        buildString {
            append(stringResource(R.string.cell_description, position.row + 1, position.column + 1))
            if (hasQueen) append(stringResource(R.string.cell_queen_suffix))
            if (isConflicting) append(stringResource(R.string.cell_conflict_suffix))
            if (isHint) append(stringResource(R.string.cell_hint_suffix))
        }
    val errorColor = MaterialTheme.colorScheme.error

    Box(
        modifier =
            modifier
                .background(if (isLightSquare) LightSquare else DarkSquare)
                .then(
                    when {
                        isConflicting -> Modifier.border(2.dp, errorColor)
                        isHint -> Modifier.border(2.dp, HintColor)
                        else -> Modifier
                    },
                )
                .clickable { onTapped() }
                .semantics {
                    contentDescription = description
                    role = Role.Button
                },
        contentAlignment = Alignment.Center,
    ) {
        val queenScale by animateFloatAsState(
            targetValue = if (hasQueen) 1f else 0f,
            animationSpec =
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
            label = "queenScale",
        )
        if (queenScale > 0.01f) {
            Text(
                text = "♛",
                fontSize = queenFontSize,
                color = if (isConflicting) errorColor else QueenColor,
                modifier =
                    Modifier.graphicsLayer {
                        scaleX = queenScale
                        scaleY = queenScale
                    },
            )
        }
        // Ghost glyph so the suggestion reads by shape, not border color alone.
        if (isHint && !hasQueen) {
            Text(
                text = "♛",
                fontSize = queenFontSize,
                color = HintColor.copy(alpha = 0.65f),
            )
        }
    }
}
