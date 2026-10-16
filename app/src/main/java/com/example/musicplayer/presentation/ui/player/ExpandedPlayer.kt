package com.example.musicplayer.presentation.ui.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.util.lerp
import com.example.musicplayer.data.MusicUtils
import com.example.musicplayer.presentation.ui.components.AlbumArt
import com.example.musicplayer.presentation.ui.components.CircleIconButton
import com.example.musicplayer.presentation.ui.components.PlayPauseIcon
import com.example.musicplayer.presentation.ui.components.VinylDisc
import com.example.musicplayer.presentation.ui.components.WaveSeekBar
import com.example.musicplayer.presentation.ui.components.bounceClick
import com.example.musicplayer.presentation.ui.theme.Motion
import com.example.musicplayer.presentation.viewmodels.TrackState
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign

/**
 * Full-screen player, laid out once at full size. Its artwork doubles as the mini player's
 * thumbnail: it is scaled and translated from the mini slot to its real place as the sheet
 * opens, while the rest fades and rises in.
 */
@Composable
fun ExpandedPlayer(
    sheet: PlayerSheetState,
    trackState: TrackState,
    progress: () -> Float,
    elapsed: () -> String,
    actions: PlayerActions,
    miniArtCenter: Offset,
    miniArtSizePx: Float,
    miniArtCornerPx: Float,
) {
    val tracker = remember { ArtTracker() }
    val reveal = Modifier.graphicsLayer {
        val a = ((sheet.progress - 0.35f) / 0.65f).coerceIn(0f, 1f)
        alpha = a
        translationY = (1f - a) * 48.dp.toPx()
    }

    Column(
        Modifier
            .fillMaxSize()
            .onPlaced(tracker::onRootPlaced)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 28.dp)
    ) {
        PlayerHeader(onCollapse = sheet::collapse, modifier = reveal)

        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val side = min(maxWidth * 0.74f, maxHeight - 24.dp)
            // Static slot that reports where the artwork lives; the morph happens in its child.
            Box(
                Modifier
                    .size(side)
                    .onPlaced(tracker::onArtPlaced)
            ) {
                ArtStack(
                    trackState = trackState,
                    sheet = sheet,
                    actions = actions,
                    miniArtCornerPx = miniArtCornerPx,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val p = sheet.progress
                            val center = tracker.artCenter
                            if (center.isSpecified) {
                                val scale = lerp(miniArtSizePx / size.width, 1f, p)
                                scaleX = scale
                                scaleY = scale
                                translationX = (miniArtCenter.x - center.x) * (1f - p)
                                translationY = (miniArtCenter.y - center.y) * (1f - p)
                            }
                        },
                    miniArtSizePx = miniArtSizePx
                )
            }
        }

        Column(reveal) {
            TrackTitle(trackState)
            Spacer(Modifier.height(20.dp))
            SeekSection(trackState, progress, elapsed, actions.onSeek)
            Spacer(Modifier.height(12.dp))
            Controls(trackState, actions)
            Spacer(Modifier.height(28.dp))
        }
    }
}

/** Remembers where the artwork slot sits inside the player so the morph knows its start point. */
private class ArtTracker {
    private var root: LayoutCoordinates? = null
    private var art: LayoutCoordinates? = null
    var artCenter by mutableStateOf(Offset.Unspecified)
        private set

    fun onRootPlaced(coordinates: LayoutCoordinates) {
        root = coordinates
        update()
    }

    fun onArtPlaced(coordinates: LayoutCoordinates) {
        art = coordinates
        update()
    }

    private fun update() {
        val r = root ?: return
        val a = art ?: return
        if (r.isAttached && a.isAttached) {
            artCenter = r.localPositionOf(a, Offset(a.size.width / 2f, a.size.height / 2f))
        }
    }
}

