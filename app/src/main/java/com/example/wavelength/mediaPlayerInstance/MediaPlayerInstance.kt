package com.example.wavelength.mediaPlayerInstance

import android.media.MediaPlayer

class MediaPlayerInstance {
    private lateinit var mp: MediaPlayer

    fun getInstance(): MediaPlayer {
        if (mp == null) {
            mp = MediaPlayer()
        }
        return mp
    }
}