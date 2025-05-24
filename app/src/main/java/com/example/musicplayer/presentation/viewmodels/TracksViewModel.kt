package com.example.musicplayer.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.domain.repo.MusicFoldersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TracksViewModel @Inject constructor(
    private val musicFoldersRepository: MusicFoldersRepository,
    state: SavedStateHandle
) :
    ViewModel() {

    private val _tracksState: MutableStateFlow<List<Track>> =
        MutableStateFlow(emptyList())
    val tracksState: StateFlow<List<Track>> get() = _tracksState

    init {
        state.get<String>("path")?.let { path ->
            if (path.isNotBlank()) {
                loadMusicFilesFromPath(path)
            }
        }
    }


    private fun loadMusicFilesFromPath(path: String) {
        viewModelScope.launch {
            _tracksState.value = musicFoldersRepository.getMusicFilesFromPath(path)
        }
    }
}