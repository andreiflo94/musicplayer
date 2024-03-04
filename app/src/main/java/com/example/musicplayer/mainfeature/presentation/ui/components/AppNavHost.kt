package com.example.musicplayer.mainfeature.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.musicplayer.mainfeature.presentation.ui.screens.MusicFoldersScreen
import com.example.musicplayer.mainfeature.presentation.viewmodels.AudioState
import com.example.musicplayer.mainfeature.presentation.viewmodels.MusicFoldersViewModel

sealed class Screen(val route: String) {
    object MusicFoldersScreen : Screen("music_folders_screen")
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    startAudioPlayback: (list: List<String>, index: Int) -> Unit,
    playPauseClick: () -> Unit,
    stopPlayingClick: () -> Unit,
    onProgressUpdate: (progress: Long) -> Unit,
    state: State<AudioState>
) {
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            AppPlayerBar(
                state.value,
                playPauseClick = playPauseClick,
                stopPlayingClick = stopPlayingClick,
                onProgressUpdate = onProgressUpdate
            )
        }
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.MusicFoldersScreen.route,
            modifier = Modifier.padding(contentPadding)
        ) {
            composable(
                route = Screen.MusicFoldersScreen.route + "?path={path}",
                arguments = listOf(navArgument(name = "path") {
                    type = NavType.StringType
                    defaultValue = ""
                })
            ) { backStackEntry ->
                val navArgs = backStackEntry.arguments
                var path = ""
                navArgs?.getString("path")?.let {
                    path = it
                }
                val musicViewModel = hiltViewModel<MusicFoldersViewModel>()
                if (path.isBlank()) {
                    musicViewModel.loadMusicFolders()
                } else {
                    musicViewModel.loadMusicFilesFromPath(path)
                }
                MusicFoldersScreen(
                    musicFolders = musicViewModel.musicFoldersState.collectAsState(
                        initial = emptyList()
                    )
                ) { musicFolder ->
                    if (musicFolder.isAudioFile()) {
                        startAudioPlayback(musicViewModel.musicFoldersState.value.map {
                            it.path
                        }, musicViewModel.musicFoldersState.value.indexOf(musicFolder))
                        return@MusicFoldersScreen
                    } else {
                        navController.navigate(Screen.MusicFoldersScreen.route + "?path=${musicFolder.path}")
                    }
                }
            }
        }
    }
}