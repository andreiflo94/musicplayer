package com.example.musicplayer.presentation.ui.navhosts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.musicplayer.presentation.ui.components.Screen
import com.example.musicplayer.presentation.ui.screens.PlaylistTracksScreen
import com.example.musicplayer.presentation.ui.screens.PlaylistsScreen
import com.example.musicplayer.presentation.viewmodels.MyPlaylistsViewModel
import com.example.musicplayer.presentation.viewmodels.PlaylistTracksViewModel


@Composable
fun PlaylistsNavHost(
    startAudioPlayback: (list: List<String>, index: Int) -> Unit
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.PlaylistsScreen.route,
    ) {
        composable(
            route = Screen.PlaylistsScreen.route,
        ) {
            PlayListsScreen(navController = navController)
        }
        composable(
            route = Screen.PlaylistTracksScreen.route + "?playlistId={playlistId}&playlistTitle={playlistTitle}",
            arguments = listOf(navArgument(name = "playlistId") {
                type = NavType.LongType
                defaultValue = -1
            },
                navArgument(name = "playlistTitle") {
                    type = NavType.StringType
                    defaultValue = ""
                })
        ) { backStackEntry ->
            val playlistTitle = backStackEntry.arguments?.getString("playlistTitle")
                ?: "Playlist Tracks"
            PlayListTracksScreen(
                playlistTitle = playlistTitle,
                startAudioPlayback = startAudioPlayback
            )
        }
    }
}


@Composable
private fun PlayListsScreen(navController: NavHostController) {
    val myPlaylistsViewModel = hiltViewModel<MyPlaylistsViewModel>()
    PlaylistsScreen(
        navController = navController,
        title = "My Playlists",
        playlists = myPlaylistsViewModel.playListsState.collectAsStateWithLifecycle(
            initialValue = emptyList()
        ),
        onClick = {
            navController.navigate(Screen.PlaylistTracksScreen.route + "?playlistId=${it.id}&playlistTitle=${it.name}")

        },
        onRemove = {
            myPlaylistsViewModel.removePlaylist(it)
        }
    )
}

@Composable
private fun PlayListTracksScreen(
    playlistTitle: String,
    startAudioPlayback: (list: List<String>, index: Int) -> Unit
) {
    val playlistTracksViewModel = hiltViewModel<PlaylistTracksViewModel>()
    PlaylistTracksScreen(
        title = playlistTitle,
        tracks = playlistTracksViewModel.playlistTracksState.collectAsStateWithLifecycle(
            initialValue = emptyList()
        ),
        onClick = { playlistTrack ->
            startAudioPlayback(playlistTracksViewModel.playlistTracksState.value.map {
                it.path
            }, playlistTracksViewModel.playlistTracksState.value.indexOf(playlistTrack))
        }
    )
}
