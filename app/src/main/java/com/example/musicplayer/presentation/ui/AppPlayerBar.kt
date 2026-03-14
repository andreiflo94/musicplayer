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
        targetValue = if (isExpanded) 1f else 0.08f,
        label = "playerHeight"
    )

    val imageSize by animateDpAsState(
        targetValue = if (isExpanded) 220.dp else 48.dp
    )

    val cornerRadius by animateDpAsState(
        targetValue = if (isExpanded) 12.dp else 12.dp
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
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

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(playerHeight)
                .clickable(enabled = !isExpanded) {
                    expansionState = PlayerExpansionState.EXPANDED
                },
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surface
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
        hiltViewModel(LocalContext.current as ComponentActivity)

    val trackState = viewModel.trackState.collectAsState().value

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
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

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = trackState.trackName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .basicMarquee()
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

        IconButton(onClick = onPlayPause) {

            Icon(
                painter = painterResource(
                    id = if (isPlaying) R.drawable.ic_not_pause else R.drawable.ic_not_play
                ),
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        if (hasNext) {

            IconButton(onClick = onNext) {

                Icon(
                    painter = painterResource(
                        id = androidx.media3.session.R.drawable.media3_icon_next
                    ),
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "Now Playing",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        IconButton(onClick = onStop) {

            Icon(
                painter = painterResource(id = R.drawable.ic_not_close),
                contentDescription = "Stop",
                tint = MaterialTheme.colorScheme.onSurface
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
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(imageSize)
            .clip(RoundedCornerShape(cornerRadius))
    )
}

@Composable
private fun ExpandedTrackInfo(trackName: String) {

    Text(
        text = trackName,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = onPrevious) {

            Icon(
                painter = painterResource(
                    id = androidx.media3.session.R.drawable.media3_icon_next
                ),
                contentDescription = "Previous",
                modifier = Modifier.rotate(180f),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable { onPlayPause() },
            contentAlignment = Alignment.Center
        ) {

            Icon(
                painter = painterResource(
                    id = if (isPlaying) R.drawable.ic_not_pause else R.drawable.ic_not_play
                ),
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(34.dp)
            )
        }

        IconButton(
            onClick = onNext,
            enabled = hasNext
        ) {

            Icon(
                painter = painterResource(
                    id = androidx.media3.session.R.drawable.media3_icon_next
                ),
                contentDescription = "Next",
                tint = if (hasNext)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

@Preview
@Composable
fun AppPlayerBarPreview() {
    AppPlayerBar()
}
