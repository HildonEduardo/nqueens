package com.hdlp.thenqueens.ui.game

fun formatElapsed(millis: Long): String {
    val totalSeconds = millis / 1000
    return "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)
}
