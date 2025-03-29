package com.example.musicplayer.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.repo.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MyPlaylistsViewModel @Inject constructor(private val playListRepository: PlaylistRepository) :
    ViewModel() {
    val playListsState: Flow<List<Playlist>> = playListRepository.getAllPlaylists()

    fun removePlaylist(playlist: Playlist){
        playListRepository.deletePlaylist(playlist.id)
    }
}