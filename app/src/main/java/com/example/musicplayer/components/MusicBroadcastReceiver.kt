package com.example.musicplayer.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MusicBroadcastReceiver(private val listener: MusicBroadcastListener) : BroadcastReceiver() {

    companion object{
        const val UPDATE_PLAYBACK_STATUS = "update_playback_status"
        const val IS_PLAYING = "IS_PLAYING"
    }

    interface MusicBroadcastListener {
        fun onMusicPlaying(isPlaying: Boolean)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == UPDATE_PLAYBACK_STATUS) {
            val isPlaying = intent.getBooleanExtra(IS_PLAYING, false)
            listener.onMusicPlaying(isPlaying)
        }
    }
}
