package com.example.musicplayer.mainfeature.presentation.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.musicplayer.mainfeature.presentation.ui.components.AppPlayerBar
import com.example.musicplayer.mainfeature.presentation.viewmodels.MusicFoldersViewModel

enum class AudioFileState {
    PLAYING, PAUSED, STOPED, IDLE
}

sealed class Screen(val route: String) {
    object MusicFoldersScreen : Screen("music_folders_screen")
}

data class AppScreenState(var audioFileState: AudioFileState, var audioFileProgress: Float)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    openAudioFile: (path: String) -> Unit,
    playPauseClick: () -> Unit,
    stopPlayingClick: () -> Unit,
    state: State<AppScreenState>
) {
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        floatingActionButton = {
            val audioFileState = state.value.audioFileState
            if (audioFileState != AudioFileState.IDLE && audioFileState != AudioFileState.STOPED)
                AppPlayerBar(
                    state.value,
                    playPauseClick = playPauseClick,
                    stopPlayingClick = stopPlayingClick
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
                ) { newPath ->
                    if (newPath.endsWith(".mp3") || newPath.endsWith(".m4a")) {
                        openAudioFile(newPath)
                        return@MusicFoldersScreen
                    } else {
                        navController.navigate(Screen.MusicFoldersScreen.route + "?path=$newPath")
                    }
                }
            }
        }
    }
}