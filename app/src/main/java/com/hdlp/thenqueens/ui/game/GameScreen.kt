package com.hdlp.thenqueens.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdlp.thenqueens.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    onChangeSize: () -> Unit,
    viewModel: GameViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(formatElapsed(state.elapsedMillis)) },
                actions = {
                    TextButton(onClick = { viewModel.onAction(GameAction.ResetClicked) }) {
                        Text(stringResource(R.string.reset))
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    stringResource(R.string.queens_left, state.queensLeft),
                    style = MaterialTheme.typography.titleMedium,
                )
                state.bestTimeMillis?.let {
                    Text(
                        stringResource(R.string.best_time, formatElapsed(it)),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
            Board(
                boardSize = state.boardSize,
                queens = state.queens,
                conflicts = state.conflicts,
                onCellTapped = { viewModel.onAction(GameAction.CellTapped(it)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
