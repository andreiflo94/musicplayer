package com.example.musicplayer.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.domain.model.MusicFolder
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.domain.repo.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
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

    private val _playlistTracksState: MutableStateFlow<List<Track>> =
        MutableStateFlow(emptyList())
    val playlistTracksState: StateFlow<List<Track>> get() = _playlistTracksState

    init {
        state.get<Long>("playlistId")?.let { playlistId ->
            if (playlistId != -1L) {
                loadTracksFromPlaylist(playlistId)
            }
        }
    }

    private fun loadTracksFromPlaylist(playlistId: Long) {
        viewModelScope.launch {
            _playlistTracksState.value = playlistTracksRepository.getTracksForPlaylist(playlistId)
        }

    }
}