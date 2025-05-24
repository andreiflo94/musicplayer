package com.example.musicplayer.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.musicplayer.presentation.ui.components.RequiredPermission
import com.example.musicplayer.presentation.viewmodels.MainActivityViewModel
import com.example.musicplayer.presentation.ui.theme.MusicplayerTheme
import dagger.hilt.android.AndroidEntryPoint

@UnstableApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var navController: NavController
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicplayerTheme(dynamicColor = false) {
                MusicPlayerApp()
            }
        }
    }

    @OptIn(UnstableApi::class)
    @Composable
    fun MusicPlayerApp() {
        navController = rememberNavController()
        RequiredPermission(
            startAudioPlayback = { trackList, index ->
                viewModel.startAudioPlayback(trackList, index)
            },
            playPauseClick = {
                viewModel.playPauseClick()
            },
            stopPlayingClick = {
                viewModel.stopPlaying()
            },
            onProgressUpdate = {
                viewModel.onProgressUpdate(it)
            },
            playPrevious = {
                viewModel.playPrevious()
            },
            playNextTrack = {
                viewModel.skipForward()
            },
            audioState =
            viewModel.audioState.collectAsState()
        )
    }
}

