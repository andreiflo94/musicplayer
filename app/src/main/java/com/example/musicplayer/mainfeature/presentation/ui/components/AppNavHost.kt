package com.example.musicplayer.mainfeature.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.musicplayer.mainfeature.presentation.ui.screens.MusicFoldersScreen
import com.example.musicplayer.mainfeature.presentation.viewmodels.MusicFoldersViewModel

sealed class Screen(val route: String) {
    object MusicFoldersScreen : Screen("music_folders_screen")
}


@Composable
fun AppNavHost(
    navController: NavHostController,
    startAudioPlayback: (list: List<String>, index: Int) -> Unit
) {
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
        ) { _ ->
            val musicViewModel = hiltViewModel<MusicFoldersViewModel>()
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
