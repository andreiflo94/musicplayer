package com.example.musicplayer.mainfeature.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import com.example.musicplayer.utils.MediaControllerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@UnstableApi
class MainActivityViewModel @Inject constructor(private val mediaControllerManager: MediaControllerManager) : ViewModel() {

    private var updateTrackProgressJob: Job? = null

    private val _state = MutableStateFlow(
        AudioState(
            trackName = "",
            progress = 0.0f,
            isPlaying = false,
            stopped = true
        )
    )
    val audioState: StateFlow<AudioState> = _state

    private lateinit var trackList: List<String>
    private var index: Int = 0

    init {
        mediaControllerManager.init {
            getMediaController()?.apply {
                addListener(getMediaControllerListener())
            }
        }
    }

    override fun onCleared() {
        mediaControllerManager.release()
        super.onCleared()
    }

    fun setCurrentTrackList(trackList: List<String>, index: Int) {
        this.trackList = trackList
        this.index = index
    }

    fun getMediaController(): MediaController? {
        return mediaControllerManager.controller
    }

    private fun startOrResumeAudioPlayback() {
        getMediaController()?.let { mCont ->
            _state.value = _state.value.copy(
                trackName = getCurrentTrackName(),
                progress = calculateProgressValue(
                    mCont.currentPosition,
                    duration = mCont.duration
                ),
                isPlaying = true,
                stopped = false
            )
        }
    }

    private fun pauseAudioPlayback() {
        getMediaController()?.let { mCont ->
            _state.value = _state.value.copy(
                trackName = getCurrentTrackName(),
                progress = calculateProgressValue(
                    mCont.currentPosition,
                    duration = mCont.duration
                ),
                isPlaying = false,
                stopped = false
            )
        }
    }

    private fun updateProgressAudioPlayback() {
        getMediaController()?.let { mCont ->
            _state.value = _state.value.copy(
                trackName = getCurrentTrackName(),
                progress = calculateProgressValue(
                    mCont.currentPosition,
                    duration = mCont.duration
                ),
                isPlaying = true,
                stopped = false
            )
        }
    }

    private fun stopAudioPlayback() {
        _state.value = _state.value.copy(
            isPlaying = false,
            stopped = true
        )
    }

    private fun getCurrentTrackName(): String {
        return this.trackList[index].split("/").last()
    }

    private fun setCurrentTrackPlayingIndex(index: Int) {
        this.index = index
    }

    private fun startProgressUpdate() {
        updateTrackProgressJob = viewModelScope.launch {
            while (true) {
                delay(500)
                updateProgressAudioPlayback()
            }
        }
    }

    private fun stopProgressUpdate() {
        updateTrackProgressJob?.cancel()
        updateTrackProgressJob = null
    }

    private fun calculateProgressValue(currentProgress: Long, duration: Long): Float {
        return if (currentProgress > 0) ((currentProgress.toFloat() / duration.toFloat()) * 100f)
        else 0f
    }

    private fun getMediaControllerListener() = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            Log.d("KKKKK", "onIsPlayingChanged: " + isPlaying)
            if (isPlaying) {
                startOrResumeAudioPlayback()
                startProgressUpdate()
            } else {
                pauseAudioPlayback()
                stopProgressUpdate()
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            Log.d("KKKKK", "onMediaItemTransition ")
            getMediaController()?.let {
                setCurrentTrackPlayingIndex(it.currentMediaItemIndex)
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            Log.d("KKKKK", "onPlaybackStateChanged " + playbackState)
            when (playbackState) {

                Player.STATE_IDLE -> {
                    stopAudioPlayback()
                    stopProgressUpdate()
                }

                Player.STATE_BUFFERING -> {
                    getMediaController()?.let {
                        setCurrentTrackPlayingIndex(it.currentMediaItemIndex)
                    }
                }

                Player.STATE_ENDED -> {
                    stopAudioPlayback()
                }

                Player.STATE_READY -> {
                }
            }
        }
    }
}

data class AudioState(
    val trackName: String,
    var progress: Float,
    var isPlaying: Boolean,
    var stopped: Boolean
)