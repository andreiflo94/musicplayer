package com.example.musicplayer.presentation.ui.navhosts

import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.presentation.ui.components.NowPlaying
import com.example.musicplayer.presentation.ui.screens.PlaylistTracksScreen
import com.example.musicplayer.presentation.ui.screens.PlaylistsScreen
import com.example.musicplayer.presentation.ui.screens.Screen
import com.example.musicplayer.presentation.viewmodels.MainActivityViewModel
import com.example.musicplayer.presentation.viewmodels.MyPlaylistsViewModel
import com.example.musicplayer.presentation.viewmodels.PlaylistTracksViewModel

@Composable
fun PlaylistsNavHost(onOpenThemes: () -> Unit) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.PlaylistsScreen.route,
        enterTransition = NavTransitions.enter,
        exitTransition = NavTransitions.exit,
        popEnterTransition = NavTransitions.popEnter,
        popExitTransition = NavTransitions.popExit
    ) {
        composable(route = Screen.PlaylistsScreen.route) {
            PlaylistsScreenRoute(navController, onOpenThemes)
        }

        composable(
            route = Screen.PlaylistTracksScreen.route + "?playlistId={playlistId}&playlistTitle={playlistTitle}",
            arguments = listOf(
                navArgument("playlistId") { type = NavType.LongType; defaultValue = -1 },
                navArgument("playlistTitle") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val playlistTitle = backStackEntry.arguments?.getString("playlistTitle").orEmpty()
            PlaylistTracksScreenRoute(
                playlistTitle = playlistTitle.ifBlank { "Playlist" },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun PlaylistsScreenRoute(navController: NavHostController, onOpenThemes: () -> Unit) {
    val viewModel = hiltViewModel<MyPlaylistsViewModel>()
    val playlists by viewModel.playListsState.collectAsStateWithLifecycle(initialValue = null)

    PlaylistsScreen(
        playlists = playlists,
        onOpenThemes = onOpenThemes,
        onClick = { playlist: Playlist ->
            navController.navigate(
                Screen.PlaylistTracksScreen.createRoute(playlist.id, playlist.name)
            )
        },
        onRemove = viewModel::removePlaylist
    )
}

@OptIn(UnstableApi::class)
@Composable
private fun PlaylistTracksScreenRoute(playlistTitle: String, onBack: () -> Unit) {
    val activityViewModel =
        hiltViewModel<MainActivityViewModel>(LocalContext.current as ComponentActivity)
    val viewModel = hiltViewModel<PlaylistTracksViewModel>()
    val tracks by viewModel.playlistTracksState.collectAsStateWithLifecycle()
    val trackState by activityViewModel.trackState.collectAsStateWithLifecycle()

    PlaylistTracksScreen(
        title = playlistTitle,
        tracks = tracks,
        nowPlaying = NowPlaying(
            trackState.trackPath.takeUnless { trackState.stopped }.orEmpty(),
            trackState.isPlaying
        ),
        onBack = onBack,
        onPlay = { index -> tracks?.let { activityViewModel.startAudioPlayback(it, index) } },
        onPlayAll = { shuffle -> tracks?.let { activityViewModel.playAll(it, shuffle) } },
        onRemove = viewModel::removeTrack
    )
}
