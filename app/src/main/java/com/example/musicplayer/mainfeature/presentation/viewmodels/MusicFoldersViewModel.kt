package com.example.musicplayer.mainfeature.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.mainfeature.data.MusicFoldersRepositoryImpl
import com.example.musicplayer.mainfeature.domain.MusicFolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicFoldersViewModel @Inject constructor(
    private val musicFoldersRepositoryImpl: MusicFoldersRepositoryImpl,
    state: SavedStateHandle
) :
    ViewModel() {

    private val _musicFoldersState: MutableStateFlow<List<MusicFolder>> =
        MutableStateFlow(emptyList())
    val musicFoldersState: StateFlow<List<MusicFolder>> get() = _musicFoldersState

    init {
        state.get<String>("path")?.let { path ->
            if (path.isNotBlank()) {
                loadMusicFilesFromPath(path)
            } else {
                loadMusicFolders()
            }
        } ?: run {
            loadMusicFolders()
        }
    }

    private fun loadMusicFolders() {
        viewModelScope.launch {
            _musicFoldersState.value = musicFoldersRepositoryImpl.getMusicFolders()
        }
    }

    private fun loadMusicFilesFromPath(path: String) {
        viewModelScope.launch {
            _musicFoldersState.value = musicFoldersRepositoryImpl.getMusicFilesFromPath(path)
        }
    }
}