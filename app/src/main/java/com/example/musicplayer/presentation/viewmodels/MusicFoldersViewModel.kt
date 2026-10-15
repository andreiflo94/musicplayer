package com.example.musicplayer.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.domain.model.MusicFolder
import com.example.musicplayer.domain.repo.MusicFoldersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicFoldersViewModel @Inject constructor(
    private val musicFoldersRepository: MusicFoldersRepository,
) :
    ViewModel() {

    // null while loading
    private val _musicFoldersState: MutableStateFlow<List<MusicFolder>?> = MutableStateFlow(null)
    val musicFoldersState: StateFlow<List<MusicFolder>?> get() = _musicFoldersState

    init {
        loadMusicFolders()
    }

    private fun loadMusicFolders() {
        viewModelScope.launch {
            _musicFoldersState.value = musicFoldersRepository.getMusicFolders()
        }
    }
}
