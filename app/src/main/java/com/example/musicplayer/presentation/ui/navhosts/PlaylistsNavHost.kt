package com.example.musicplayer.presentation.ui.navhosts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musicplayer.presentation.ui.components.Screen
import com.example.musicplayer.presentation.ui.screens.PlaylistsScreen
import com.example.musicplayer.presentation.viewmodels.MyPlaylistsViewModel


@Composable
fun PlaylistsNavHost(
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.PlaylistsScreen.route,
    ) {
        composable(
            route = Screen.PlaylistsScreen.route
        ) {
            PlayListsScreen(navController = navController)
        }
    }
}


@Composable
private fun PlayListsScreen(navController: NavHostController) {
    val myPlaylistsViewModel = hiltViewModel<MyPlaylistsViewModel>()
    PlaylistsScreen(
        navController = navController,
        title = "My Playlists",
        playlists = myPlaylistsViewModel.playListsState.collectAsStateWithLifecycle(
            initialValue = emptyList()
        ),
        onClick = {

        },
        onRemove = {
            myPlaylistsViewModel.removePlaylist(it)
        }
    )
}
