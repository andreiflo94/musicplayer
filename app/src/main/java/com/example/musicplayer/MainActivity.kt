package com.example.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.musicplayer.mainfeature.presentation.ui.components.RequiredPermission
import com.example.musicplayer.mainfeature.presentation.viewmodels.MainActivityViewModel
import com.example.musicplayer.ui.theme.MusicplayerTheme
import dagger.hilt.android.AndroidEntryPoint

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
            navController = navController as NavHostController,
            startAudioPlayback = { trackList, index ->
                viewModel.setCurrentTrackList(trackList, index)
                val list: List<MediaItem> = trackList.map { track -> MediaItem.fromUri(track) }
                viewModel.getMediaController()?.setMediaItems(list, index, 0L)
                viewModel.getMediaController()?.prepare()
                viewModel.getMediaController()?.play()
            },
            playPauseClick = {
                if (viewModel.getMediaController()?.isPlaying == true) {
                    viewModel.getMediaController()?.pause()
                } else {
                    viewModel.getMediaController()?.play()
                }
            },
            stopPlayingClick = {
                viewModel.getMediaController()?.stop()
            },
            onProgressUpdate = {
                val seekTo = ((viewModel.getMediaController()?.duration!! * it) / 100f).toLong()
                viewModel.getMediaController()?.seekTo(seekTo)
            },
            viewModel.audioState.collectAsState()
        )
    }
}

