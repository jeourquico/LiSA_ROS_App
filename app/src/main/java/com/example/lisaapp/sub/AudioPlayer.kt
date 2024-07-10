package com.example.lisaapp.sub

import android.content.Context
import android.media.MediaPlayer

class AudioPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    fun playAudio(resId: Int) {
        releaseMediaPlayer() // Ensure any existing player is released before creating a new one

        mediaPlayer = MediaPlayer.create(context, resId).apply {
            setOnCompletionListener {
                releaseMediaPlayer()
            }
            start()
        }
    }

    private fun stopAudio() {
        releaseMediaPlayer()
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun onStop() {
        stopAudio()
    }
}
