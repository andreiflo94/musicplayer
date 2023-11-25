package com.example.musicplayer.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.musicplayer.ui.screens.MusicFoldersScreen
import com.example.musicplayer.ui.screens.MusicOverlay
import com.example.musicplayer.ui.screens.PermissionRequiredScreen
import com.example.musicplayer.ui.screens.SplashScreen
import com.example.musicplayer.ui.theme.MusicplayerTheme
import com.example.musicplayer.viewmodels.MusicFoldersViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity(), MusicBroadcastReceiver.MusicBroadcastListener {

    private lateinit var musicReceiver: MusicBroadcastReceiver
    private lateinit var navController: NavController


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                navController.navigate(Screen.MusicFoldersScreen.route)
            } else {
                navController.navigate(Screen.PermissionRequiredScreen.route)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicplayerTheme {
                MusicPlayerApp()
            }
        }
        musicReceiver = MusicBroadcastReceiver(this)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(musicReceiver, IntentFilter(MusicBroadcastReceiver.UPDATE_PLAYBACK_STATUS))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(musicReceiver)
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        // Request the permission
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    sealed class Screen(val route: String) {
        object SplashScreen : Screen("splash_screen")
        object PermissionRequiredScreen : Screen("permission_required_screen")
        object MusicFoldersScreen : Screen("music_folders_screen")
    }

    @Composable
    fun MusicPlayerApp() {
        navController = rememberNavController()
        NavHost(
            navController = navController as NavHostController,
            startDestination = Screen.SplashScreen.route
        ) {
            composable(Screen.SplashScreen.route) {
                SplashScreen()
                // Check if the permission is already granted
                if (isPermissionGranted()) {
                    // Permission already granted
                    // You can now proceed with your logic
                    navController.navigate(Screen.MusicFoldersScreen.route) {
                        popUpTo(Screen.SplashScreen.route) { inclusive = true }
                    }
                    // Do something with the musicFolders
                } else {
                    // Permission not yet granted, request it
                    requestStoragePermission()
                }
            }
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
                    musicViewModel.loadMusicFolders(applicationContext)
                } else {
                    musicViewModel.loadMusicFilesFromPath(applicationContext, path)
                }
                MusicFoldersScreen(
                    musicFolders = musicViewModel.musicFoldersState.collectAsState(
                        initial = emptyList()
                    )
                ) { newPath ->
                    if (newPath.endsWith(".mp3") || newPath.endsWith(".m4a")) {
                        startMusicService(this@MainActivity, newPath)
                        return@MusicFoldersScreen
                    } else {
                        navController.navigate(Screen.MusicFoldersScreen.route + "?path=$newPath")
                    }
                }
            }
            composable(Screen.PermissionRequiredScreen.route) {
                PermissionRequiredScreen()
            }
        }
    }

    private fun startMusicService(context: Context, audioFilePath: String) {
        val serviceIntent = Intent(context, MusicPlaybackService::class.java)
        serviceIntent.action = MusicPlaybackService.PLAY_NEW_TRACK_ACTION
        serviceIntent.putExtra(MusicPlaybackService.EXTRA_AUDIO_FILE_PATH, audioFilePath)
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    override fun onMusicPlaying(isPlaying: Boolean) {
//        setContent {
//            MusicplayerTheme {
//                MusicPlayerApp()
//                MusicOverlay(isPlaying = isPlaying)
//            }
//        }
    }
}
