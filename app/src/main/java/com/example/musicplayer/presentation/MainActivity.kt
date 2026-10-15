package com.example.musicplayer.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import com.example.musicplayer.presentation.ui.screens.RequiredPermissionScreen
import com.example.musicplayer.presentation.ui.theme.MusicplayerTheme
import com.example.musicplayer.presentation.viewmodels.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@UnstableApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val themeSettings by themeViewModel.settings.collectAsStateWithLifecycle()
            MusicplayerTheme(settings = themeSettings) {
                RequiredPermissionScreen()
            }
        }
    }
}
