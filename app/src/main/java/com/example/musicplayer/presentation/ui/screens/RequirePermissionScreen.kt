package com.example.musicplayer.presentation.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.musicplayer.presentation.AppShell
import com.example.musicplayer.presentation.ui.components.AuroraBackground
import com.example.musicplayer.presentation.ui.components.EmptyState
import com.example.musicplayer.presentation.ui.components.GradientPillButton
import com.example.musicplayer.presentation.ui.components.TonalPillButton
import com.example.musicplayer.presentation.ui.preview.PreviewScreen
import com.example.musicplayer.presentation.ui.theme.Motion
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequiredPermissionScreen() {
    val context = LocalContext.current
    val permission = if (Build.VERSION.SDK_INT >= 33) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionState = rememberPermissionState(permission)

    // Launch request only once when permission is not granted
    LaunchedEffect(permissionState.status) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    AnimatedContent(
        targetState = permissionState.status.isGranted,
        transitionSpec = {
            (fadeIn() + scaleIn(
                Motion.fluid(),
                initialScale = 0.92f
            )) togetherWith fadeOut()
        },
        label = "permissionGate"
    ) { granted ->
        if (granted) {
            AppShell()
        } else {
            PermissionDeniedContent(
                canAskAgain = permissionState.status.shouldShowRationale,
                onRequest = { permissionState.launchPermissionRequest() },
                onGoToSettings = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
private fun PermissionDeniedContent(
    canAskAgain: Boolean,
    onRequest: () -> Unit,
    onGoToSettings: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AuroraBackground(Modifier.fillMaxSize())
        EmptyState(
            icon = Icons.Rounded.LibraryMusic,
            title = "Let's find your music",
            message = "Allow access to audio files so your songs can show up here. Nothing leaves your device.",
            modifier = Modifier.align(Alignment.Center)
        )
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(24.dp)
        ) {
            if (canAskAgain) {
                GradientPillButton(
                    "Allow access",
                    Icons.Rounded.LockOpen,
                    onRequest,
                    Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
            }
            TonalPillButton(
                "Open settings",
                Icons.Rounded.Settings,
                onGoToSettings,
                Modifier.fillMaxWidth()
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PermissionDeniedContentPreview() {
    PreviewScreen {
        PermissionDeniedContent(canAskAgain = true, onRequest = {}, onGoToSettings = {})
    }
}

@Preview(name = "Permanently denied")
@Composable
private fun PermissionDeniedContentSettingsPreview() {
    PreviewScreen {
        PermissionDeniedContent(canAskAgain = false, onRequest = {}, onGoToSettings = {})
    }
}
