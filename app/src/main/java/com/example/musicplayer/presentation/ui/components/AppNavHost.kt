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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavHostController
import com.example.musicplayer.presentation.ui.navhosts.MusicFolderNavHost
import com.example.musicplayer.presentation.ui.navhosts.PlaylistsNavHost

sealed class Screen(val route: String) {
    object MusicFoldersScreen : Screen("music_folders_screen")
    object PlaylistsScreen: Screen("playlists_lists")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startAudioPlayback: (list: List<String>, index: Int) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp > 600 // Adjust threshold as needed

    val items = listOf("Music Folders", "Playlists")
    val selectedItem = remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            if (!isTablet) {
                BottomNavigation(
                    backgroundColor = MaterialTheme.colorScheme.onBackground
                ) {
                    items.forEachIndexed { index, item ->
                        val icons = listOf(Icons.Default.Home, Icons.Default.Favorite)

                        BottomNavigationItem(
                            icon = { Icon(icons[index], contentDescription = item) },
                            label = { Text(item) },
                            selected = selectedItem.intValue == index,
                            onClick = { selectedItem.intValue = index },
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
                // Show both pages side by side on tablets
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        MusicFolderNavHost(startAudioPlayback)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        PlaylistsNavHost()
                    }
                }
            } else {
                // Show only one page on phones
                when (selectedItem.intValue) {
                    0 -> MusicFolderNavHost(startAudioPlayback)
                    1 -> PlaylistsNavHost()
                }
            }
        }
    }
}
