package com.example.musicplayer.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.example.musicplayer.data.MusicUtils
import com.example.musicplayer.domain.MediaControllerEvent
import com.example.musicplayer.domain.MediaControllerManager
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.domain.repo.MusicFoldersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@UnstableApi
class MainActivityViewModel @Inject constructor(
    private val mediaControllerManager: MediaControllerManager,
    private val musicFoldersRepository: MusicFoldersRepository
) : ViewModel() {

    private var updateTrackProgressJob: Job? = null

    private val _state = MutableStateFlow(
        TrackState(
            trackName = "",
            trackArtUrl = "",
            trackDurationFormatted = "0:00",
            isPlaying = false,
            stopped = true,
            hasNextMediaItem = false
        )
    )
    val trackState: StateFlow<TrackState> = _state

    private val _trackLiveProgress = MutableStateFlow(0f)
    val trackLiveProgress: StateFlow<Float> = _trackLiveProgress

    private val _trackProgressFormatted = MutableStateFlow("0:00")
    val trackProgressFormatted: StateFlow<String> = _trackProgressFormatted

    init {
        Log.d("MainActivityViewModel", "Initializing ViewModel")
        mediaControllerManager.init()

        viewModelScope.launch {
            mediaControllerManager.mediaControllerEvents.collect { event ->
                Log.d("MainActivityViewModel", "Received event: $event")
                when (event) {
                    is MediaControllerEvent.IsPlayingChanged -> {
                        if (event.isPlaying) {
                            updateAudioStateStartOrResume()
                            startProgressUpdate()
                        } else {
                            updateAudioStatePause()
                            stopProgressUpdate()
                        }
                    }
                    is MediaControllerEvent.MediaItemTransition -> {
                        mediaControllerManager.setCurrentTrackPlayingIndex()
                        updateAudioStateStartOrResume()
                    }
                    is MediaControllerEvent.PlaybackStateChanged -> {
                        when (event.playbackState) {
                            Player.STATE_IDLE -> {
                                updateAudioStateStop()
                                stopProgressUpdate()
                            }
                            Player.STATE_READY, Player.STATE_BUFFERING -> {
                                updateAudioStateStartOrResume()
                                mediaControllerManager.setCurrentTrackPlayingIndex()
                            }
                            Player.STATE_ENDED -> {
                                updateAudioStateStop()
                                stopProgressUpdate()
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
        _state.value = TrackState(
            trackName = "",
            trackArtUrl = "",
            trackDurationFormatted = "0:00",
            isPlaying = false,
            stopped = true,
            hasNextMediaItem = false
        )
        _trackLiveProgress.value = 0f
        _trackProgressFormatted.value = "0:00"
        super.onCleared()
    }

    fun startAudioPlayback(trackList: List<Track>, index: Int) {
        mediaControllerManager.startAudioPlayback(trackList, index)
        updateAudioStateStartOrResume()
    }

    fun playPauseClick() = mediaControllerManager.playPauseClick()

    fun stopPlaying() = mediaControllerManager.stopPlaying()

    fun onProgressUpdate(it: Long) = mediaControllerManager.onProgressUpdate(it)

    fun playPrevious() = mediaControllerManager.seekToPreviousMediaItem(_trackLiveProgress.value.toLong())

    fun skipForward() = mediaControllerManager.seekToNextMediaItem()

    // =========================
    // State Updates (audio info)
    // =========================

    private fun updateAudioStateStartOrResume() {
        _state.value = _state.value.copy(
            trackName = mediaControllerManager.getCurrentPlayingTrackName(),
            trackArtUrl = musicFoldersRepository.getAlbumIconUrl(mediaControllerManager.getCurrentPlayingTrackPath()),
            trackDurationFormatted = MusicUtils.formatDurationFromMillis(mediaControllerManager.currentTrackDuration()),
            isPlaying = mediaControllerManager.isCurrentlyPlaying(),
            stopped = false,
            hasNextMediaItem = mediaControllerManager.hasNextMediaItem()
        )
    }

    private fun updateAudioStatePause() {
        _state.value = _state.value.copy(
            trackName = mediaControllerManager.getCurrentPlayingTrackName(),
            trackArtUrl = musicFoldersRepository.getAlbumIconUrl(mediaControllerManager.getCurrentPlayingTrackPath()),
            trackDurationFormatted = MusicUtils.formatDurationFromMillis(mediaControllerManager.currentTrackDuration()),
            isPlaying = false,
            stopped = false,
            hasNextMediaItem = mediaControllerManager.hasNextMediaItem()
        )
    }

    private fun updateAudioStateStop() {
        _state.value = _state.value.copy(
            isPlaying = false,
            stopped = true,
            hasNextMediaItem = mediaControllerManager.hasNextMediaItem()
        )
    }

    // =========================
    // Progress Updates (decoupled)
    // =========================

    private fun startProgressUpdate() {
        updateTrackProgressJob = viewModelScope.launch {
            while (true) {
                delay(100)
                val currentPosition = mediaControllerManager.currentPlayingPosition()
                val duration = mediaControllerManager.currentTrackDuration()
                _trackLiveProgress.value = if (duration > 0) (currentPosition.toFloat() / duration.toFloat()) * 100f else 0f
                _trackProgressFormatted.value = MusicUtils.formatDurationFromMillis(currentPosition)
            }
        }
    }

    private fun stopProgressUpdate() {
        updateTrackProgressJob?.cancel()
        updateTrackProgressJob = null
    }
}

data class TrackState(
    val trackName: String,
    val trackArtUrl: String,
    val trackDurationFormatted: String, // adaugat
    val isPlaying: Boolean,
    val stopped: Boolean,
    val hasNextMediaItem: Boolean
)
