package com.hdlp.thenqueens.ui.game

sealed interface GameEffect {
    data object QueenPlaced : GameEffect

    data object ConflictCreated : GameEffect

    data object Victory : GameEffect

    data object HintUnavailable : GameEffect
}
