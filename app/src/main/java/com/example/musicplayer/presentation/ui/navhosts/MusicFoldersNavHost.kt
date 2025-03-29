package com.example.musicplayer.presentation.ui.navhosts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.musicplayer.presentation.ui.components.Screen
import com.example.musicplayer.presentation.ui.screens.MusicFoldersScreen
import com.example.musicplayer.presentation.viewmodels.MusicFoldersViewModel


@Composable
fun MusicFolderNavHost(
    startAudioPlayback: (list: List<String>, index: Int) -> Unit
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
