package com.example.fouroperations.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.fouroperations.R

class SoundManager(context: Context) {

    private val soundPool: SoundPool
    private val correctId: Int
    private val wrongId: Int

    init {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(attrs)
            .build()

        correctId = soundPool.load(context, R.raw.correct, 1)
        wrongId = soundPool.load(context, R.raw.wrong, 1)
    }

    fun playCorrect() {
        soundPool.play(correctId, 1f, 1f, 1, 0, 1f)
    }

    fun playWrong() {
        soundPool.play(wrongId, 1f, 1f, 1, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}
