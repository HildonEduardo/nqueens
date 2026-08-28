package com.hdlp.thenqueens.ui.victory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
