package com.example.musicplayer.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.domain.model.MusicFolder
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.repo.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistBottomSheetVm @Inject constructor(private val playListRepository: PlaylistRepository) :
    ViewModel() {
    val playListsState: Flow<List<Playlist>> = playListRepository.getAllPlaylists()

    fun addToNewPlaylist(playlistName: String, musicFolder: MusicFolder) {
        viewModelScope.launch {
            playListRepository.insertTrackToNewPlaylist(
                playlistName,
                musicFolder.name, musicFolder.path, musicFolder.albumIconUrl
            )
        }
    }

    fun addToPlaylist(playlistId: Long, musicFolder: MusicFolder) {
        viewModelScope.launch {
            playListRepository.insertTrack(
                playlistId,
                musicFolder.name, musicFolder.path, musicFolder.albumIconUrl
            )
        }
    }
}