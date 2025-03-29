package com.example.musicplayer.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.example.musicplayer.R
import com.example.musicplayer.presentation.viewmodels.AudioState

@Composable
fun HomeScreen(
    audioState: State<AudioState>,
    content: @Composable (PaddingValues) -> Unit,
    audioPlayer: @Composable ColumnScope.() -> Unit,
) {
    val animatedVisibility = audioState.value.stopped
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp > 600
    var bottomPadding = 56.dp
    if (isTablet) {
        bottomPadding = 0.dp
    }

    Box(modifier = Modifier.fillMaxSize()) {
        content(PaddingValues())
        if (!animatedVisibility) {
            Column(
                modifier = Modifier
                    .padding(paddingValues = PaddingValues(bottom = bottomPadding))
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)

            ) {
                audioPlayer()
            }
        }
    }
}

