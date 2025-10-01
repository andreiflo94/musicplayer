package com.example.musicplayer.presentation.ui.screens

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import com.example.musicplayer.presentation.viewmodels.MainActivityViewModel

@OptIn(UnstableApi::class)
@Composable
fun HomeScreen(
    content: @Composable (PaddingValues) -> Unit,
    audioPlayer: @Composable ColumnScope.() -> Unit,
) {
    val viewModel: MainActivityViewModel = hiltViewModel()
    val trackState = viewModel.trackState.collectAsState().value

    val stopped = trackState.stopped
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp > 600
    val bottomPadding = if (isTablet) 0.dp else 56.dp

    Box(modifier = Modifier.fillMaxSize()) {
        content(PaddingValues())

        if (!stopped) {
            Column(
                modifier = Modifier
                    .padding(bottom = bottomPadding)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                audioPlayer()
            }
        }
    }
}
