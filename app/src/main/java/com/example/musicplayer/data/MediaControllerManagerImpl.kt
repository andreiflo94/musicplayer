package com.example.musicplayer.data

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicplayer.PlaybackService
import com.example.musicplayer.domain.MediaControllerEvent
import com.example.musicplayer.domain.MediaControllerManager
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class MediaControllerManagerImpl @Inject constructor(@ApplicationContext private val context: Context) :
    MediaControllerManager {

    private val _mediaControllerEvents =
        MutableStateFlow<MediaControllerEvent>(MediaControllerEvent.Idle)
    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private lateinit var trackList: List<String>
    private var currentTrackIndex: Int = 0
    private val controller: MediaController?
        get() = if (controllerFuture.isDone) controllerFuture.get() else null

    override val mediaControllerEvents: StateFlow<MediaControllerEvent>
        get() = _mediaControllerEvents


    @UnstableApi
    override fun init() {
        Log.d("MediaControllerManager", "init")
        controllerFuture = MediaController.Builder(
            context, SessionToken(context, ComponentName(context, PlaybackService::class.java))
        ).buildAsync()
        controllerFuture.addListener({
            controller?.addListener(getMediaControllerListener())
        }, MoreExecutors.directExecutor())
    }

    override fun release() {
        Log.d("MediaControllerManager", "release")
        _mediaControllerEvents.value = MediaControllerEvent.Idle
        controllerFuture.let {
            MediaController.releaseFuture(it)
        }
    }

    override fun isCurrentlyPlaying(): Boolean {
        Log.d("MediaControllerManager", "isCurrentlyPlaying: ${controller?.isPlaying}")
        return controller?.isPlaying == true
    }

    override fun currentPlayingPosition() = controller?.currentPosition ?: 0

    override fun currentTrackDuration() = controller?.duration ?: 0

    override fun getCurrentPlayingTrackName(): String {
        Log.d("MediaControllerManager", "getCurrentTrackName: ")
        return trackList[currentTrackIndex].split("/").last()
    }

    override fun getCurrentPlayingTrackPath(): String {
        Log.d("MediaControllerManager", "getCurrentTrackPath: ")
        return trackList[currentTrackIndex]
    }

    override fun setCurrentTrackPlayingIndex() {
        Log.d("MediaControllerManager", "setCurrentTrackPlayingIndex: ")
        this.currentTrackIndex = controller?.currentMediaItemIndex ?: 0
    }

    override fun startAudioPlayback(trackList: List<String>, index: Int) {
        Log.d("MediaControllerManager", "startAudioPlayback: $trackList")
        setCurrentTrackList(trackList, index)
        val list: List<MediaItem> = trackList.map { track -> MediaItem.fromUri(track) }
        controller?.setMediaItems(list, index, 0L)
        controller?.prepare()
        controller?.play()
    }

    override fun playPauseClick() {
        Log.d("MediaControllerManager", "playPauseClick: ")
        if (controller?.isPlaying == true) {
            controller?.pause()
        } else {
            controller?.play()
        }
    }

    override fun hasNextMediaItem(): Boolean {
        Log.d("MediaControllerManager", "hasNextMediaItem: ")
        return controller?.hasNextMediaItem() == true
    }

    override fun seekToNextMediaItem() {
        Log.d("MediaControllerManager", "seekToNextMediaItem: ")
        controller?.seekToNextMediaItem()
    }

    override fun seekToPreviousMediaItem(progress: Long) {
        Log.d("MediaControllerManager", "seekToPreviousMediaItem: $progress")
        if (progress > 0) {
            controller?.seekTo(0)
        } else if (controller?.hasPreviousMediaItem() == true) {
            controller?.seekToPreviousMediaItem()
        } else {
            controller?.seekTo(0)
        }
    }

    override fun stopPlaying() {
        Log.d("MediaControllerManager", "stopPlaying: ")
        controller?.stop()
    }

    override fun onProgressUpdate(progress: Long) {
        Log.d("MediaControllerManager", "onProgressUpdate: $progress")
        val seekTo = ((controller?.duration!! * progress) / 100f).toLong()
        controller?.seekTo(seekTo)
    }

    private fun setCurrentTrackList(trackList: List<String>, index: Int) {
        Log.d("MediaControllerManager", "setCurrentTrackList: $trackList")
        this.trackList = trackList
        this.currentTrackIndex = index
    }

    private fun getMediaControllerListener() = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            Log.d("MediaControllerManager", "onIsPlayingChanged: $isPlaying")
            _mediaControllerEvents.value = MediaControllerEvent.IsPlayingChanged(isPlaying)
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            Log.d("MediaControllerManager", "onMediaItemTransition: $mediaItem")
            _mediaControllerEvents.value =
                MediaControllerEvent.MediaItemTransition(mediaItem, reason)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            Log.d("MediaControllerManager", "onPlaybackStateChanged: $playbackState")
            super.onPlaybackStateChanged(playbackState)
            _mediaControllerEvents.value = MediaControllerEvent.PlaybackStateChanged(playbackState)
        }
    }
}