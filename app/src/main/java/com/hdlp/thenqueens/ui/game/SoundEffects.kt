package com.hdlp.thenqueens.ui.game

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.hdlp.thenqueens.R

class SoundEffectsPlayer(context: Context) {
    private val soundPool =
        SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build(),
            )
            .build()

    private val placeId = soundPool.load(context, R.raw.queen_place, 1)
    private val conflictId = soundPool.load(context, R.raw.queen_conflict, 1)
    private val victoryId = soundPool.load(context, R.raw.game_victory, 1)

    fun play(effect: GameEffect) {
        val soundId =
            when (effect) {
                GameEffect.QueenPlaced -> placeId
                GameEffect.ConflictCreated -> conflictId
                GameEffect.Victory -> victoryId
            }
        soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
    }

    fun release() = soundPool.release()
}

@Composable
fun rememberSoundEffectsPlayer(): SoundEffectsPlayer {
    val context = LocalContext.current.applicationContext
    val player = remember { SoundEffectsPlayer(context) }
    DisposableEffect(player) {
        onDispose { player.release() }
    }
    return player
}
