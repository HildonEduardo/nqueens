package com.hdlp.thenqueens.ui.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdlp.thenqueens.data.BestTimeRepository
import com.hdlp.thenqueens.domain.NQueensRules
import com.hdlp.thenqueens.ui.setup.MAX_PRESET_SIZE
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class LeaderboardEntry(
    val boardSize: Int,
    val times: List<Long>,
)

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    bestTimeRepository: BestTimeRepository,
) : ViewModel() {

    val entries: StateFlow<List<LeaderboardEntry>> =
        combine(
            (NQueensRules.MIN_BOARD_SIZE..MAX_PRESET_SIZE).map { size ->
                bestTimeRepository.observeTopTimes(size).map { LeaderboardEntry(size, it) }
            },
        ) { all -> all.filter { it.times.isNotEmpty() } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
