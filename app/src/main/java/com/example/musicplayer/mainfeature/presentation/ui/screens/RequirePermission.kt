package com.example.musicplayer.mainfeature.presentation.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RequiredPermission(
    navController: NavHostController,
    openAudioFile: (path: String) -> Unit,
    playPauseClick: () -> Unit,
    stopPlayingClick: () -> Unit,
    appState: State<AppScreenState>
) {
    val state = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)
    Scaffold {
        when {
            state.status.isGranted -> AppNavHost(
                navController = navController,
                openAudioFile = {
                    openAudioFile(it)
                },
                playPauseClick = {
                    playPauseClick()
                },
                stopPlayingClick = {
                    stopPlayingClick()
                },
                appState
            )

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
                        Text("Camera permission required", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(4.dp))
                        Text("This is required in order for the app to take pictures")
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