package com.example.musicplayer.data

import android.content.Context
import com.example.musicplayer.domain.model.AppTheme
import com.example.musicplayer.domain.model.DarkMode
import com.example.musicplayer.domain.model.ThemeSettings
import com.example.musicplayer.domain.repo.ThemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeRepositoryImpl(context: Context) : ThemeRepository {

    private val prefs = context.getSharedPreferences("theme_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(
        ThemeSettings(
            theme = prefs.getString(KEY_THEME, null).toEnumOr(AppTheme.AURORA),
            darkMode = prefs.getString(KEY_DARK_MODE, null).toEnumOr(DarkMode.SYSTEM)
        )
    )
    override val settings: StateFlow<ThemeSettings> = _settings

    override fun setTheme(theme: AppTheme) {
        _settings.value = _settings.value.copy(theme = theme)
        prefs.edit().putString(KEY_THEME, theme.name).apply()
    }

    override fun setDarkMode(darkMode: DarkMode) {
        _settings.value = _settings.value.copy(darkMode = darkMode)
        prefs.edit().putString(KEY_DARK_MODE, darkMode.name).apply()
    }

    private inline fun <reified T : Enum<T>> String?.toEnumOr(default: T): T =
        enumValues<T>().firstOrNull { it.name == this } ?: default

    private companion object {
        const val KEY_THEME = "theme"
        const val KEY_DARK_MODE = "dark_mode"
    }
}
