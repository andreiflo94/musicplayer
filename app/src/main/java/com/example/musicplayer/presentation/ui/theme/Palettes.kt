package com.example.musicplayer.presentation.ui.theme

import android.content.Context
import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import com.example.musicplayer.domain.model.AppTheme

/** The handful of hand-picked colors a theme is built from; every other role is derived. */
private class Seed(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val onBackground: Color,
    val onSurfaceVariant: Color,
    val onPrimary: Color,
)

private class Palette(val light: Seed, val dark: Seed)

private val Aurora = Palette(
    light = Seed(
        primary = Color(0xFF6C4DF6),
        secondary = Color(0xFF00A3B8),
        tertiary = Color(0xFFF0509E),
        background = Color(0xFFF7F5FF),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFECE8FA),
        onBackground = Color(0xFF1B1730),
        onSurfaceVariant = Color(0xFF5A5670),
        onPrimary = Color.White
    ),
    dark = Seed(
        primary = Color(0xFFB9A6FF),
        secondary = Color(0xFF5EE7F2),
        tertiary = Color(0xFFFF8CC6),
        background = Color(0xFF0E0B1A),
        surface = Color(0xFF17132A),
        surfaceVariant = Color(0xFF262040),
        onBackground = Color(0xFFEDE9FF),
        onSurfaceVariant = Color(0xFFBDB6D8),
        onPrimary = Color(0xFF22106B)
    )
)

private val Sunset = Palette(
    light = Seed(
        primary = Color(0xFFE5482A),
        secondary = Color(0xFFE89412),
        tertiary = Color(0xFFC2185B),
        background = Color(0xFFFFF6F0),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFFBE6DA),
        onBackground = Color(0xFF2B160F),
        onSurfaceVariant = Color(0xFF6E5248),
        onPrimary = Color.White
    ),
    dark = Seed(
        primary = Color(0xFFFF8A65),
        secondary = Color(0xFFFFC56B),
        tertiary = Color(0xFFFF7AA8),
        background = Color(0xFF1A0F0C),
        surface = Color(0xFF24150F),
        surfaceVariant = Color(0xFF3A241B),
        onBackground = Color(0xFFFFEDE6),
        onSurfaceVariant = Color(0xFFD9BBAE),
        onPrimary = Color(0xFF4A1206)
    )
)

private val Ocean = Palette(
    light = Seed(
        primary = Color(0xFF0071B0),
        secondary = Color(0xFF0097B7),
        tertiary = Color(0xFF3D5AFE),
        background = Color(0xFFF2F8FC),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFDDEBF5),
        onBackground = Color(0xFF0B1D2A),
        onSurfaceVariant = Color(0xFF475F70),
        onPrimary = Color.White
    ),
    dark = Seed(
        primary = Color(0xFF6CC8FF),
        secondary = Color(0xFF7FE3F5),
        tertiary = Color(0xFF9AA8FF),
        background = Color(0xFF07131C),
        surface = Color(0xFF0E1E2A),
        surfaceVariant = Color(0xFF1A2F3E),
        onBackground = Color(0xFFE3F2FD),
        onSurfaceVariant = Color(0xFFADC4D4),
        onPrimary = Color(0xFF00344F)
    )
)

private val Forest = Palette(
    light = Seed(
        primary = Color(0xFF2E7D32),
        secondary = Color(0xFF7A8B1F),
        tertiary = Color(0xFFA1662F),
        background = Color(0xFFF4F8F1),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFE0EAD9),
        onBackground = Color(0xFF142015),
        onSurfaceVariant = Color(0xFF4F5E4F),
        onPrimary = Color.White
    ),
    dark = Seed(
        primary = Color(0xFF7FD48A),
        secondary = Color(0xFFC9DB6A),
        tertiary = Color(0xFFE3A86E),
        background = Color(0xFF0C140D),
        surface = Color(0xFF142016),
        surfaceVariant = Color(0xFF213224),
        onBackground = Color(0xFFE4F1E4),
        onSurfaceVariant = Color(0xFFB3C6B3),
        onPrimary = Color(0xFF0A3911)
    )
)

