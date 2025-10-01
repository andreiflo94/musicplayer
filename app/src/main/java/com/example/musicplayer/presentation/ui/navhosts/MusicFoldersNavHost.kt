package com.example.musicplayer.presentation.ui.navhosts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.ui.components.Screen
import com.example.musicplayer.presentation.ui.screens.MusicFoldersScreen
import com.example.musicplayer.presentation.ui.screens.TracksScreen
import com.example.musicplayer.presentation.viewmodels.MusicFoldersViewModel
import com.example.musicplayer.presentation.viewmodels.PlaylistBottomSheetVm
import com.example.musicplayer.presentation.viewmodels.TracksViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun MusicFolderNavHost(
    startAudioPlayback: (list: List<Track>, index: Int) -> Unit,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.MusicFoldersScreen.route,
    ) {
        composable(
            route = Screen.MusicFoldersScreen.route
        ) {
            val musicViewModel: MusicFoldersViewModel = hiltViewModel()
            val musicFolders by musicViewModel.musicFoldersState.collectAsStateWithLifecycle(
                initialValue = emptyList()
            )

            MusicFoldersScreen(
                title = "Music Folders",
                musicFolders = musicFolders,
                onClick = { folder ->
                    val encodedPath =
                        URLEncoder.encode(folder.path, StandardCharsets.UTF_8.toString())
                    navController.navigate("${Screen.TracksScreen.route}/$encodedPath")
                }
            )
        }

        composable(
            route = Screen.TracksScreen.route + "/{path}",
            arguments = listOf(
                navArgument("path") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tracksViewModel: TracksViewModel = hiltViewModel()
            val playlistBottomSheetVm: PlaylistBottomSheetVm = hiltViewModel()

            val tracks by tracksViewModel.tracksState.collectAsStateWithLifecycle()
            val playlists by playlistBottomSheetVm.playListsState.collectAsStateWithLifecycle(
                initialValue = emptyList()
            )

            val path = backStackEntry.arguments?.getString("path") ?: ""
            val decodedPath = java.net.URLDecoder.decode(path, StandardCharsets.UTF_8.toString())
            val pageTitle =
                decodedPath.split("/").lastOrNull().takeIf { it!!.isNotBlank() } ?: "Music Folders"

            TracksScreen(
                title = pageTitle,
                tracks = tracks,
                playlists = playlists,
                addToNewPlaylist = playlistBottomSheetVm::addToNewPlaylist,
                addToPlaylist = playlistBottomSheetVm::addToPlaylist,
                onClick = { track ->
                    val index = tracks.indexOf(track)
                    if (index != -1) startAudioPlayback(tracks, index)
                }
            )
        }
    }
}