@Composable
private fun PlayerHeader(onCollapse: () -> Unit, modifier: Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleIconButton(Icons.Rounded.KeyboardArrowDown, "Collapse player", onCollapse)
        Text(
            text = "NOW PLAYING",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.size(44.dp))
    }
}

/**
 * Album card with a vinyl record that slides out from behind it and spins while music plays.
 * Swipe the card sideways to skip; it flies off and the next one glides in.
 */
@Composable
private fun ArtStack(
    trackState: TrackState,
    sheet: PlayerSheetState,
    actions: PlayerActions,
    miniArtSizePx: Float,
    miniArtCornerPx: Float,
    modifier: Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    val expanded = sheet.isExpanded
    val isPlaying = trackState.isPlaying
    val hasNext by rememberUpdatedState(trackState.hasNextMediaItem)
    val currentActions by rememberUpdatedState(actions)

    val vinylOut by animateFloatAsState(
        targetValue = if (isPlaying) 1f else 0f,
        animationSpec = Motion.fluid(),
        label = "vinylOut"
    )
    var rotation by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(isPlaying, expanded) {
        if (!isPlaying || !expanded) return@LaunchedEffect
        var last = withFrameNanos { it }
        while (true) {
            withFrameNanos { now ->
                rotation = (rotation + (now - last) / 1_000_000_000f * DegreesPerSecond) % 360f
                last = now
            }
        }
    }
    val swipe = remember { Animatable(0f) }
    val bigCornerPx = with(LocalDensity.current) { 28.dp.toPx() }
    val shadowPx = with(LocalDensity.current) { 28.dp.toPx() }

    Box(modifier) {
        VinylDisc(
            labelColor = scheme.primary,
            accentColor = scheme.tertiary,
            rotation = { rotation },
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .graphicsLayer { translationX = vinylOut * sheet.progress * size.width * 0.22f }
        )
        AlbumArt(
            model = trackState.trackArtUrl,
            seed = trackState.trackPath,
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val p = sheet.progress
                    val minScale = miniArtSizePx / size.width
                    translationX = -vinylOut * p * size.width * 0.1f + swipe.value
                    rotationZ = swipe.value / size.width * 12f
                    alpha = 1f - (abs(swipe.value) / size.width * 0.7f).coerceIn(0f, 0.7f)
                    shape = RoundedCornerShape(lerp(miniArtCornerPx / minScale, bigCornerPx, p))
                    clip = true
                    shadowElevation = shadowPx * p
                    ambientShadowColor = scheme.primary
                    spotShadowColor = scheme.primary
                }
                .pointerInput(expanded) {
                    if (!expanded) return@pointerInput
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                val width = size.width.toFloat()
                                val direction = sign(swipe.value)
                                val canSkip = direction > 0 || hasNext
                                if (abs(swipe.value) > width * 0.28f && canSkip) {
                                    swipe.animateTo(direction * width * 1.1f, tween(160))
                                    if (direction > 0) currentActions.onPrevious() else currentActions.onNext()
                                    swipe.snapTo(-direction * width * 0.5f)
                                    swipe.animateTo(0f, Motion.fluid())
                                } else {
                                    swipe.animateTo(0f, Motion.bouncy())
                                }
                            }
                        },
                        onDragCancel = { scope.launch { swipe.animateTo(0f, Motion.bouncy()) } },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch { swipe.snapTo(swipe.value + dragAmount) }
                        }
                    )
                }
        )
    }
}

private const val DegreesPerSecond = 200f // ≈ 33⅓ rpm

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TrackTitle(trackState: TrackState) {
    AnimatedContent(
        targetState = trackState,
        contentKey = { it.trackPath },
        transitionSpec = {
            (slideInVertically(Motion.fluid()) { it / 2 } + fadeIn()) togetherWith
                    (slideOutVertically(Motion.fluid()) { -it / 2 } + fadeOut())
        },
        label = "trackTitle"
    ) { track ->
        Column(Modifier.fillMaxWidth()) {
            Text(
                text = track.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                modifier = Modifier.basicMarquee()
            )
            Text(
                text = track.artist.ifBlank { "Unknown artist" },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                modifier = Modifier.basicMarquee()
            )
        }
    }
}

