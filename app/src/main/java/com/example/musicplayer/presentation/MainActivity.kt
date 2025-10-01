package com.example.musicplayer.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.musicplayer.presentation.ui.screens.RequiredPermissionScreen
import com.example.musicplayer.presentation.ui.theme.MusicplayerTheme
import dagger.hilt.android.AndroidEntryPoint

@UnstableApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicplayerTheme(dynamicColor = false) {
                navController = rememberNavController()
                MusicPlayerApp()
            }
        }
    }

    @OptIn(UnstableApi::class)
    @Composable
    fun MusicPlayerApp() {
        RequiredPermissionScreen()
    }
}

