package com.example.musicplayer.presentation.ui.navhosts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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


@Composable
fun MusicFolderNavHost(
    startAudioPlayback: (list: List<Track>, index: Int) -> Unit
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.MusicFoldersScreen.route,
    ) {
        composable(
            route = Screen.MusicFoldersScreen.route + "?path={path}",
            arguments = listOf(navArgument(name = "path") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { path ->
            val pageTitle = path.arguments?.getString("path")
                ?.split("/")?.lastOrNull()
                ?.takeIf { it.isNotBlank() } ?: "Music Folders"

            val musicViewModel = hiltViewModel<MusicFoldersViewModel>()
            MusicFoldersScreen(
                title = pageTitle,
                musicFolders = musicViewModel.musicFoldersState.collectAsState(
                    initial = emptyList()
                )
            ) { musicFolder ->
                navController.navigate(Screen.TracksScreen.route + "?path=${musicFolder.path}")
            }
        }
        composable(
            route = Screen.TracksScreen.route + "?path={path}",
            arguments = listOf(navArgument(name = "path") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { path ->
            val pageTitle = path.arguments?.getString("path")
                ?.split("/")?.lastOrNull()
                ?.takeIf { it.isNotBlank() } ?: "Music Folders"

            val tracksViewModel = hiltViewModel<TracksViewModel>()
            val playlistBottomSheetVm = hiltViewModel<PlaylistBottomSheetVm>()
            TracksScreen(
                title = pageTitle,
                tracks = tracksViewModel.tracksState.collectAsStateWithLifecycle(),
                playlists = playlistBottomSheetVm.playListsState.collectAsStateWithLifecycle(
                    emptyList()
                ),
                addToNewPlaylist = { playlistName, track ->
                    playlistBottomSheetVm.addToNewPlaylist(playlistName, track)
                },
                addToPlaylist = { playListId, track ->
                    playlistBottomSheetVm.addToPlaylist(playListId, track)
                },
            ) { musicFolder ->
                startAudioPlayback(tracksViewModel.tracksState.value, tracksViewModel.tracksState.value.indexOf(musicFolder))
                return@TracksScreen
            }
        }
    }
}
