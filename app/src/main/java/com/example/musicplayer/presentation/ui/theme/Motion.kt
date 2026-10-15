package com.example.musicplayer.presentation.ui.theme

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

/** Shared motion vocabulary, so every animation in the app feels like it belongs together. */
object Motion {
    const val ThemeFadeMillis = 650

    /** Snappy and slightly playful: presses, toggles, icon pops. */
    fun <T> bouncy(): FiniteAnimationSpec<T> = spring(dampingRatio = 0.5f, stiffness = 500f)

    /** Soft settle with a whisper of overshoot: layout and position changes. */
    fun <T> fluid(): FiniteAnimationSpec<T> =
        spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMediumLow)

    /** Critically damped: sheets and large surfaces. */
    fun <T> calm(): FiniteAnimationSpec<T> =
        spring(dampingRatio = 1f, stiffness = Spring.StiffnessLow)
}
