package com.example.musicplayer.presentation.ui

import androidx.activity.ComponentActivity
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
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

    var dragProgress by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val snapTarget = if (isExpanded) 1f else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = if (isDragging) dragProgress else snapTarget,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "playerExpansion"
    )

    val playerHeight = 0.08f + animatedProgress * (1f - 0.08f)

    val imageSize = lerp(48.dp, 220.dp, animatedProgress)

    val cornerRadius = lerp(8.dp, 16.dp, animatedProgress)

    val miniAlpha = (1f - animatedProgress * 3.5f).coerceIn(0f, 1f)
    val expandedAlpha = ((animatedProgress - 0.6f) * 2.5f).coerceIn(0f, 1f)

    val miniControlsScale = lerp(1.dp, 0.85.dp, animatedProgress).value

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = {
                        isDragging = true
                        dragProgress = if (isExpanded) 1f else 0f
                    },
                    onDragEnd = {
                        isDragging = false
                        expansionState = if (dragProgress > 0.4f)
                            PlayerExpansionState.EXPANDED
                        else
                            PlayerExpansionState.COLLAPSED
                    },
                    onDragCancel = {
                        isDragging = false
                        // Revine la starea anterioară
                        dragProgress = if (isExpanded) 1f else 0f
                    },
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        val delta = -dragAmount / size.height.toFloat()
                        dragProgress = (dragProgress + delta).coerceIn(0f, 1f)
                    }
                )
            }
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(playerHeight)
                .clickable(
                    enabled = !isExpanded,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    expansionState = PlayerExpansionState.EXPANDED
                },
            tonalElevation = 6.dp,
            shape = RoundedCornerShape(
                topStart = lerp(12.dp, 24.dp, animatedProgress),
                topEnd = lerp(12.dp, 24.dp, animatedProgress),
                bottomStart = 0.dp,
                bottomEnd = 0.dp
            ),
            color = MaterialTheme.colorScheme.surface
        ) {
            Box(modifier = Modifier.fillMaxSize()) {

                // Mini player
                if (miniAlpha > 0f) {
                    MiniPlayerContent(
                        imageSize = imageSize,
                        cornerRadius = cornerRadius,
                        controlsScale = miniControlsScale,
                        modifier = Modifier.alpha(miniAlpha)
                    )
                }

                // Expanded player
                if (expandedAlpha > 0f) {
                    ExpandedPlayerContent(
                        imageSize = imageSize,
                        cornerRadius = cornerRadius,
                        modifier = Modifier.alpha(expandedAlpha),
                        onCollapse = { expansionState = PlayerExpansionState.COLLAPSED }
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp)
                        .size(width = lerp(24.dp, 36.dp, animatedProgress), height = 4.dp)
                        .alpha(0.15f + animatedProgress * 0.25f)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.onSurface)
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
    controlsScale: Float = 1f,
    modifier: Modifier = Modifier
) {
    val viewModel: MainActivityViewModel =
        hiltViewModel(LocalContext.current as ComponentActivity)
    val trackState = viewModel.trackState.collectAsState().value

    Row(
        modifier = modifier
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

        Spacer(modifier = Modifier.width(12.dp))

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
            onNext = { viewModel.skipForward() },
            scale = controlsScale
        )
    }
}

@Composable
private fun MiniPlayerControls(
    isPlaying: Boolean,
    hasNext: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    scale: Float = 1f
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.scale(scale)
    ) {
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
private fun ExpandedPlayerContent(
    imageSize: Dp,
    cornerRadius: Dp,
    modifier: Modifier = Modifier,
    onCollapse: () -> Unit = {}
) {
    val viewModel: MainActivityViewModel =
        hiltViewModel<MainActivityViewModel>(LocalContext.current as ComponentActivity)
    val trackState = viewModel.trackState.collectAsState().value

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        ExpandedPlayerHeader(onCollapse = onCollapse)

        Spacer(modifier = Modifier.height(32.dp))

        ExpandedAlbumArt(
            url = trackState.trackArtUrl,
            imageSize = imageSize,
            cornerRadius = cornerRadius
        )

        Spacer(modifier = Modifier.height(32.dp))

        ExpandedTrackInfo(trackName = trackState.trackName)

        Spacer(modifier = Modifier.weight(1f))

        ExpandedTrackProgress(
            trackDurationFormatted = trackState.trackDurationFormatted,
            onSeek = { viewModel.onProgressUpdate(it.toLong()) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        ExpandedPlayerControls(
            isPlaying = trackState.isPlaying,
            hasNext = trackState.hasNextMediaItem,
            onPrevious = { viewModel.playPrevious() },
            onPlayPause = { viewModel.playPauseClick() },
            onNext = { viewModel.skipForward() }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ExpandedPlayerHeader(onCollapse: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Now Playing",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        IconButton(onClick = onCollapse) {
            Icon(
                painter = painterResource(id = R.drawable.ic_not_close),
                contentDescription = "Collapse",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ExpandedAlbumArt(url: String?, imageSize: Dp, cornerRadius: Dp) {
    // Umbra subtilă în jurul albumului pentru depth
    Box(
        modifier = Modifier
            .size(imageSize + 8.dp)
            .clip(RoundedCornerShape(cornerRadius + 4.dp))
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),
        contentAlignment = Alignment.Center
    ) {
        GlideImage(
            model = url,
            contentDescription = "Album Art",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(imageSize)
                .clip(RoundedCornerShape(cornerRadius))
        )
    }
}

@Composable
private fun ExpandedTrackInfo(trackName: String) {
    Text(
        text = trackName,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.fillMaxWidth()
    )
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
private fun ExpandedTrackProgress(trackDurationFormatted: String, onSeek: (Float) -> Unit) {
    val viewModel: MainActivityViewModel =
        hiltViewModel<MainActivityViewModel>(LocalContext.current as ComponentActivity)
    val trackProgress = viewModel.trackLiveProgress.collectAsState().value
    val trackProgressFormatted = viewModel.trackProgressFormatted.collectAsState().value

    Column(modifier = Modifier.fillMaxWidth()) {
        Slider(
            value = trackProgress,
            onValueChange = onSeek,
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = trackProgressFormatted,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = trackDurationFormatted,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
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
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPrevious,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = painterResource(
                    id = androidx.media3.session.R.drawable.media3_icon_next
                ),
                contentDescription = "Previous",
                modifier = Modifier
                    .size(28.dp)
                    .rotate(180f),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        FilledIconButton(
            onClick = onPlayPause,
            modifier = Modifier.size(72.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                painter = painterResource(
                    id = if (isPlaying) R.drawable.ic_not_pause else R.drawable.ic_not_play
                ),
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(32.dp)
            )
        }

        IconButton(
            onClick = onPlayPause,
            enabled = hasNext,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = painterResource(
                    id = androidx.media3.session.R.drawable.media3_icon_next
                ),
                contentDescription = "Next",
                modifier = Modifier.size(28.dp),
                tint = if (hasNext)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}