@Composable
private fun SeekSection(
    trackState: TrackState,
    progress: () -> Float,
    elapsed: () -> String,
    onSeek: (Float) -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    var scrub by remember { mutableStateOf<Float?>(null) }
    WaveSeekBar(
        progress = progress,
        isPlaying = trackState.isPlaying,
        onSeek = onSeek,
        onScrub = { scrub = it },
        activeColor = scheme.primary,
        inactiveColor = scheme.onSurface.copy(alpha = 0.14f)
    )
    Row(Modifier.fillMaxWidth()) {
        ElapsedText(scrub, trackState.durationMs, elapsed)
        Spacer(Modifier.weight(1f))
        Text(
            text = trackState.trackDurationFormatted,
            style = MaterialTheme.typography.labelMedium,
            color = scheme.onSurfaceVariant
        )
    }
}

/** Its own scope: the ticking clock recomposes only this text. */
@Composable
private fun ElapsedText(scrub: Float?, durationMs: Long, elapsed: () -> String) {
    Text(
        text = if (scrub != null) MusicUtils.formatDurationFromMillis((scrub * durationMs).toLong()) else elapsed(),
        style = MaterialTheme.typography.labelMedium,
        color = if (scrub != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun Controls(trackState: TrackState, actions: PlayerActions) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ControlIcon(
            Icons.Rounded.SkipPrevious,
            "Previous",
            enabled = true,
            onClick = actions.onPrevious
        )
        BigPlayButton(isPlaying = trackState.isPlaying, onClick = actions.onPlayPause)
        ControlIcon(
            Icons.Rounded.SkipNext,
            "Next",
            enabled = trackState.hasNextMediaItem,
            onClick = actions.onNext
        )
    }
}

@Composable
private fun ControlIcon(
    icon: ImageVector,
    description: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val fade by animateFloatAsState(if (enabled) 1f else 0.3f, label = "controlAlpha")
    Icon(
        imageVector = icon,
        contentDescription = description,
        tint = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .size(60.dp)
            .graphicsLayer { alpha = fade }
            .bounceClick(
                enabled = enabled,
                pressedScale = 0.8f,
                shape = CircleShape,
                onClick = onClick
            )
            .padding(12.dp)
    )
}

/**
 * The halo's color for an [energy] that springs between 0 (paused) and 1 (playing). Motion.fluid()
 * overshoots, so energy dips just below 0 on pause, and Color rejects a negative alpha.
 */
internal fun haloColor(primary: Color, energy: Float): Color =
    primary.copy(alpha = 0.22f * energy.coerceIn(0f, 1f))

/** Gradient play button with a halo that breathes while music plays. */
@Composable
private fun BigPlayButton(isPlaying: Boolean, onClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val breath by rememberInfiniteTransition(label = "breath").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1400, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "breathValue"
    )
    val energy by animateFloatAsState(
        if (isPlaying) 1f else 0f,
        Motion.fluid(),
        label = "haloEnergy"
    )

    Box(
        modifier = Modifier
            .size(96.dp)
            .drawBehind {
                val halo = size.minDimension / 2f * (0.78f + 0.2f * breath * energy)
                drawCircle(haloColor(scheme.primary, energy), halo)
            },
        contentAlignment = Alignment.Center
    ) {
        PlayPauseIcon(
            isPlaying = isPlaying,
            color = scheme.onPrimary,
            modifier = Modifier
                .size(76.dp)
                .bounceClick(pressedScale = 0.88f, shape = CircleShape, onClick = onClick)
                .background(Brush.linearGradient(listOf(scheme.primary, scheme.tertiary)))
                .padding(22.dp)
        )
    }
}
