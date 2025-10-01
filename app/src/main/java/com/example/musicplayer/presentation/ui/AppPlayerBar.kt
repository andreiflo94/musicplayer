package com.example.musicplayer.presentation.ui

import androidx.activity.ComponentActivity
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
import androidx.compose.ui.platform.LocalContext
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
fun AppPlayerBar() {
    var expansionState by remember { mutableStateOf(PlayerExpansionState.COLLAPSED) }
    val isExpanded = expansionState == PlayerExpansionState.EXPANDED

    val playerHeight by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0.07f, label = "playerHeight"
    )
    val imageSize by animateDpAsState(targetValue = if (isExpanded) 150.dp else 40.dp)
    val cornerRadius by animateDpAsState(targetValue = if (isExpanded) 8.dp else 20.dp)

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
                .clickable(enabled = !isExpanded) {
                    expansionState = PlayerExpansionState.EXPANDED
                }
        ) {
            if (isExpanded) {
                ExpandedPlayerContent(imageSize, cornerRadius)
            } else {
                MiniPlayerContent(imageSize, cornerRadius)
            }
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalFoundationApi::class, ExperimentalGlideComposeApi::class)
@Composable
private fun MiniPlayerContent(imageSize: Dp, cornerRadius: Dp) {
    val viewModel: MainActivityViewModel =
        hiltViewModel<MainActivityViewModel>(LocalContext.current as ComponentActivity)
    val trackState = viewModel.trackState.collectAsState().value

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = trackState.trackArtUrl,
            contentDescription = "Album Art",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(imageSize).clip(RoundedCornerShape(cornerRadius))
        )

        Text(
            text = trackState.trackName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f).padding(horizontal = 12.dp).basicMarquee()
        )

        MiniPlayerControls(
            isPlaying = trackState.isPlaying,
            hasNext = trackState.hasNextMediaItem,
            onPlayPause = { viewModel.playPauseClick() },
            onNext = { viewModel.skipForward() }
        )
    }
}

@Composable
private fun MiniPlayerControls(
    isPlaying: Boolean,
    hasNext: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onPlayPause, modifier = Modifier.size(40.dp)) {
            Icon(
                painter = painterResource(
                    id = if (isPlaying) R.drawable.ic_not_pause else R.drawable.ic_not_play
                ),
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        if (hasNext) {
            IconButton(onClick = onNext, modifier = Modifier.size(40.dp)) {
                Icon(
                    painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_next),
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ExpandedPlayerContent(imageSize: Dp, cornerRadius: Dp) {
    val viewModel: MainActivityViewModel =
        hiltViewModel<MainActivityViewModel>(LocalContext.current as ComponentActivity)
    val trackState = viewModel.trackState.collectAsState().value

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ExpandedPlayerHeader(onStop = { viewModel.stopPlaying() })
        Spacer(modifier = Modifier.height(24.dp))
        ExpandedAlbumArt(trackState.trackArtUrl, imageSize, cornerRadius)
        Spacer(modifier = Modifier.height(32.dp))
        ExpandedTrackInfo(trackState.trackName)
        Spacer(modifier = Modifier.weight(1f))
        ExpandedTrackProgress(
            trackDurationFormatted = trackState.trackDurationFormatted,
            onSeek = { viewModel.onProgressUpdate(it.toLong()) }
        )
        Spacer(modifier = Modifier.height(24.dp))
        ExpandedPlayerControls(
            isPlaying = trackState.isPlaying,
            hasNext = trackState.hasNextMediaItem,
            onPrevious = { viewModel.playPrevious() },
            onPlayPause = { viewModel.playPauseClick() },
            onNext = { viewModel.skipForward() }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ExpandedPlayerHeader(onStop: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Now Playing", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        IconButton(onClick = onStop) {
            Icon(
                painter = painterResource(id = R.drawable.ic_not_close),
                contentDescription = "Stop",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ExpandedAlbumArt(url: String?, imageSize: Dp, cornerRadius: Dp) {
    GlideImage(
        model = url,
        contentDescription = "Album Art",
        contentScale = ContentScale.FillWidth,
        modifier = Modifier.size(imageSize).clip(RoundedCornerShape(cornerRadius)).shadow(8.dp)
    )
}

@Composable
private fun ExpandedTrackInfo(trackName: String) {
    Text(
        text = trackName,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
private fun ExpandedTrackProgress(trackDurationFormatted: String, onSeek: (Float) -> Unit) {
    val viewModel: MainActivityViewModel =
        hiltViewModel<MainActivityViewModel>(LocalContext.current as ComponentActivity)
    val trackProgress = viewModel.trackLiveProgress.collectAsState().value
    val trackProgressFormatted = viewModel.trackProgressFormatted.collectAsState().value
    Slider(
        value = trackProgress,
        onValueChange = onSeek,
        valueRange = 0f..100f,
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.primary,
            activeTrackColor = MaterialTheme.colorScheme.primary,
            inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
    )

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(trackProgressFormatted, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
        Text(trackDurationFormatted, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
    }
}

@Composable
private fun ExpandedPlayerControls(
    isPlaying: Boolean,
    hasNext: Boolean,
    onPrevious: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious, modifier = Modifier.size(48.dp)) {
            Icon(
                painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_next),
                contentDescription = "Previous",
                modifier = Modifier.rotate(180f),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Box(
            modifier = Modifier.size(64.dp).clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable { onPlayPause() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = if (isPlaying) R.drawable.ic_not_pause else R.drawable.ic_not_play),
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(32.dp)
            )
        }

        IconButton(
            onClick = onNext,
            modifier = Modifier.size(48.dp),
            enabled = hasNext
        ) {
            Icon(
                painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_next),
                contentDescription = "Next",
                tint = if (hasNext) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
        }
    }
}

@Preview
@Composable
fun AppPlayerBarPreview() {
    AppPlayerBar()
}
