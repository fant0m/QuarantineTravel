package com.example.quarantinetravel.util

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import com.example.quarantinetravel.R

class SfxManager {
    companion object {
        private var soundPool: SoundPool? = null
        private var sm: IntArray? = null
        var sfxEnabled: Boolean = true

        fun init(context: Context, sfxEnabled: Boolean) {
            this.sfxEnabled = sfxEnabled
            val maxStreams = 2
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                soundPool = SoundPool.Builder()
                    .setMaxStreams(maxStreams)
                    .build()
            } else {
                soundPool = SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0)
            }

            sm = IntArray(4)

            sm!![context.resources.getInteger(R.integer.sfx_click)] = soundPool!!.load(context, R.raw.click, 1)
            sm!![context.resources.getInteger(R.integer.sfx_success)] = soundPool!!.load(context, R.raw.success, 1)
            sm!![context.resources.getInteger(R.integer.sfx_error)] = soundPool!!.load(context, R.raw.error, 1)
            sm!![context.resources.getInteger(R.integer.sfx_tada)] = soundPool!!.load(context, R.raw.tada, 1)
        }

        fun play(sound: Int) {
            if (sfxEnabled) {
                soundPool!!.play(
                    sm!![sound],
                    1f,
                    1f,
                    1,
                    0,
                    1f
                )
            }
        }

        fun release() {
            sm = null
            soundPool?.let {
                it.release()
                soundPool = null
            }
        }
    }

}