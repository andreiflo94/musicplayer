package com.example.musicplayer.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.example.musicplayer.domain.model.AppTheme
import com.example.musicplayer.domain.model.DarkMode
import com.example.musicplayer.domain.model.ThemeSettings
import com.example.musicplayer.domain.repo.ThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themeRepository: ThemeRepository
) : ViewModel() {

    val settings: StateFlow<ThemeSettings> = themeRepository.settings

    fun setTheme(theme: AppTheme) = themeRepository.setTheme(theme)

    fun setDarkMode(darkMode: DarkMode) = themeRepository.setDarkMode(darkMode)
}
