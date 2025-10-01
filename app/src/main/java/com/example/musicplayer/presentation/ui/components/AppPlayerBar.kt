package com.example.musicplayer.presentation.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.musicplayer.R
import com.example.musicplayer.presentation.viewmodels.MainActivityViewModel

enum class PlayerExpansionState { COLLAPSED, EXPANDED }

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun AppPlayerBar(
    playPauseClick: () -> Unit,
    stopPlayingClick: () -> Unit,
    playNextTrack: () -> Unit,
    playPreviousTrack: () -> Unit,
    onProgressUpdate: (progress: Long) -> Unit,
) {
    var expansionState by remember { mutableStateOf(PlayerExpansionState.COLLAPSED) }
    val isExpanded = expansionState == PlayerExpansionState.EXPANDED

    val playerHeight by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0.07f,
        label = "playerHeight"
    )
    val imageSize by animateDpAsState(targetValue = if (isExpanded) 150.dp else 40.dp)
    val cornerRadius by animateDpAsState(targetValue = if (isExpanded) 8.dp else 20.dp)

    val currentPlayPause by rememberUpdatedState(playPauseClick)
    val currentNext by rememberUpdatedState(playNextTrack)
    val currentPrev by rememberUpdatedState(playPreviousTrack)
    val currentStop by rememberUpdatedState(stopPlayingClick)
    val currentProgress by rememberUpdatedState(onProgressUpdate)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    when {
                        dragAmount < -10 -> expansionState = PlayerExpansionState.EXPANDED
                        dragAmount > 10 -> expansionState = PlayerExpansionState.COLLAPSED
                    }
                    change.consume()
                }
            }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(playerHeight)
                .shadow(4.dp)
                .background(MaterialTheme.colorScheme.onBackground)
                .clickable(enabled = !isExpanded) { expansionState = PlayerExpansionState.EXPANDED }
        ) {
            if (isExpanded) {
                ExpandedPlayerContent(
                    imageSize = imageSize,
                    cornerRadius = cornerRadius,
                    playPauseClick = currentPlayPause,
                    playNextTrack = currentNext,
                    playPreviousTrack = currentPrev,
                    stopPlayingClick = currentStop,
                    onProgressUpdate = currentProgress
                )
            } else {
                MiniPlayerContent(
                    imageSize = imageSize,
                    cornerRadius = cornerRadius,
                    playPauseClick = currentPlayPause,
                    playNextTrack = currentNext
                )
            }
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalFoundationApi::class, ExperimentalGlideComposeApi::class)
@Composable
private fun MiniPlayerContent(
    imageSize: Dp,
    cornerRadius: Dp,
    playPauseClick: () -> Unit,
    playNextTrack: () -> Unit,
) {
    val viewModel: MainActivityViewModel = hiltViewModel()
    val trackState = viewModel.trackState.collectAsState().value

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = trackState.trackArtUrl,
            contentDescription = "Album Art",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(imageSize)
                .clip(RoundedCornerShape(cornerRadius))
        )

        Text(
            text = trackState.trackName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
                .basicMarquee()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = playPauseClick, modifier = Modifier.size(40.dp)) {
                Icon(
                    painter = painterResource(id = if (trackState.isPlaying) R.drawable.ic_not_pause else R.drawable.ic_not_play),
                    contentDescription = if (trackState.isPlaying) "Pause" else "Play",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (trackState.hasNextMediaItem) {
                IconButton(onClick = playNextTrack, modifier = Modifier.size(40.dp)) {
                    Icon(
                        painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_next),
                        contentDescription = "Next",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ExpandedPlayerContent(
    imageSize: Dp,
    cornerRadius: Dp,
    playPauseClick: () -> Unit,
    playNextTrack: () -> Unit,
    playPreviousTrack: () -> Unit,
    stopPlayingClick: () -> Unit,
    onProgressUpdate: (progress: Long) -> Unit
) {
    val viewModel: MainActivityViewModel = hiltViewModel()
    val trackState = viewModel.trackState.collectAsState().value
    val trackProgress = viewModel.trackLiveProgress.collectAsState().value
    val trackProgressFormatted = viewModel.trackProgressFormatted.collectAsState().value

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
            model = trackState.trackArtUrl,
            contentDescription = "Album Art",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .size(imageSize)
                .clip(RoundedCornerShape(cornerRadius))
                .shadow(8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = trackState.trackName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.weight(1f))

        Slider(
            value = trackProgress,
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
                trackProgressFormatted,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
            Text(
                trackProgressFormatted,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = playPreviousTrack, modifier = Modifier.size(48.dp)) {
                Icon(
                    painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_next),
                    contentDescription = "Previous",
                    modifier = Modifier.rotate(180f),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { playPauseClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = if (trackState.isPlaying) R.drawable.ic_not_pause else R.drawable.ic_not_play),
                    contentDescription = if (trackState.isPlaying) "Pause" else "Play",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(
                onClick = playNextTrack,
                modifier = Modifier.size(48.dp),
                enabled = trackState.hasNextMediaItem
            ) {
                Icon(
                    painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_next),
                    contentDescription = "Next",
                    tint = if (trackState.hasNextMediaItem) MaterialTheme.colorScheme.primary
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
        playPauseClick = {},
        stopPlayingClick = {},
        playNextTrack = {},
        playPreviousTrack = {},
        onProgressUpdate = {},
    )
}
