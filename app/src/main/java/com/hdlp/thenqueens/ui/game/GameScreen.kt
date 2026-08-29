package com.hdlp.thenqueens.ui.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.min
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdlp.thenqueens.R
import com.hdlp.thenqueens.domain.BoardPosition
import com.hdlp.thenqueens.domain.GameStatus
import com.hdlp.thenqueens.ui.preview.NQueensPreview
import com.hdlp.thenqueens.ui.preview.NQueensPreviewSurface
import com.hdlp.thenqueens.ui.theme.NQueensTheme
import com.hdlp.thenqueens.ui.victory.VictoryDialog

@Composable
fun GameScreen(
    onNavigateBack: () -> Unit,
    viewModel: GameViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val soundPlayer = rememberSoundEffectsPlayer()
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { soundPlayer.play(it) }
    }

    var showGiveUpDialog by rememberSaveable { mutableStateOf(false) }
    val isRunning = state.status == GameStatus.IN_PROGRESS
    val onBackClicked = {
        if (isRunning) {
            viewModel.onAction(GameAction.PauseRequested)
            showGiveUpDialog = true
        } else {
            onNavigateBack()
        }
    }
    BackHandler(enabled = isRunning, onBack = onBackClicked)

    GameScreenContent(
        state = state,
        onAction = viewModel::onAction,
        onBackClicked = onBackClicked,
    )

    // Dialogs must close before the pop, or they linger over the exit transition.
    if (showGiveUpDialog) {
        GiveUpDialog(
            onGiveUp = {
                showGiveUpDialog = false
                onNavigateBack()
            },
            onKeepPlaying = {
                showGiveUpDialog = false
                viewModel.onAction(GameAction.ResumeRequested)
            },
        )
    }

    var victoryDismissed by rememberSaveable(state.status) { mutableStateOf(false) }
    if (state.isSolved && !victoryDismissed) {
        VictoryDialog(
            elapsedMillis = state.elapsedMillis,
            bestTimeMillis = state.bestTimeMillis,
            onPlayAgain = { viewModel.onAction(GameAction.PlayAgainClicked) },
            onChangeSize = {
                victoryDismissed = true
                onNavigateBack()
            },
            onDismiss = { victoryDismissed = true },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameScreenContent(
    state: GameUiState,
    onAction: (GameAction) -> Unit,
    onBackClicked: () -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val dimens = NQueensTheme.dimens
        // A wide viewport trades the top bar for a side panel: the board gets the full
        // height, and the stats fill the otherwise-empty flank at display type sizes.
        val isWide = maxWidth > maxHeight
        Scaffold(
            topBar = {
                if (!isWide) {
                    TopAppBar(
                        navigationIcon = { BackButton(onBackClicked) },
                        title = { Text(formatElapsed(state.elapsedMillis)) },
                        actions = {
                            TextButton(onClick = { onAction(GameAction.ResetClicked) }) {
                                Text(stringResource(R.string.reset))
                            }
                        },
                    )
                }
            },
        ) { padding ->
            if (isWide) {
                WideGameContent(
                    state = state,
                    onAction = onAction,
                    onBackClicked = onBackClicked,
                    modifier = Modifier.padding(padding).fillMaxSize().padding(dimens.spacingS),
                )
            } else {
                TallGameContent(
                    state = state,
                    onAction = onAction,
                    modifier = Modifier.padding(padding).fillMaxSize().padding(dimens.spacingM),
                )
            }
        }
    }
}

@Composable
private fun TallGameContent(
    state: GameUiState,
    onAction: (GameAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier) {
        val dimens = NQueensTheme.dimens
        val boardSide = min(maxWidth, maxHeight)
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimens.spacingM),
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
                onCellTapped = { onAction(GameAction.CellTapped(it)) },
                modifier = Modifier.width(boardSide),
            )
        }
    }
}

@Composable
private fun WideGameContent(
    state: GameUiState,
    onAction: (GameAction) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = NQueensTheme.dimens
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimens.spacingM),
    ) {
        BoxWithConstraints(Modifier.weight(1f).fillMaxHeight()) {
            val boardSide = min(maxWidth, maxHeight)
            Board(
                boardSize = state.boardSize,
                queens = state.queens,
                conflicts = state.conflicts,
                onCellTapped = { onAction(GameAction.CellTapped(it)) },
                modifier = Modifier.width(boardSide).align(Alignment.Center),
            )
        }
        Column(Modifier.weight(1f).fillMaxHeight()) {
            BackButton(onBackClicked)
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(dimens.spacingM, Alignment.CenterVertically),
            ) {
                Text(
                    formatElapsed(state.elapsedMillis),
                    style = MaterialTheme.typography.displayLarge,
                )
                Text(
                    stringResource(R.string.queens_left, state.queensLeft),
                    style = MaterialTheme.typography.headlineMedium,
                )
                state.bestTimeMillis?.let {
                    Text(
                        stringResource(R.string.best_time, formatElapsed(it)),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                FilledTonalButton(onClick = { onAction(GameAction.ResetClicked) }) {
                    Icon(Icons.Filled.Refresh, contentDescription = null)
                    Spacer(Modifier.width(dimens.spacingS))
                    Text(
                        stringResource(R.string.reset),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.back),
        )
    }
}

@Composable
private fun GiveUpDialog(
    onGiveUp: () -> Unit,
    onKeepPlaying: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onKeepPlaying,
        title = { Text(stringResource(R.string.give_up_title)) },
        text = { Text(stringResource(R.string.give_up_message)) },
        confirmButton = {
            TextButton(onClick = onGiveUp) { Text(stringResource(R.string.give_up_confirm)) }
        },
        dismissButton = {
            TextButton(onClick = onKeepPlaying) { Text(stringResource(R.string.keep_playing)) }
        },
    )
}

@NQueensPreview
@Composable
private fun GameScreenPreview() {
    NQueensPreviewSurface {
        GameScreenContent(
            state = GameUiState(
                boardSize = 8,
                queens = setOf(
                    BoardPosition(row = 0, column = 0),
                    BoardPosition(row = 1, column = 4),
                    BoardPosition(row = 3, column = 1),
                    BoardPosition(row = 5, column = 2),
                    BoardPosition(row = 7, column = 2),
                ),
                conflicts = setOf(
                    BoardPosition(row = 5, column = 2),
                    BoardPosition(row = 7, column = 2),
                ),
                status = GameStatus.IN_PROGRESS,
                elapsedMillis = 83_456L,
                bestTimeMillis = 61_002L,
            ),
            onAction = {},
            onBackClicked = {},
        )
    }
}
