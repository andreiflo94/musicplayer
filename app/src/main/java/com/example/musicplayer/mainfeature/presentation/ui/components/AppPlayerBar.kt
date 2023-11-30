package com.example.musicplayer.mainfeature.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicplayer.R
import com.example.musicplayer.mainfeature.presentation.ui.screens.AppScreenState
import com.example.musicplayer.mainfeature.presentation.ui.screens.AudioFileState

@Composable
fun AppPlayerBar(appScreenState: AppScreenState) {
    val audioFileProgress = appScreenState.audioFileProgress
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(8.dp)
            .background(Color.LightGray)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { /* Start button click action */ },
                modifier = Modifier
                    .padding(8.dp),
                content = {
                    if (appScreenState.audioFileState == AudioFileState.PLAYING) {
                        Icon(
                            painterResource(id = R.drawable.ic_not_pause),
                            contentDescription = "Pause"
                        )
                    } else if (appScreenState.audioFileState == AudioFileState.PAUSED
                        || appScreenState.audioFileState == AudioFileState.STOPED
                    ) {
                        Icon(
                            painterResource(id = R.drawable.ic_not_play),
                            contentDescription = "Start"
                        )
                    }
                }
            )
            IconButton(
                onClick = { /* Start button click action */ },
                modifier = Modifier
                    .padding(8.dp),
                content = {
                    Icon(
                        painterResource(id = R.drawable.ic_not_close),
                        contentDescription = "Close"
                    )
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            ProgressBar(audioFileProgress)
        }
    }
}

@Composable
fun ProgressBar(audioFileProgress: Float) {
    LinearProgressIndicator(
        progress = audioFileProgress,
        modifier = Modifier
            .padding(end = 8.dp)
    )
}

@Preview
@Composable
fun AppPlayerBarPreview() {
    AppPlayerBar(AppScreenState(audioFileState = AudioFileState.PLAYING, 0.3f))
}
