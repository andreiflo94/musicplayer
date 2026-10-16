package com.example.musicplayer.presentation.ui.screens

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import com.example.musicplayer.presentation.ui.navhosts.MusicFolderNavHost
import com.example.musicplayer.presentation.ui.navhosts.PlaylistsNavHost
import com.example.musicplayer.presentation.ui.theme.Motion

// ---- Screens ----
sealed class Screen(val route: String) {
    object TracksScreen : Screen("tracks_screen") {
        // Navigation decodes path args with Uri.decode, so encode the same way (URLEncoder's
        // "+" for spaces wouldn't come back as spaces).
        fun createRoute(folderPath: String) = "$route/${Uri.encode(folderPath)}"
    }
    object MusicFoldersScreen : Screen("music_folders_screen")
    object PlaylistsScreen : Screen("playlists_lists")
    object PlaylistTracksScreen : Screen("playlist_tracks_screen") {
        fun createRoute(playlistId: Long, playlistTitle: String) =
            "$route?playlistId=$playlistId&playlistTitle=${Uri.encode(playlistTitle)}"
    }
}

/**
 * Phones show one tab at a time and glide between them (each tab keeps its own back stack
 * and scroll position); tablets show both side by side.
 */
@Composable
fun BottomNavigationScreen(isTablet: Boolean, selectedTab: Int, onOpenThemes: () -> Unit) {
    if (isTablet) {
        Row(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f)) { MusicFolderNavHost(onOpenThemes) }
            Box(Modifier.weight(1f)) { PlaylistsNavHost(onOpenThemes) }
        }
        return
    }

    val tabStates = rememberSaveableStateHolder()
    AnimatedContent(
        targetState = selectedTab,
        transitionSpec = {
            val direction = if (targetState > initialState) 1 else -1
            (fadeIn(tween(240, delayMillis = 60)) +
                    slideInHorizontally(Motion.fluid()) { direction * it / 5 } +
                    scaleIn(Motion.fluid(), initialScale = 0.96f)) togetherWith
                    (fadeOut(tween(140)) + slideOutHorizontally(Motion.fluid()) { -direction * it / 5 })
        },
        label = "tabs",
        modifier = Modifier.fillMaxSize()
    ) { tab ->
        tabStates.SaveableStateProvider(tab) {
            when (tab) {
                0 -> MusicFolderNavHost(onOpenThemes)
                else -> PlaylistsNavHost(onOpenThemes)
            }
        }
    }
}
