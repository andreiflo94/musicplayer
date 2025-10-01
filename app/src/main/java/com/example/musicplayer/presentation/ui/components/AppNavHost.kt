package com.example.musicplayer.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.ui.navhosts.MusicFolderNavHost
import com.example.musicplayer.presentation.ui.navhosts.PlaylistsNavHost

sealed class Screen(val route: String) {
    object TracksScreen : Screen("tracks_screen")
    object MusicFoldersScreen : Screen("music_folders_screen")
    object PlaylistsScreen : Screen("playlists_lists")
    object PlaylistTracksScreen : Screen("playlist_tracks_screen")
}

@Composable
fun AppNavHost(
    startAudioPlayback: (list: List<Track>, index: Int) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp > 600 // Adjust threshold if needed

    // Use rememberUpdatedState for lambdas to avoid unnecessary recompositions
    val playbackState by rememberUpdatedState(newValue = startAudioPlayback)

    val items = listOf("Music Folders", "Playlists")
    val icons = listOf(Icons.Default.Home, Icons.Default.Favorite)
    val selectedIndex = remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            if (!isTablet) {
                BottomNavigation(
                    backgroundColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    items.forEachIndexed { index, item ->
                        BottomNavigationItem(
                            icon = { Icon(icons[index], contentDescription = item) },
                            label = { Text(item) },
                            selected = selectedIndex.value == index,
                            onClick = { selectedIndex.value = index },
                            unselectedContentColor = MaterialTheme.colorScheme.primary,
                            selectedContentColor = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (isTablet) {
                // Side-by-side layout for tablets
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        MusicFolderNavHost(playbackState)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        PlaylistsNavHost(playbackState)
                    }
                }
            } else {
                // Single-page layout for phones
                when (selectedIndex.value) {
                    0 -> MusicFolderNavHost(playbackState)
                    1 -> PlaylistsNavHost(playbackState)
                }
            }
        }
    }
}
