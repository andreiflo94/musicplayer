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
class MainActivityViewModel @Inject constructor(private val mediaControllerManager: MediaControllerManager,
    private val musicFoldersRepository: MusicFoldersRepository
) :
    ViewModel() {

    private var updateTrackProgressJob: Job? = null

    private val _state = MutableStateFlow(
        AudioState(
            trackName = "",
            trackArtUrl = "",
            progress = 0.0f,
            trackProgressFormatted = "",
            trackDurationFormatted = "",
            isPlaying = false,
            stopped = true,
            hasNextMediaItem = false
        )
    )
    val audioState: StateFlow<AudioState> = _state


    init {
        Log.d("MainActivityViewModel", "Initializing ViewModel")
        mediaControllerManager.init()

        viewModelScope.launch {
            mediaControllerManager.mediaControllerEvents.collect { event ->
                Log.d("MainActivityViewModel", "Received event: $event")

                when (event) {
                    is MediaControllerEvent.IsPlayingChanged -> {
                        Log.d("MainActivityViewModel", "IsPlayingChanged: ${event.isPlaying}")
                        if (event.isPlaying) {
                            stateUpdateStartOrResume()
                            startProgressUpdate()
                        } else {
                            stateUpdatePause()
                            stopProgressUpdate()
                        }
                    }

                    is MediaControllerEvent.MediaItemTransition -> {
                        Log.d("MainActivityViewModel", "MediaItemTransition event received")
                        mediaControllerManager.setCurrentTrackPlayingIndex()
                    }

                    is MediaControllerEvent.PlaybackStateChanged -> {
                        Log.d("MainActivityViewModel", "PlaybackStateChanged: ${event.playbackState}")
                        when (event.playbackState) {
                            Player.STATE_IDLE -> {
                                Log.d("MainActivityViewModel", "Player state: STATE_IDLE")
                                stateUpdateStopAudio()
                                stopProgressUpdate()
                            }
                            Player.STATE_READY,
                            Player.STATE_BUFFERING -> {
                                Log.d("MainActivityViewModel", "Player state: STATE_READY, STATE_BUFFERING")
                                stateUpdateStartOrResume()
                                mediaControllerManager.setCurrentTrackPlayingIndex()
                            }

                            Player.STATE_ENDED -> {
                                Log.d("MainActivityViewModel", "Player state: STATE_ENDED")
                                stateUpdateStopAudio()
                            }
                        }
                    }

                    else -> {
                        Log.d("MainActivityViewModel", "Unhandled event type")
                    }
                }
            }
        }
    }

    override fun onCleared() {
        mediaControllerManager.release()
        _state.value =
            AudioState(
                trackName = "",
                trackArtUrl = "",
                progress = 0.0f,
                trackProgressFormatted = "0:03",
                trackDurationFormatted = "2:30",
                isPlaying = false,
                stopped = true,
                hasNextMediaItem = false
            )
        super.onCleared()
    }

    fun startAudioPlayback(trackList: List<Track>, index: Int) {
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

    fun playPrevious() {
        mediaControllerManager.seekToPreviousMediaItem(audioState.value.progress.toLong())
    }

    fun skipForward() {
        mediaControllerManager.seekToNextMediaItem()
    }

    private fun stateUpdateStartOrResume() {
        _state.value = _state.value.copy(
            trackName = mediaControllerManager.getCurrentPlayingTrackName(),
            trackArtUrl = musicFoldersRepository.getAlbumIconUrl(mediaControllerManager.getCurrentPlayingTrackPath()),
            progress = calculateProgressValue(
                mediaControllerManager.currentPlayingPosition(),
                duration = mediaControllerManager.currentTrackDuration()
            ),
            trackDurationFormatted = MusicUtils.formatDurationFromMillis(mediaControllerManager.currentTrackDuration()),
            isPlaying = mediaControllerManager.isCurrentlyPlaying(),
            stopped = false,
            hasNextMediaItem = mediaControllerManager.hasNextMediaItem()
        )
    }

    private fun stateUpdatePause() {
        _state.value = _state.value.copy(
            trackName = mediaControllerManager.getCurrentPlayingTrackName(),
            trackArtUrl = musicFoldersRepository.getAlbumIconUrl(mediaControllerManager.getCurrentPlayingTrackPath()),
            progress = calculateProgressValue(
                mediaControllerManager.currentPlayingPosition(),
                duration = mediaControllerManager.currentTrackDuration()
            ),
            trackDurationFormatted = MusicUtils.formatDurationFromMillis(mediaControllerManager.currentTrackDuration()),
            isPlaying = false,
            stopped = false,
            hasNextMediaItem = mediaControllerManager.hasNextMediaItem()
        )
    }

    private fun stateUpdateProgress() {
        _state.value = _state.value.copy(
            progress = calculateProgressValue(
                mediaControllerManager.currentPlayingPosition(),
                duration = mediaControllerManager.currentTrackDuration()
            ),
            trackProgressFormatted = MusicUtils.formatDurationFromMillis(mediaControllerManager.currentPlayingPosition()),
            isPlaying = true,
            stopped = false,
            hasNextMediaItem = mediaControllerManager.hasNextMediaItem()
        )
    }

    private fun stateUpdateStopAudio() {
        _state.value = _state.value.copy(
            isPlaying = false,
            stopped = true,
            hasNextMediaItem = mediaControllerManager.hasNextMediaItem()
        )
    }

    private fun startProgressUpdate() {
        updateTrackProgressJob = viewModelScope.launch {
            while (true) {
                delay(100)
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
    val trackArtUrl: String,
    var progress: Float,
    var trackProgressFormatted: String, //"0:03"
    var trackDurationFormatted: String, //3:20
    var isPlaying: Boolean,
    var stopped: Boolean,
    var hasNextMediaItem: Boolean,
)