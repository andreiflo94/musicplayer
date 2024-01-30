package com.example.musicplayer.mainfeature.presentation.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicplayer.R
import com.example.musicplayer.mainfeature.presentation.ui.screens.AppScreenState
import com.example.musicplayer.mainfeature.presentation.ui.screens.AudioFileState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppPlayerBar(
    appScreenState: AppScreenState,
    playPauseClick: () -> Unit,
    stopPlayingClick: () -> Unit
) {
    val audioFileState = appScreenState.audioFileState
    if (audioFileState != AudioFileState.IDLE && audioFileState != AudioFileState.STOPED) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onBackground
            ),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(2.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                // Display the album name
                Text(
                    modifier = Modifier
                        .padding(8.dp, 0.dp, 0.dp, 0.dp)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .basicMarquee(),
                    text = appScreenState.audioFileTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { playPauseClick() },
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
                        onClick = { stopPlayingClick() },
                        content = {
                            Icon(
                                painterResource(id = R.drawable.ic_not_close),
                                contentDescription = "Close"
                            )
                        }
                    )
                    ProgressBar(appScreenState)
                }
            }
        }
    }
}

@Composable
fun ProgressBar(appScreenState: AppScreenState) {
    val audioFileProgress = appScreenState.audioFileProgress
    LinearProgressIndicator(
        progress = audioFileProgress
    )
}

@Preview
@Composable
fun AppPlayerBarPreview() {
    AppPlayerBar(AppScreenState(
        audioFileTitle = "testing",
        audioFileState = AudioFileState.PLAYING,
        audioFileProgress = 0.3f
    ), {}, {})
}
