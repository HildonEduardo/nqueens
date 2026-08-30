package com.hdlp.thenqueens.ui.game

import com.hdlp.thenqueens.domain.BoardPosition

sealed interface GameAction {
    data class CellTapped(val position: BoardPosition) : GameAction
    data object UndoClicked : GameAction
    data object RedoClicked : GameAction
    data object ResetClicked : GameAction
    data object PlayAgainClicked : GameAction
    data object PauseRequested : GameAction
    data object ResumeRequested : GameAction
}
