package com.example.musicplayer.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.domain.repo.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistTracksViewModel @Inject constructor(
    private val playlistTracksRepository: PlaylistRepository,
    state: SavedStateHandle
) :
    ViewModel() {

    private val playlistId: Long = state.get<Long>("playlistId") ?: -1L

    // null while loading
    private val _playlistTracksState: MutableStateFlow<List<Track>?> = MutableStateFlow(null)
    val playlistTracksState: StateFlow<List<Track>?> get() = _playlistTracksState

    init {
        if (playlistId != -1L) {
            observeTracksFromPlaylist()
        } else {
            _playlistTracksState.value = emptyList()
        }
    }

    // The tracks flow re-emits after the delete, so there's nothing to reload here
    fun removeTrack(track: Track) {
        playlistTracksRepository.deleteTrack(playlistId, track.path)
    }

    // Observed rather than loaded once, so tracks added from the Library tab show up here
    private fun observeTracksFromPlaylist() {
        viewModelScope.launch {
            playlistTracksRepository.getTracksForPlaylist(playlistId).collect {
                _playlistTracksState.value = it
            }
        }
    }
}
