package com.example.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.musicplayer.mainfeature.presentation.ui.screens.AudioFileState

class MusicBroadcastReceiver(private val listener: MusicBroadcastListener) : BroadcastReceiver() {

    companion object {
        const val UPDATE_PLAYBACK_STATUS = "update_playback_status"
        const val AUDIO_STATE = "AUDIO_STATE"
        const val PLAYBACK_PROGRESS = "PLAYBACK_PROGRESS"
        const val PLAYBACK_TOTAL_DURATION = "PLAYBACK_TOTAL_DURATION"
        const val PLAYBACK_TITLE = "PLAYBACK_TITLE"
    }

    interface MusicBroadcastListener {
        fun onMusicPlaying(audioFileState: AudioFileState)
        fun onMusicPlayingProgress(progress: Int, totalDuration: Int, musicFileTitle: String)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == UPDATE_PLAYBACK_STATUS && intent.hasExtra(AUDIO_STATE)) {
            intent.getStringExtra(AUDIO_STATE)?.let {
                listener.onMusicPlaying(AudioFileState.valueOf(it))
            }
        }
        if (intent?.action == UPDATE_PLAYBACK_STATUS && intent.hasExtra(PLAYBACK_PROGRESS)) {
            val progress = intent.getIntExtra(PLAYBACK_PROGRESS, 0)
            val totalDuration = intent.getIntExtra(PLAYBACK_TOTAL_DURATION, 0)
            val musicFileTitle = intent.getStringExtra(PLAYBACK_TITLE) as String
            listener.onMusicPlayingProgress(progress, totalDuration, musicFileTitle)
        }
    }
}
