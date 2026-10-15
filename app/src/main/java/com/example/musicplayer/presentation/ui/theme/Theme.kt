package com.example.musicplayer.presentation.ui.theme

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.musicplayer.domain.model.DarkMode
import com.example.musicplayer.domain.model.ThemeSettings

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun ThemeSettings.isDark(): Boolean = when (darkMode) {
    DarkMode.SYSTEM -> isSystemInDarkTheme()
    DarkMode.LIGHT -> false
    DarkMode.DARK -> true
}

@Composable
fun MusicplayerTheme(
    settings: ThemeSettings = ThemeSettings(),
    content: @Composable () -> Unit
) {
    val darkTheme = settings.isDark()
    val context = LocalContext.current
    val target =
        remember(settings.theme, darkTheme) { settings.theme.colorScheme(darkTheme, context) }
    val colorScheme = animateColorScheme(target)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}

/** Cross-fades every color role, so switching themes melts one palette into the next. */
@Composable
private fun animateColorScheme(target: ColorScheme): ColorScheme {
    val spec: AnimationSpec<Color> = tween(durationMillis = Motion.ThemeFadeMillis)

    @Composable
    fun Color.animated(): Color = animateColorAsState(this, spec, label = "themeColor").value

    return target.copy(
        primary = target.primary.animated(),
        onPrimary = target.onPrimary.animated(),
        primaryContainer = target.primaryContainer.animated(),
        onPrimaryContainer = target.onPrimaryContainer.animated(),
        secondary = target.secondary.animated(),
        onSecondary = target.onSecondary.animated(),
        secondaryContainer = target.secondaryContainer.animated(),
        onSecondaryContainer = target.onSecondaryContainer.animated(),
        tertiary = target.tertiary.animated(),
        tertiaryContainer = target.tertiaryContainer.animated(),
        background = target.background.animated(),
        onBackground = target.onBackground.animated(),
        surface = target.surface.animated(),
        onSurface = target.onSurface.animated(),
        surfaceVariant = target.surfaceVariant.animated(),
        onSurfaceVariant = target.onSurfaceVariant.animated(),
        outline = target.outline.animated(),
        outlineVariant = target.outlineVariant.animated(),
        surfaceContainerLow = target.surfaceContainerLow.animated(),
        surfaceContainer = target.surfaceContainer.animated(),
        surfaceContainerHigh = target.surfaceContainerHigh.animated(),
        surfaceContainerHighest = target.surfaceContainerHighest.animated(),
    )
}
