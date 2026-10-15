package com.example.musicplayer.presentation.ui.navhosts

import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.musicplayer.presentation.ui.components.NowPlaying
import com.example.musicplayer.presentation.ui.screens.MusicFoldersScreen
import com.example.musicplayer.presentation.ui.screens.Screen
import com.example.musicplayer.presentation.ui.screens.TracksScreen
import com.example.musicplayer.presentation.viewmodels.MainActivityViewModel
import com.example.musicplayer.presentation.viewmodels.MusicFoldersViewModel
import com.example.musicplayer.presentation.viewmodels.PlaylistBottomSheetVm
import com.example.musicplayer.presentation.viewmodels.TracksViewModel
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun MusicFolderNavHost(onOpenThemes: () -> Unit) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.MusicFoldersScreen.route,
        enterTransition = NavTransitions.enter,
        exitTransition = NavTransitions.exit,
        popEnterTransition = NavTransitions.popEnter,
        popExitTransition = NavTransitions.popExit
    ) {
        composable(Screen.MusicFoldersScreen.route) {
            MusicFoldersScreenRoute(navController, onOpenThemes)
        }

        composable(
            route = Screen.TracksScreen.route + "/{path}",
            arguments = listOf(navArgument("path") { type = NavType.StringType })
        ) { backStackEntry ->
            TracksScreenRoute(
                path = backStackEntry.arguments?.getString("path") ?: "",
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun MusicFoldersScreenRoute(navController: NavHostController, onOpenThemes: () -> Unit) {
    val musicViewModel: MusicFoldersViewModel = hiltViewModel()
    val musicFolders by musicViewModel.musicFoldersState.collectAsStateWithLifecycle()

    MusicFoldersScreen(
        musicFolders = musicFolders,
        onOpenThemes = onOpenThemes,
        onClick = { folder ->
            val encodedPath = URLEncoder.encode(folder.path, StandardCharsets.UTF_8.toString())
            navController.navigate("${Screen.TracksScreen.route}/$encodedPath")
        }
    )
}

@OptIn(UnstableApi::class)
@Composable
private fun TracksScreenRoute(path: String, onBack: () -> Unit) {
    val mainViewModel: MainActivityViewModel =
        hiltViewModel(LocalContext.current as ComponentActivity)
    val tracksViewModel: TracksViewModel = hiltViewModel()
    val playlistBottomSheetVm: PlaylistBottomSheetVm = hiltViewModel()

    val tracks by tracksViewModel.tracksState.collectAsStateWithLifecycle()
    val playlists by playlistBottomSheetVm.playListsState.collectAsStateWithLifecycle(initialValue = emptyList())
    val trackState by mainViewModel.trackState.collectAsStateWithLifecycle()

    val pageTitle = remember(path) {
        URLDecoder.decode(path, StandardCharsets.UTF_8.toString())
            .split("/").lastOrNull { it.isNotBlank() } ?: "Music"
    }

    TracksScreen(
        title = pageTitle,
        tracks = tracks,
        playlists = playlists,
        nowPlaying = NowPlaying(
            trackState.trackPath.takeUnless { trackState.stopped }.orEmpty(),
            trackState.isPlaying
        ),
        onBack = onBack,
        addToNewPlaylist = playlistBottomSheetVm::addToNewPlaylist,
        addToPlaylist = playlistBottomSheetVm::addToPlaylist,
        onPlay = { index -> tracks?.let { mainViewModel.startAudioPlayback(it, index) } },
        onPlayAll = { shuffle -> tracks?.let { mainViewModel.playAll(it, shuffle) } }
    )
}
