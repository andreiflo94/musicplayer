package com.example.musicplayer.mainfeature.presentation.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicplayer.R
import com.example.musicplayer.mainfeature.presentation.viewmodels.AudioState

@Composable
fun AppPlayerBar(
    audioState: AudioState,
    playPauseClick: () -> Unit,
    stopPlayingClick: () -> Unit,
    onProgressUpdate: (progress: Long) -> Unit
) {
    if (!audioState.stopped) {
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
                TrackName(audioState.trackName)
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
                            if (audioState.isPlaying) {
                                Icon(
                                    painterResource(id = R.drawable.ic_not_pause),
                                    contentDescription = "Pause"
                                )
                            } else {
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
                }
                ProgressBar(audioState, onProgressUpdate)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun TrackName(trackName: String) {
    Text(
        modifier = Modifier
            .padding(10.dp, 10.dp, 10.dp, 10.dp)
            .wrapContentWidth()
            .wrapContentHeight()
            .basicMarquee(),
        text = trackName,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun ProgressBar(
    audioState: AudioState,
    onProgressUpdate: (progress: Long) -> Unit
) {
    Slider(
        value = audioState.progress,
        onValueChange = {
            onProgressUpdate(it.toLong())
        },
        valueRange = 0f..100f
    )
}

@Preview
@Composable
fun AppPlayerBarPreview() {
    AppPlayerBar(AudioState(
        trackName = "example.mp3", isPlaying = true, progress = 30.0f, stopped = false
    ), {}, {}, {})
}
