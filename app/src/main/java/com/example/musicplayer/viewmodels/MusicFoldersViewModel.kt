package com.example.musicplayer.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.utils.MusicUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MusicFolder(val name: String, val path: String, val albumIconUrl: String?)

@HiltViewModel
class MusicFoldersViewModel @Inject constructor(): ViewModel() {

    // StateFlow to hold the list of music folders
    private val _musicFoldersState: MutableStateFlow<List<MusicFolder>> = MutableStateFlow(emptyList())
    val musicFoldersState: StateFlow<List<MusicFolder>> get() = _musicFoldersState

    // Function to load music folders
    fun loadMusicFolders(context: Context) {
        viewModelScope.launch {
            // Use MusicUtils to get music folders
            val musicFolders = MusicUtils.getMusicFolders(context)

            // Update the StateFlow with the new data
            _musicFoldersState.value = musicFolders.map { folder ->
                MusicFolder(folder.name, folder.path, getAlbumIconUrl(folder.path))
            }
        }
    }

    fun loadMusicFilesFromPath(context: Context, path: String) {
        viewModelScope.launch {
            // Use MusicUtils to get music folders
            val musicFolders = MusicUtils.getMusicFilesFromPath(context, path)

            // Update the StateFlow with the new data
            _musicFoldersState.value = musicFolders.map { folder ->
                MusicFolder(folder.name, folder.path, getAlbumIconUrl(folder.path))
            }
        }
    }

    private fun getAlbumIconUrl(albumPath: String): String {
        // You can return a valid URL or file path to the album cover
        // For simplicity, let's assume the albumPath itself is the URL
        return albumPath
    }
}