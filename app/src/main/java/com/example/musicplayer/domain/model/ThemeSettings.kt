package com.example.musicplayer.domain.model

enum class AppTheme { DYNAMIC, AURORA, SUNSET, OCEAN, FOREST, ROSE, NOIR }

enum class DarkMode { SYSTEM, LIGHT, DARK }

data class ThemeSettings(
    val theme: AppTheme = AppTheme.AURORA,
    val darkMode: DarkMode = DarkMode.SYSTEM
)
