package com.hdlp.thenqueens.ui.game

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.unit.dp
import com.hdlp.thenqueens.R
import com.hdlp.thenqueens.domain.BoardPosition
import com.hdlp.thenqueens.ui.theme.DarkSquare
import com.hdlp.thenqueens.ui.theme.LightSquare
import com.hdlp.thenqueens.ui.theme.QueenColor

@Composable
fun Board(
    boardSize: Int,
    queens: Set<BoardPosition>,
    conflicts: Set<BoardPosition>,
    onCellTapped: (BoardPosition) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.aspectRatio(1f)) {
        repeat(boardSize) { row ->
            Row(Modifier.weight(1f)) {
                repeat(boardSize) { column ->
                    val position = BoardPosition(row, column)
                    BoardCell(
                        position = position,
                        hasQueen = position in queens,
                        isConflicting = position in conflicts,
                        onTapped = { onCellTapped(position) },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    )
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
    onTapped: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLightSquare = (position.row + position.column) % 2 == 0
    val description =
        buildString {
            append(stringResource(R.string.cell_description, position.row + 1, position.column + 1))
            if (hasQueen) append(stringResource(R.string.cell_queen_suffix))
            if (isConflicting) append(stringResource(R.string.cell_conflict_suffix))
        }
    val errorColor = MaterialTheme.colorScheme.error

    BoxWithConstraints(
        modifier =
            modifier
                .background(if (isLightSquare) LightSquare else DarkSquare)
                .then(if (isConflicting) Modifier.border(2.dp, errorColor) else Modifier)
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
            val queenFontSize = with(LocalDensity.current) { (maxWidth * 0.6f).toSp() }
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
    }
}
