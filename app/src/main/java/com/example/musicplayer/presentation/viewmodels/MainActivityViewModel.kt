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

    private val _state = MutableStateFlow(TrackState())
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
                    is MediaControllerEvent.TrackTransition -> {
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
        _state.value = TrackState()
        _trackLiveProgress.value = 0f
        _trackProgressFormatted.value = "0:00"
        super.onCleared()
    }

    fun startAudioPlayback(trackList: List<Track>, index: Int) {
        mediaControllerManager.startAudioPlayback(trackList, index)
        updateAudioStateStartOrResume()
    }

    fun playAll(trackList: List<Track>, shuffle: Boolean) {
        if (trackList.isEmpty()) return
        startAudioPlayback(if (shuffle) trackList.shuffled() else trackList, 0)
    }

    fun playPauseClick() = mediaControllerManager.playPauseClick()

    fun stopPlaying() = mediaControllerManager.stopPlaying()

    fun onProgressUpdate(it: Long) {
        mediaControllerManager.onProgressUpdate(it)
        // Reflect the seek right away; the polling loop isn't running while paused
        _trackLiveProgress.value = it.toFloat()
        _trackProgressFormatted.value =
            MusicUtils.formatDurationFromMillis(_state.value.durationMs * it / 100)
    }

    fun playPrevious() = mediaControllerManager.seekToPreviousMediaItem(_trackLiveProgress.value.toLong())

    fun skipForward() = mediaControllerManager.seekToNextMediaItem()

    // =========================
    // State Updates (audio info)
    // =========================

    private fun updateAudioStateStartOrResume() =
        updateTrackInfo(isPlaying = mediaControllerManager.isCurrentlyPlaying())

    // Keeps `stopped`: Media3 reports isPlaying=false right after STATE_ENDED/STATE_IDLE, and that
    // mustn't bring the hidden player back.
    private fun updateAudioStatePause() =
        updateTrackInfo(isPlaying = false, stopped = _state.value.stopped)

    private fun updateTrackInfo(isPlaying: Boolean, stopped: Boolean = false) {
        val track = mediaControllerManager.getCurrentPlayingTrack() ?: return
        val duration = mediaControllerManager.currentTrackDuration().coerceAtLeast(0)
        _state.value = _state.value.copy(
            trackName = track.name,
            title = track.trackName.ifBlank { track.name },
            artist = track.artistName,
            trackPath = track.path,
            trackArtUrl = musicFoldersRepository.getAlbumIconUrl(track.path),
            trackDurationFormatted = MusicUtils.formatDurationFromMillis(duration),
            durationMs = duration,
            isPlaying = isPlaying,
            stopped = stopped,
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
        updateTrackProgressJob?.cancel()
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
    val trackName: String = "",
    val title: String = "",
    val artist: String = "",
    val trackPath: String = "",
    val trackArtUrl: String = "",
    val trackDurationFormatted: String = "0:00",
    val durationMs: Long = 0,
    val isPlaying: Boolean = false,
    val stopped: Boolean = true,
    val hasNextMediaItem: Boolean = false
)
