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
            loadTracksFromPlaylist()
        } else {
            _playlistTracksState.value = emptyList()
        }
    }

    fun removeTrack(track: Track) {
        viewModelScope.launch {
            playlistTracksRepository.deleteTrack(playlistId, track.path)
            loadTracksFromPlaylist()
        }
    }

    private fun loadTracksFromPlaylist() {
        viewModelScope.launch {
            _playlistTracksState.value = playlistTracksRepository.getTracksForPlaylist(playlistId)
        }
    }
}
