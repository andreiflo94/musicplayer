package com.example.musicplayer.domain.repo

import com.example.musicplayer.domain.model.AppTheme
import com.example.musicplayer.domain.model.DarkMode
import com.example.musicplayer.domain.model.ThemeSettings
import kotlinx.coroutines.flow.StateFlow

interface ThemeRepository {
    val settings: StateFlow<ThemeSettings>

    fun setTheme(theme: AppTheme)

    fun setDarkMode(darkMode: DarkMode)
}
