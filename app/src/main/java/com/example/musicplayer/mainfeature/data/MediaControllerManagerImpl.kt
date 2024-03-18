package com.example.musicplayer.mainfeature.data

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicplayer.PlaybackService
import com.example.musicplayer.mainfeature.domain.MediaControllerEvent
import com.example.musicplayer.mainfeature.domain.MediaControllerManager
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class MediaControllerManagerImpl @Inject constructor(@ApplicationContext private val context: Context) : MediaControllerManager {

    private val _mediaControllerEvents = MutableStateFlow<MediaControllerEvent>(MediaControllerEvent.Idle)
    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private lateinit var trackList: List<String>
    private var currentTrackIndex: Int = 0
    private val controller: MediaController?
        get() = if (controllerFuture.isDone) controllerFuture.get() else null

    override val mediaControllerEvents: StateFlow<MediaControllerEvent>
        get() = _mediaControllerEvents


    @UnstableApi
    override fun init() {
        controllerFuture = MediaController.Builder(
            context, SessionToken(context, ComponentName(context, PlaybackService::class.java))
        ).buildAsync()
        controllerFuture.addListener({
            controller?.addListener(getMediaControllerListener())
        }, MoreExecutors.directExecutor())
    }

    override fun release() {
        controllerFuture.let {
            MediaController.releaseFuture(it)
        }
    }

    override fun currentPlayingPosition() = controller?.currentPosition ?: 0

    override fun currentTrackDuration() = controller?.duration ?: 0

    override fun getCurrentTrackName(): String {
        return trackList[currentTrackIndex].split("/").last()
    }

    override fun setCurrentTrackPlayingIndex() {
        this.currentTrackIndex = controller?.currentMediaItemIndex ?: 0
    }

    override fun startAudioPlayback(trackList: List<String>, index: Int) {
        setCurrentTrackList(trackList, index)
        val list: List<MediaItem> = trackList.map { track -> MediaItem.fromUri(track) }
        controller?.setMediaItems(list, index, 0L)
        controller?.prepare()
        controller?.play()
    }

    override fun playPauseClick() {
        if (controller?.isPlaying == true) {
            controller?.pause()
        } else {
            controller?.play()
        }
    }

    override fun stopPlaying() {
        controller?.stop()
    }

    override fun onProgressUpdate(progress: Long) {
        val seekTo = ((controller?.duration!! * progress) / 100f).toLong()
        controller?.seekTo(seekTo)
    }

    private fun setCurrentTrackList(trackList: List<String>, index: Int) {
        this.trackList = trackList
        this.currentTrackIndex = index
    }

    private fun getMediaControllerListener() = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            _mediaControllerEvents.value = MediaControllerEvent.IsPlayingChanged(isPlaying)
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            _mediaControllerEvents.value = MediaControllerEvent.MediaItemTransition(mediaItem, reason)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            _mediaControllerEvents.value = MediaControllerEvent.PlaybackStateChanged(playbackState)
        }
    }
}