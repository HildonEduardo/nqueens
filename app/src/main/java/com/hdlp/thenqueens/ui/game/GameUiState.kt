package com.hdlp.thenqueens.ui.game

import com.hdlp.thenqueens.domain.BoardPosition
import com.hdlp.thenqueens.domain.GameStatus

data class GameUiState(
    val boardSize: Int,
    val queens: Set<BoardPosition> = emptySet(),
    val conflicts: Set<BoardPosition> = emptySet(),
    val status: GameStatus = GameStatus.NOT_STARTED,
    val elapsedMillis: Long = 0L,
    val bestTimeMillis: Long? = null,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val hintPosition: BoardPosition? = null,
) {
    val queensLeft: Int get() = boardSize - queens.size
    val isSolved: Boolean get() = status == GameStatus.SOLVED
}
