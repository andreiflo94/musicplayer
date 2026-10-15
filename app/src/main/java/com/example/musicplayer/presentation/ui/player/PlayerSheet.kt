package com.example.musicplayer.presentation.ui.player

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.example.musicplayer.presentation.ui.preview.PreviewData
import com.example.musicplayer.presentation.ui.preview.PreviewScreen
import com.example.musicplayer.presentation.viewmodels.TrackState

@Immutable
class PlayerActions(
    val onPlayPause: () -> Unit,
    val onNext: () -> Unit,
    val onPrevious: () -> Unit,
    /** Seek to a 0..1 fraction of the track. */
    val onSeek: (Float) -> Unit,
)

object PlayerDimens {
    val MiniHeight = 68.dp
    val MiniInset = 12.dp
    val MiniArtSize = 46.dp
    val MiniArtCorner = 14.dp
    val MiniCorner = 24.dp
}

/**
 * The player as one full-screen panel. Collapsed, it is pushed down so only a floating
 * mini-player card shows (everything else is clipped away); expanding slides the panel up
 * while the clip grows to fill the screen. Position, clip, corners and shadow are all
 * graphics-layer properties, so the morph never re-measures or recomposes.
 *
 * @param collapsedBottomOffset gap between the collapsed card and the bottom of the screen.
 * @param progress current track position (0..1), read lazily.
 * @param elapsed formatted current position, read lazily.
 */
@Composable
fun PlayerSheet(
    state: PlayerSheetState,
    trackState: TrackState,
    progress: () -> Float,
    elapsed: () -> String,
    actions: PlayerActions,
    collapsedBottomOffset: Dp,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val miniHeightPx = with(density) { PlayerDimens.MiniHeight.toPx() }
    val insetPx = with(density) { PlayerDimens.MiniInset.toPx() }
    val cornerPx = with(density) { PlayerDimens.MiniCorner.toPx() }
    val bottomOffsetPx = with(density) { collapsedBottomOffset.toPx() }
    val shadowPx = with(density) { 14.dp.toPx() }

    val scheme = MaterialTheme.colorScheme
    val miniColor = scheme.surfaceContainerHighest
    val fullColor = scheme.background
    val glow = scheme.primary.copy(alpha = 0.32f)

    BackHandler(enabled = state.isExpanded) { state.collapse() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged {
                state.travel = (it.height - miniHeightPx - bottomOffsetPx).coerceAtLeast(1f)
            }
            .graphicsLayer {
                val p = state.progress
                translationY =
                    (1f - p) * (size.height - miniHeightPx - bottomOffsetPx).coerceAtLeast(0f)
                shape = PanelShape(
                    visibleHeight = lerp(miniHeightPx, size.height, p),
                    inset = lerp(insetPx, 0f, p),
                    corner = lerp(cornerPx, 0f, p)
                )
                clip = true
                shadowElevation = shadowPx * (1f - p)
                ambientShadowColor = glow
                spotShadowColor = glow
            }
            .drawBehind {
                val p = state.progress
                drawRect(lerp(miniColor, fullColor, p))
                if (p > 0f) {
                    drawRect(
                        Brush.verticalGradient(
                            0f to glow.copy(alpha = glow.alpha * p),
                            0.6f to Color.Transparent
                        )
                    )
                }
            }
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { state.dragBy(it) },
                onDragStopped = { velocity -> state.settle(velocity) }
            )
    ) {
        ExpandedPlayer(
            sheet = state,
            trackState = trackState,
            progress = progress,
            elapsed = elapsed,
            actions = actions,
            miniArtCenter = with(density) {
                Offset(
                    x = (PlayerDimens.MiniInset + 12.dp + PlayerDimens.MiniArtSize / 2).toPx(),
                    y = PlayerDimens.MiniHeight.toPx() / 2f
                )
            },
            miniArtSizePx = with(density) { PlayerDimens.MiniArtSize.toPx() },
            miniArtCornerPx = with(density) { PlayerDimens.MiniArtCorner.toPx() },
        )

        // Only exists while mostly collapsed; on top, so it owns every tap on the visible card.
        if (!state.isExpanded) {
            MiniPlayer(
                trackState = trackState,
                progress = progress,
                onPlayPause = actions.onPlayPause,
                onNext = actions.onNext,
                onClick = state::expand,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(PlayerDimens.MiniHeight)
                    .graphicsLayer { alpha = (1f - state.progress * 4f).coerceIn(0f, 1f) }
            )
        }
    }
}

/** The visible slice of the panel: a rounded card of [visibleHeight] at its top. */
private class PanelShape(
    private val visibleHeight: Float,
    private val inset: Float,
    private val corner: Float,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline =
        Outline.Rounded(
            RoundRect(
                left = inset,
                top = 0f,
                right = size.width - inset,
                bottom = visibleHeight,
                cornerRadius = CornerRadius(corner)
            )
        )
}

@Preview(name = "Collapsed", showSystemUi = true)
@Composable
private fun PlayerSheetCollapsedPreview() {
    PreviewScreen {
        PlayerSheet(
            state = rememberPlayerSheetState(),
            trackState = PreviewData.trackState,
            progress = { 0.3f },
            elapsed = { "2:17" },
            actions = PreviewData.playerActions,
            collapsedBottomOffset = 24.dp
        )
    }
}

@PreviewLightDark
@Composable
private fun PlayerSheetExpandedPreview() {
    PreviewScreen {
        PlayerSheet(
            state = rememberPlayerSheetState(initialProgress = 1f),
            trackState = PreviewData.trackState,
            progress = { 0.3f },
            elapsed = { "2:17" },
            actions = PreviewData.playerActions,
            collapsedBottomOffset = 24.dp
        )
    }
}
