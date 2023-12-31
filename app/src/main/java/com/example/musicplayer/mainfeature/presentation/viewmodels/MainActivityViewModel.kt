package com.example.musicplayer.mainfeature.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.musicplayer.mainfeature.presentation.ui.screens.AppScreenState
import com.example.musicplayer.mainfeature.presentation.ui.screens.AudioFileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(AppScreenState(AudioFileState.IDLE, 0f))
    val appState: StateFlow<AppScreenState> = _state

    fun changeAudioFileState(audioFileState: AudioFileState) {
        Log.d("MainActivityViewModel", ""+audioFileState.name)
        _state.value = AppScreenState(audioFileState, 0f)
    }

    fun changeAudioFileProgress(progress: Int) {
        Log.d("MainActivityViewModel", "progress "+progress)
        _state.value = _state.value.copy(audioFileProgress = progress.toFloat() / 100)
    }
}