private val Rose = Palette(
    light = Seed(
        primary = Color(0xFFD81B60),
        secondary = Color(0xFF9C3FB0),
        tertiary = Color(0xFFF4511E),
        background = Color(0xFFFFF5F8),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFF9E1EA),
        onBackground = Color(0xFF2A1119),
        onSurfaceVariant = Color(0xFF6B4D58),
        onPrimary = Color.White
    ),
    dark = Seed(
        primary = Color(0xFFFF8FB8),
        secondary = Color(0xFFE59AF0),
        tertiary = Color(0xFFFFAB91),
        background = Color(0xFF1A0D12),
        surface = Color(0xFF26131B),
        surfaceVariant = Color(0xFF3B2029),
        onBackground = Color(0xFFFFE8EF),
        onSurfaceVariant = Color(0xFFDDB8C5),
        onPrimary = Color(0xFF5C0A2C)
    )
)

private val Noir = Palette(
    light = Seed(
        primary = Color(0xFF151515),
        secondary = Color(0xFF5F5F5F),
        tertiary = Color(0xFF9A9A9A),
        background = Color(0xFFFAFAFA),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFEDEDED),
        onBackground = Color(0xFF111111),
        onSurfaceVariant = Color(0xFF5C5C5C),
        onPrimary = Color.White
    ),
    dark = Seed(
        primary = Color(0xFFF2F2F2),
        secondary = Color(0xFFBDBDBD),
        tertiary = Color(0xFF7A7A7A),
        background = Color(0xFF000000),
        surface = Color(0xFF0E0E0E),
        surfaceVariant = Color(0xFF1E1E1E),
        onBackground = Color(0xFFF5F5F5),
        onSurfaceVariant = Color(0xFFAAAAAA),
        onPrimary = Color.Black
    )
)

private fun AppTheme.palette(): Palette = when (this) {
    AppTheme.DYNAMIC, AppTheme.AURORA -> Aurora
    AppTheme.SUNSET -> Sunset
    AppTheme.OCEAN -> Ocean
    AppTheme.FOREST -> Forest
    AppTheme.ROSE -> Rose
    AppTheme.NOIR -> Noir
}

val AppTheme.displayName: String
    get() = when (this) {
        AppTheme.DYNAMIC -> "Wallpaper"
        AppTheme.AURORA -> "Aurora"
        AppTheme.SUNSET -> "Sunset"
        AppTheme.OCEAN -> "Ocean"
        AppTheme.FOREST -> "Forest"
        AppTheme.ROSE -> "Rose"
        AppTheme.NOIR -> "Noir"
    }

val supportsDynamicColor: Boolean get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

/** Themes offered in the picker; wallpaper colors only exist on Android 12+. */
val selectableThemes: List<AppTheme>
    get() = AppTheme.values().filter { it != AppTheme.DYNAMIC || supportsDynamicColor }

fun AppTheme.colorScheme(dark: Boolean, context: Context): ColorScheme {
    if (this == AppTheme.DYNAMIC && supportsDynamicColor) {
        return if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    val palette = palette()
    return (if (dark) palette.dark else palette.light).toColorScheme(dark)
}

private fun Seed.toColorScheme(dark: Boolean): ColorScheme {
    fun tint(color: Color, amount: Float) = color.copy(alpha = amount).compositeOver(surface)
    val containerAlpha = if (dark) 0.28f else 0.16f
    val base = if (dark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = tint(primary, containerAlpha),
        onPrimaryContainer = onBackground,
        inversePrimary = tint(primary, 0.6f),
        secondary = secondary,
        onSecondary = onPrimary,
        secondaryContainer = tint(secondary, containerAlpha),
        onSecondaryContainer = onBackground,
        tertiary = tertiary,
        onTertiary = onPrimary,
        tertiaryContainer = tint(tertiary, containerAlpha),
        onTertiaryContainer = onBackground,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onBackground,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        surfaceTint = primary,
        outline = onSurfaceVariant.copy(alpha = 0.5f).compositeOver(surface),
        outlineVariant = surfaceVariant,
        surfaceContainerLowest = background,
        surfaceContainerLow = tint(surfaceVariant, 0.5f),
        surfaceContainer = tint(surfaceVariant, 0.75f),
        surfaceContainerHigh = surfaceVariant,
        surfaceContainerHighest = tint(primary, if (dark) 0.12f else 0.08f).compositeOver(
            surfaceVariant
        ),
        surfaceBright = surface,
        surfaceDim = background,
    )
}
