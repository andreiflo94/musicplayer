package com.example.musicplayer.viewmodels

import androidx.lifecycle.ViewModel
import com.example.musicplayer.ui.screens.AppScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(AppScreenState(audioFilePlaying = false, 0f))
    val appState: StateFlow<AppScreenState> = _state

    fun changeAudioFileState(isPlaying: Boolean) {
        _state.value = AppScreenState(isPlaying, 0f)
    }

    fun changeAudioFileProgress(progress: Int) {
        _state.value = _state.value.copy(audioFileProgress = progress.toFloat() / 100)
    }
}
