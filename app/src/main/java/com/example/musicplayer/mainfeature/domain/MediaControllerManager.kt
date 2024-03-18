package com.example.musicplayer.mainfeature.domain

import androidx.media3.common.MediaItem
import kotlinx.coroutines.flow.StateFlow

sealed class MediaControllerEvent {
    object Idle : MediaControllerEvent()
    data class IsPlayingChanged(val isPlaying: Boolean) : MediaControllerEvent()
    data class MediaItemTransition(val mediaItem: MediaItem?, val reason: Int) : MediaControllerEvent()
    data class PlaybackStateChanged(val playbackState: Int) : MediaControllerEvent()
}


interface MediaControllerManager {

    val mediaControllerEvents: StateFlow<MediaControllerEvent>

    fun init()

    fun release()

    fun currentPlayingPosition(): Long

    fun currentTrackDuration(): Long

    fun getCurrentTrackName(): String

    fun setCurrentTrackPlayingIndex()

    fun startAudioPlayback(trackList: List<String>, index: Int)

    fun playPauseClick()

    fun stopPlaying()

    fun onProgressUpdate(progress: Long)
}