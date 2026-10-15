package com.example.musicplayer.presentation.ui.components

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.dp

/**
 * Space that scrolling content must leave at the bottom so its last item can scroll clear of
 * the floating nav bar and mini player. Provided by the app shell.
 */
val LocalContentBottomPadding = compositionLocalOf { 0.dp }

/** Lightweight snapshot of what's playing, for highlighting the current row in lists. */
data class NowPlaying(val path: String = "", val isPlaying: Boolean = false) {
    fun isCurrent(trackPath: String): Boolean = path.isNotEmpty() && path == trackPath
}

