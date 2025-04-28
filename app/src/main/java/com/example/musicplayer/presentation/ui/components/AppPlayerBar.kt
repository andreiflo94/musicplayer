package com.example.musicplayer.presentation.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.musicplayer.R
import com.example.musicplayer.data.MusicUtils
import com.example.musicplayer.presentation.viewmodels.AudioState

enum class PlayerExpansionState {
    COLLAPSED, EXPANDED
}

@Composable
fun AppPlayerBar(
    audioState: AudioState,
    playPauseClick: () -> Unit,
    stopPlayingClick: () -> Unit,
    playNextTrack: () -> Unit,
    playPreviousTrack: () -> Unit,
    onProgressUpdate: (progress: Long) -> Unit,
) {
    var expansionState by remember { mutableStateOf(PlayerExpansionState.COLLAPSED) }
    val isExpanded = expansionState == PlayerExpansionState.EXPANDED

    val playerHeight by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0.070f, label = "playerHeight"
    )

    val imageSize by animateDpAsState(
        targetValue = if (isExpanded) 150.dp else 40.dp, label = "imageSize"
    )

    val cornerRadius by animateDpAsState(
        targetValue = if (isExpanded) 8.dp else 20.dp, label = "cornerRadius"
    )

    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .pointerInput(Unit) {
            detectVerticalDragGestures(onDragEnd = {},
                onDragCancel = {}, onVerticalDrag = { change, dragAmount ->
                    when {
                        dragAmount < -10 -> expansionState = PlayerExpansionState.EXPANDED
                        dragAmount > 10 -> expansionState = PlayerExpansionState.COLLAPSED
                    }
                    change.consume()
                })
        }) {

        Box(modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .fillMaxHeight(playerHeight)
            .shadow(4.dp)
            .background(
                color = MaterialTheme.colorScheme.onBackground
            )
            .clickable(enabled = !isExpanded) {
                expansionState = PlayerExpansionState.EXPANDED
            }) {
            if (isExpanded) {
                ExpandedPlayerContent(
                    audioState = audioState,
                    imageSize = imageSize,
                    cornerRadius = cornerRadius,
                    trackName = audioState.trackName,
                    playPauseClick = playPauseClick,
                    playNextTrack = playNextTrack,
                    playPreviousTrack = playPreviousTrack,
                    onProgressUpdate = onProgressUpdate,
                    stopPlayingClick = stopPlayingClick
                )
            } else {
                MiniPlayerContent(
                    audioState = audioState,
                    imageSize = imageSize,
                    cornerRadius = cornerRadius,
                    playPauseClick = playPauseClick,
                    playNextTrack = playNextTrack,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalGlideComposeApi::class)
@Composable
private fun MiniPlayerContent(
    audioState: AudioState,
    imageSize: androidx.compose.ui.unit.Dp,
    cornerRadius: androidx.compose.ui.unit.Dp,
    playPauseClick: () -> Unit,
    playNextTrack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlideImage(
                model = audioState.trackArtUrl,
                contentDescription = "Album Art",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(imageSize)
                    .clip(RoundedCornerShape(cornerRadius))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = audioState.trackName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.basicMarquee()
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { playPauseClick() }, modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (audioState.isPlaying) R.drawable.ic_not_pause else R.drawable.ic_not_play
                        ),
                        contentDescription = if (audioState.isPlaying) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                if (audioState.hasNextMediaItem) {
                    IconButton(
                        onClick = { playNextTrack() }, modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_next),
                            contentDescription = "Next",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        LinearProgressIndicator(
            progress = { audioState.progress / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ExpandedPlayerContent(
    audioState: AudioState,
    imageSize: androidx.compose.ui.unit.Dp,
    cornerRadius: androidx.compose.ui.unit.Dp,
    trackName: String,
    playPauseClick: () -> Unit,
    playNextTrack: () -> Unit,
    playPreviousTrack: () -> Unit,
    onProgressUpdate: (progress: Long) -> Unit,
    stopPlayingClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Now Playing",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            IconButton(onClick = stopPlayingClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_not_close),
                    contentDescription = "Stop",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))


        GlideImage(
            model = audioState.trackArtUrl,
            contentDescription = "Album Art",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .size(imageSize)
                .clip(RoundedCornerShape(cornerRadius))
                .shadow(8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = trackName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.weight(1f))


        Slider(
            value = audioState.progress,
            onValueChange = { onProgressUpdate(it.toLong()) },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = audioState.trackProgressFormatted,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )

            Text(
                text = audioState.trackDurationFormatted,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Control buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { playPreviousTrack() }, modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_next),
                    contentDescription = "Previous",
                    modifier = Modifier.rotate(180f),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Play/Pause button (larger)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { playPauseClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        id = if (audioState.isPlaying) R.drawable.ic_not_pause else R.drawable.ic_not_play
                    ),
                    contentDescription = if (audioState.isPlaying) "Pause" else "Play",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(
                onClick = { playNextTrack() },
                modifier = Modifier.size(48.dp),
                enabled = audioState.hasNextMediaItem
            ) {
                Icon(
                    painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_next),
                    contentDescription = "Next",
                    tint = if (audioState.hasNextMediaItem) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview
@Composable
fun YouTubeMusicPlayerBarPreview() {
    AppPlayerBar(
        audioState = AudioState(
            trackName = "Bohemian Rhapsody",
            trackArtUrl = "",
            isPlaying = true,
            progress = 30.0f,
            trackProgressFormatted = "",
            trackDurationFormatted = "",
            stopped = false,
            hasNextMediaItem = true
        ),
        playPauseClick = {},
        stopPlayingClick = {},
        playNextTrack = {},
        playPreviousTrack = {},
        onProgressUpdate = {},
    )
}