package com.example.musicplayer.mainfeature.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.example.musicplayer.mainfeature.domain.MediaControllerEvent
import com.example.musicplayer.mainfeature.domain.MediaControllerManager
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

    init {
        mediaControllerManager.init()
        viewModelScope.launch {
            mediaControllerManager.mediaControllerEvents.collect {
                when (it) {
                    is MediaControllerEvent.IsPlayingChanged -> {
                        if (it.isPlaying) {
                            stateUpdateStartOrResume()
                            startProgressUpdate()
                        } else {
                            stateUpdatePause()
                            stopProgressUpdate()
                        }
                    }

                    is MediaControllerEvent.MediaItemTransition -> {
                        mediaControllerManager.setCurrentTrackPlayingIndex()
                    }

                    is MediaControllerEvent.PlaybackStateChanged -> {
                        when (it.playbackState) {
                            Player.STATE_IDLE -> {
                                stateUpdateStopAudio()
                                stopProgressUpdate()
                            }

                            Player.STATE_BUFFERING -> {
                                mediaControllerManager.setCurrentTrackPlayingIndex()
                            }

                            Player.STATE_ENDED -> {
                                stateUpdateStopAudio()
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onCleared() {
        mediaControllerManager.release()
        super.onCleared()
    }

    fun startAudioPlayback(trackList: List<String>, index: Int) {
        mediaControllerManager.startAudioPlayback(trackList, index)
    }

    fun playPauseClick() {
        mediaControllerManager.playPauseClick()
    }

    fun stopPlaying() {
        mediaControllerManager.stopPlaying()
    }

    fun onProgressUpdate(it: Long) {
        mediaControllerManager.onProgressUpdate(it)
    }

    private fun stateUpdateStartOrResume() {
        _state.value = _state.value.copy(
            trackName = mediaControllerManager.getCurrentTrackName(),
            progress = calculateProgressValue(
                mediaControllerManager.currentPlayingPosition(),
                duration = mediaControllerManager.currentTrackDuration()
            ),
            isPlaying = true,
            stopped = false
        )
    }

    private fun stateUpdatePause() {
        _state.value = _state.value.copy(
            trackName = mediaControllerManager.getCurrentTrackName(),
            progress = calculateProgressValue(
                mediaControllerManager.currentPlayingPosition(),
                duration = mediaControllerManager.currentTrackDuration()
            ),
            isPlaying = false,
            stopped = false
        )
    }

    private fun stateUpdateProgress() {
        _state.value = _state.value.copy(
            trackName = mediaControllerManager.getCurrentTrackName(),
            progress = calculateProgressValue(
                mediaControllerManager.currentPlayingPosition(),
                duration = mediaControllerManager.currentTrackDuration()
            ),
            isPlaying = true,
            stopped = false
        )
    }

    private fun stateUpdateStopAudio() {
        _state.value = _state.value.copy(
            isPlaying = false,
            stopped = true
        )
    }

    private fun startProgressUpdate() {
        updateTrackProgressJob = viewModelScope.launch {
            while (true) {
                delay(500)
                stateUpdateProgress()
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
}

data class AudioState(
    val trackName: String,
    var progress: Float,
    var isPlaying: Boolean,
    var stopped: Boolean
)