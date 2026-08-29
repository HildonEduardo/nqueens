package com.hdlp.thenqueens.ui.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hdlp.thenqueens.R
import com.hdlp.thenqueens.ui.game.formatElapsed
import com.hdlp.thenqueens.ui.preview.NQueensPreview
import com.hdlp.thenqueens.ui.preview.NQueensPreviewSurface
import com.hdlp.thenqueens.ui.theme.LightSquare
import com.hdlp.thenqueens.ui.theme.NQueensTheme
import com.hdlp.thenqueens.ui.theme.QueenColor

private val MEDALS = listOf("🥇", "🥈", "🥉")

@Composable
fun LeaderboardScreen(
    onBack: () -> Unit,
    viewModel: LeaderboardViewModel = hiltViewModel(),
) {
    val entries by viewModel.entries.collectAsStateWithLifecycle()
    LeaderboardContent(entries = entries, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeaderboardContent(
    entries: List<LeaderboardEntry>,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.leaderboard_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        if (entries.isEmpty()) {
            EmptyLeaderboard(Modifier.padding(padding).fillMaxSize())
        } else {
            val dimens = NQueensTheme.dimens
            Box(Modifier.padding(padding).fillMaxSize()) {
                LazyColumn(
                    modifier =
                        Modifier
                            .align(Alignment.TopCenter)
                            .widthIn(max = dimens.contentMaxWidth)
                            .fillMaxWidth()
                            .fillMaxHeight(),
                    contentPadding = PaddingValues(dimens.spacingM),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(entries, key = { it.boardSize }) { entry ->
                        LeaderboardCard(entry)
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardCard(
    entry: LeaderboardEntry,
    modifier: Modifier = Modifier,
) {
    Card(modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BoardSizeBadge(entry.boardSize)
            Spacer(Modifier.width(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                entry.times.forEachIndexed { rank, timeMillis ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(MEDALS[rank], fontSize = 18.sp)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            formatElapsed(timeMillis),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BoardSizeBadge(
    boardSize: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(64.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(QueenColor),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            stringResource(R.string.board_size_option, boardSize),
            color = LightSquare,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun EmptyLeaderboard(modifier: Modifier = Modifier) {
    val dimens = NQueensTheme.dimens
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(dimens.spacingXl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "♛",
            fontSize = 72.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            stringResource(R.string.leaderboard_empty_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(Modifier.height(dimens.spacingXs))
        Text(
            stringResource(R.string.leaderboard_empty_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@NQueensPreview
@Composable
private fun LeaderboardScreenPreview() {
    NQueensPreviewSurface {
        LeaderboardContent(
            entries = listOf(
                LeaderboardEntry(boardSize = 4, times = listOf(9_800L, 12_400L, 15_100L)),
                LeaderboardEntry(boardSize = 6, times = listOf(48_000L, 61_500L)),
                LeaderboardEntry(boardSize = 8, times = listOf(83_456L)),
            ),
            onBack = {},
        )
    }
}

@NQueensPreview
@Composable
private fun EmptyLeaderboardPreview() {
    NQueensPreviewSurface {
        LeaderboardContent(entries = emptyList(), onBack = {})
    }
}

