package com.example.musicplayer.mainfeature.presentation.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
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
    playNextTrack: () -> Unit,
    playPrevious: () -> Unit,
    onProgressUpdate: (progress: Long) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.app_player_bar_height))
            .background(MaterialTheme.colorScheme.tertiary)
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
                onClick = { playPrevious() },
                modifier = Modifier.rotate(180f),
                content = {
                    Icon(
                        painterResource(id = androidx.media3.session.R.drawable.media3_icon_next),
                        contentDescription = "SkipPrevious",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            )
            IconButton(
                onClick = { playPauseClick() },
                content = {
                    if (audioState.isPlaying) {
                        Icon(
                            painterResource(id = R.drawable.ic_not_pause),
                            contentDescription = "Pause",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    } else {
                        Icon(
                            painterResource(id = R.drawable.ic_not_play),
                            contentDescription = "Start",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            )
            if (audioState.hasNextMediaItem) {
                IconButton(
                    onClick = { playNextTrack() },
                    content = {
                        Icon(
                            painterResource(id = androidx.media3.session.R.drawable.media3_icon_next),
                            contentDescription = "SkipForward",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                )
            }
            IconButton(
                onClick = { stopPlayingClick() },
                content = {
                    Icon(
                        painterResource(id = R.drawable.ic_not_close),
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            )
        }
        ProgressBar(audioState, onProgressUpdate)
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
        color = MaterialTheme.colorScheme.secondary
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
        valueRange = 0f..100f,
        colors = SliderDefaults.colors(
            inactiveTrackColor = Color.White,
            activeTrackColor = MaterialTheme.colorScheme.secondary,
            activeTickColor = MaterialTheme.colorScheme.secondary,
            inactiveTickColor = Color.White
        )
    )
}

@Preview
@Composable
fun AppPlayerBarPreview() {
    AppPlayerBar(AudioState(
        trackName = "example.mp3", isPlaying = true, progress = 30.0f, stopped = false,
        hasNextMediaItem = true
    ), {}, {}, {}, {}, {})
}
