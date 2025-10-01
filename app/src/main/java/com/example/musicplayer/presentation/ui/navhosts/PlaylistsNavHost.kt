package com.example.musicplayer.presentation.ui.navhosts

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.ui.components.Screen
import com.example.musicplayer.presentation.ui.screens.PlaylistTracksScreen
import com.example.musicplayer.presentation.ui.screens.PlaylistsScreen
import com.example.musicplayer.presentation.viewmodels.MyPlaylistsViewModel
import com.example.musicplayer.presentation.viewmodels.PlaylistTracksViewModel

@Composable
fun PlaylistsNavHost(
    startAudioPlayback: (list: List<Track>, index: Int) -> Unit,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.PlaylistsScreen.route
    ) {
        composable(route = Screen.PlaylistsScreen.route) {
            PlayListsScreen(navController)
        }

        composable(
            route = Screen.PlaylistTracksScreen.route + "?playlistId={playlistId}&playlistTitle={playlistTitle}",
            arguments = listOf(
                navArgument("playlistId") { type = NavType.LongType; defaultValue = -1 },
                navArgument("playlistTitle") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val playlistTitle = backStackEntry.arguments?.getString("playlistTitle") ?: "Playlist Tracks"
            PlayListTracksScreen(
                playlistTitle = playlistTitle,
                startAudioPlayback = startAudioPlayback
            )
        }
    }
}

@Composable
private fun PlayListsScreen(
    navController: NavHostController
) {
    val viewModel = hiltViewModel<MyPlaylistsViewModel>()
    val playlists = viewModel.playListsState.collectAsStateWithLifecycle(initialValue = emptyList()).value

    PlaylistsScreen(
        title = "My Playlists",
        playlists = playlists,
        onClick = { playlist ->
            navController.navigate(
                Screen.PlaylistTracksScreen.route + "?playlistId=${playlist.id}&playlistTitle=${playlist.name}"
            )
        },
        onRemove = { playlist ->
            viewModel.removePlaylist(playlist)
        }
    )
}

@Composable
private fun PlayListTracksScreen(
    playlistTitle: String,
    startAudioPlayback: (list: List<Track>, index: Int) -> Unit
) {
    val viewModel = hiltViewModel<PlaylistTracksViewModel>()
    val tracks = viewModel.playlistTracksState.collectAsStateWithLifecycle(initialValue = emptyList()).value

    PlaylistTracksScreen(
        title = playlistTitle,
        tracks = tracks,
        onClick = { track ->
            val index = tracks.indexOf(track)
            if (index >= 0) startAudioPlayback(tracks, index)
        }
    )
}
