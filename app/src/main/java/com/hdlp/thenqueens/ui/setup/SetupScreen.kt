package com.hdlp.thenqueens.ui.setup

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hdlp.thenqueens.R
import com.hdlp.thenqueens.domain.NQueensRules

const val MAX_PRESET_SIZE = 12

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SetupScreen(onStart: (Int) -> Unit, modifier: Modifier = Modifier) {
    var selectedSize by rememberSaveable { mutableIntStateOf(8) }

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(stringResource(R.string.setup_title), style = MaterialTheme.typography.headlineMedium)
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
