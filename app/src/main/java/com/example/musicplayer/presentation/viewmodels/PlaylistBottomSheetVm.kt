package com.example.musicplayer.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.domain.model.MusicFolder
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.domain.repo.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistBottomSheetVm @Inject constructor(private val playListRepository: PlaylistRepository) :
    ViewModel() {
    val playListsState: Flow<List<Playlist>> = playListRepository.getAllPlaylists()

    fun addToNewPlaylist(playlistName: String, track: Track) {
        viewModelScope.launch {
            playListRepository.insertTrackToNewPlaylist(
                playlistName,
                track.name, track.path, track.albumIconUrl
            )
        }
    }

    fun addToPlaylist(playlistId: Long, track: Track) {
        viewModelScope.launch {
            playListRepository.insertTrack(
                playlistId,
                track.name, track.path, track.albumIconUrl
            )
        }
    }
}