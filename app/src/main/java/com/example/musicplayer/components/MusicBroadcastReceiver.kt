package com.example.musicplayer.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MusicBroadcastReceiver(private val listener: MusicBroadcastListener) : BroadcastReceiver() {

    companion object{
        const val UPDATE_PLAYBACK_STATUS = "update_playback_status"
        const val IS_PLAYING = "IS_PLAYING"
        const val PLAYBACK_PROGRESS = "PLAYBACK_PROGRESS"
        const val PLAYBACK_TOTAL_DURATION = "PLAYBACK_TOTAL_DURATION"
    }

    interface MusicBroadcastListener {
        fun onMusicPlaying(isPlaying: Boolean)
        fun onMusicPlayingProgress(progress: Int, totalDuration: Int)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == UPDATE_PLAYBACK_STATUS && intent.hasExtra(IS_PLAYING)) {
            val isPlaying = intent.getBooleanExtra(IS_PLAYING, false)
            listener.onMusicPlaying(isPlaying)
        }
        if (intent?.action == UPDATE_PLAYBACK_STATUS && intent.hasExtra(PLAYBACK_PROGRESS)) {
            val progress = intent.getIntExtra(PLAYBACK_PROGRESS, 0)
            val totalDuration = intent.getIntExtra(PLAYBACK_TOTAL_DURATION, 0)
            listener.onMusicPlayingProgress(progress, totalDuration)
        }
    }
}
