package com.example.musicplayer.presentation.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.ui.screens.HomeScreen
import com.example.musicplayer.presentation.viewmodels.AudioState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequiredPermission(
    startAudioPlayback: (list: List<Track>, index: Int) -> Unit,
    playPauseClick: () -> Unit,
    stopPlayingClick: () -> Unit,
    playNextTrack: () -> Unit,
    playPrevious: () -> Unit,
    onProgressUpdate: (progress: Long) -> Unit,
    audioState: State<AudioState>
) {
    val permissionToRequest = if (Build.VERSION.SDK_INT >= 33) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    val state = rememberPermissionState(permissionToRequest)

    Scaffold {
        when {
            state.status.isGranted ->
                HomeScreen(audioState = audioState,
                    audioPlayer = {
                        AppPlayerBar(
                            audioState.value,
                            playPauseClick = playPauseClick,
                            stopPlayingClick = stopPlayingClick,
                            onProgressUpdate = onProgressUpdate,
                            playPreviousTrack = playPrevious,
                            playNextTrack = playNextTrack,
                        )
                    },
                    content = {
                        AppNavHost(
                            startAudioPlayback = { list, index ->
                                startAudioPlayback(list, index)
                            }
                        )
                    })

            else -> {
                LaunchedEffect(Unit) {
                    state.launchPermissionRequest()
                }
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Column(Modifier.padding(vertical = 120.dp, horizontal = 16.dp)) {
                        Icon(
                            Icons.Rounded.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Read internal storage permission required",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(4.dp))
                        Text("This is required in order for the app to scan the media files")
                    }
                    val context = LocalContext.current
                    Button(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(16.dp),
                        onClick = {
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                            context.startActivity(intent)
                        }) {
                        Text("Go to settings")
                    }
                }
            }
        }
    }
}