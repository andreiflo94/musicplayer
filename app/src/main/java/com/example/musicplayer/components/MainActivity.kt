package com.example.musicplayer.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.musicplayer.ui.screens.AppNavHost
import com.example.musicplayer.ui.screens.AppScreenState
import com.example.musicplayer.ui.theme.MusicplayerTheme
import com.example.musicplayer.viewmodels.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity(), MusicBroadcastReceiver.MusicBroadcastListener {

    private lateinit var musicReceiver: MusicBroadcastReceiver
    private lateinit var navController: NavController
    private val viewModel: MainActivityViewModel by viewModels()

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
        AppNavHost(
            navController = navController as NavHostController,
            isPermissionGranted = isPermissionGranted(),
            requestStoragePermission = { requestStoragePermission() },
            openAudioFile = {
                startMusicService(this@MainActivity, it)
            },
            viewModel.appState.collectAsState()
        )
    }

    private fun startMusicService(context: Context, audioFilePath: String) {
        val serviceIntent = Intent(context, MusicPlaybackService::class.java)
        serviceIntent.action = MusicPlaybackService.PLAY_NEW_TRACK_ACTION
        serviceIntent.putExtra(MusicPlaybackService.EXTRA_AUDIO_FILE_PATH, audioFilePath)
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    override fun onMusicPlaying(isPlaying: Boolean) {
        viewModel.changeAudioFileState(isPlaying)
    }

    override fun onMusicPlayingProgress(progress: Int, totalDuration: Int) {
        Log.d("Progress", " " + progress + " " + totalDuration)
        viewModel.changeAudioFileProgress(progress)
    }
}
