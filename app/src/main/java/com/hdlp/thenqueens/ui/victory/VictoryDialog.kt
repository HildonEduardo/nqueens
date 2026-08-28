package com.hdlp.thenqueens.ui.victory

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hdlp.thenqueens.R
import com.hdlp.thenqueens.ui.game.formatElapsed

@Composable
fun VictoryDialog(
    elapsedMillis: Long,
    bestTimeMillis: Long?,
    onPlayAgain: () -> Unit,
    onChangeSize: () -> Unit,
    onDismiss: () -> Unit,
) {
    val isNewBest = bestTimeMillis != null && elapsedMillis <= bestTimeMillis

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            var shown by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { shown = true }
            val trophyScale by animateFloatAsState(
                targetValue = if (shown) 1f else 0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "trophyScale",
            )
            Text(
                text = "🏆",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.graphicsLayer {
                    scaleX = trophyScale
                    scaleY = trophyScale
                },
            )
        },
        title = { Text(stringResource(R.string.victory_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stringResource(R.string.victory_time, formatElapsed(elapsedMillis)))
                if (isNewBest) {
                    Text(
                        stringResource(R.string.victory_new_best),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                    )
                } else if (bestTimeMillis != null) {
                    Text(stringResource(R.string.victory_best, formatElapsed(bestTimeMillis)))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onPlayAgain) { Text(stringResource(R.string.play_again)) }
        },
        dismissButton = {
            TextButton(onClick = onChangeSize) { Text(stringResource(R.string.change_size)) }
        },
    )
}
