package com.example.musicplayer.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.example.musicplayer.presentation.ui.navhosts.MusicFolderNavHost
import com.example.musicplayer.presentation.ui.navhosts.PlaylistsNavHost

// ---- Screens ----
sealed class Screen(val route: String) {
    object TracksScreen : Screen("tracks_screen")
    object MusicFoldersScreen : Screen("music_folders_screen")
    object PlaylistsScreen : Screen("playlists_lists")
    object PlaylistTracksScreen : Screen("playlist_tracks_screen")
}

// ---- AppNavHost ----
@Composable
fun BottomNavigationScreen() {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp > 600
    val selectedIndex = rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            if (!isTablet) BottomNavBar(selectedIndex)
        }
    ) { paddingValues ->
        AppContent(isTablet, selectedIndex.intValue, Modifier.padding(paddingValues))
    }
}

// ---- Bottom Navigation Bar ----
@Composable
private fun BottomNavBar(selectedIndex: MutableIntState) {
    val items = listOf("Music Folders", "Playlists")
    val icons = listOf(Icons.Default.Home, Icons.Default.Favorite)

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.onBackground,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item) },
                selected = selectedIndex.intValue == index,
                onClick = { selectedIndex.intValue = index },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    selectedIconColor = MaterialTheme.colorScheme.onBackground,
                    unselectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

// ---- App Content ----
@Composable
private fun AppContent(isTablet: Boolean, selectedIndex: Int, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        if (isTablet) {
            TabletLayout()
        } else {
            PhoneLayout(selectedIndex)
        }
    }
}

// ---- Phone Layout ----
@Composable
private fun PhoneLayout(selectedIndex: Int) {
    when (selectedIndex) {
        0 -> MusicFolderNavHost()
        1 -> PlaylistsNavHost()
    }
}

// ---- Tablet Layout ----
@Composable
private fun TabletLayout() {
    Row(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            MusicFolderNavHost()
        }
        Box(modifier = Modifier.weight(1f)) {
            PlaylistsNavHost()
        }
    }
}
