package com.example.musicplayer.ui.screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.musicplayer.components.MainActivity
import com.example.musicplayer.ui.components.AppPlayerBar
import com.example.musicplayer.viewmodels.MusicFoldersViewModel

data class AppScreenState(var audioFilePlaying: Boolean, var audioFileProgress: Float)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    isPermissionGranted: Boolean,
    requestStoragePermission: () -> Unit,
    openAudioFile: (path: String) -> Unit,
    state: State<AppScreenState>
) {
    val showSnackBar = state.value.audioFilePlaying
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            if (showSnackBar)
                AppPlayerBar(state.value)
        }
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = MainActivity.Screen.SplashScreen.route,
            modifier = Modifier.padding(contentPadding)
        ) {
            composable(MainActivity.Screen.SplashScreen.route) {
                SplashScreen()
                // Check if the permission is already granted
                if (isPermissionGranted) {
                    // Permission already granted
                    // You can now proceed with your logic
                    navController.navigate(MainActivity.Screen.MusicFoldersScreen.route) {
                        popUpTo(MainActivity.Screen.SplashScreen.route) { inclusive = true }
                    }
                    // Do something with the musicFolders
                } else {
                    // Permission not yet granted, request it
                    requestStoragePermission()
                }
            }
            composable(
                route = MainActivity.Screen.MusicFoldersScreen.route + "?path={path}",
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
                    musicViewModel.loadMusicFolders(LocalContext.current)
                } else {
                    musicViewModel.loadMusicFilesFromPath(LocalContext.current, path)
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
                        navController.navigate(MainActivity.Screen.MusicFoldersScreen.route + "?path=$newPath")
                    }
                }
            }
            composable(MainActivity.Screen.PermissionRequiredScreen.route) {
                PermissionRequiredScreen()
            }
        }
    }
}