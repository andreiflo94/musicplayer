package com.example.musicplayer.domain

import androidx.media3.common.MediaItem
import com.example.musicplayer.domain.model.Track
import kotlinx.coroutines.flow.StateFlow

sealed class MediaControllerEvent {
    object Idle : MediaControllerEvent()
    data class IsPlayingChanged(val isPlaying: Boolean) : MediaControllerEvent()
    data class MediaItemTransition(val mediaItem: MediaItem?, val reason: Int) :
        MediaControllerEvent()

    data class PlaybackStateChanged(val playbackState: Int) : MediaControllerEvent()
}


interface MediaControllerManager {

    val mediaControllerEvents: StateFlow<MediaControllerEvent>

    fun init()

    fun release()

    fun isCurrentlyPlaying(): Boolean

    fun currentPlayingPosition(): Long

    fun currentTrackDuration(): Long

    fun getCurrentPlayingTrackName(): String

    fun getCurrentPlayingTrackPath(): String

    fun setCurrentTrackPlayingIndex()

    fun startAudioPlayback(trackList: List<Track>, index: Int)

    fun playPauseClick()

    fun hasNextMediaItem(): Boolean

    fun seekToNextMediaItem()

    fun seekToPreviousMediaItem(progress: Long)

    fun stopPlaying()

    fun onProgressUpdate(progress: Long)
}