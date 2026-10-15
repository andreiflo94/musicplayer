package com.example.musicplayer.presentation.ui.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.musicplayer.presentation.ui.components.PlayPauseIcon
import com.example.musicplayer.presentation.ui.components.bounceClick
import com.example.musicplayer.presentation.ui.preview.PreviewData
import com.example.musicplayer.presentation.ui.preview.PreviewSurface
import com.example.musicplayer.presentation.ui.theme.Motion
import com.example.musicplayer.presentation.viewmodels.TrackState

/**
 * Collapsed player card. The artwork isn't drawn here: the expanded player's artwork
 * shrinks into the empty slot on the left, so it is one continuous object.
 */
@Composable
fun MiniPlayer(
    trackState: TrackState,
    progress: () -> Float,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = PlayerDimens.MiniInset + 12.dp,
                    end = PlayerDimens.MiniInset + 6.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(PlayerDimens.MiniArtSize + 12.dp))

            AnimatedContent(
                targetState = trackState,
                contentKey = { it.trackPath },
                transitionSpec = {
                    (slideInVertically(Motion.fluid()) { it } + fadeIn()) togetherWith
                            (slideOutVertically(Motion.fluid()) { -it } + fadeOut())
                },
                label = "miniTitle",
                modifier = Modifier.weight(1f)
            ) { track ->
                Column {
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = scheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (track.artist.isNotBlank()) {
                        Text(
                            text = track.artist,
                            style = MaterialTheme.typography.bodySmall,
                            color = scheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            PlayPauseIcon(
                isPlaying = trackState.isPlaying,
                color = scheme.onSurface,
                modifier = Modifier
                    .size(44.dp)
                    .bounceClick(pressedScale = 0.82f, shape = CircleShape, onClick = onPlayPause)
                    .padding(11.dp)
            )

            AnimatedVisibility(
                visible = trackState.hasNextMediaItem,
                enter = fadeIn() + scaleIn(animationSpec = Motion.bouncy()),
                exit = fadeOut() + scaleOut()
            ) {
                Icon(
                    imageVector = Icons.Rounded.SkipNext,
                    contentDescription = "Next",
                    tint = scheme.onSurface,
                    modifier = Modifier
                        .size(44.dp)
                        .bounceClick(pressedScale = 0.82f, shape = CircleShape, onClick = onNext)
                        .padding(10.dp)
                )
            }
        }

        // Hairline progress along the bottom edge of the card.
        val track = scheme.onSurface.copy(alpha = 0.08f)
        val fill = scheme.primary
        Canvas(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(
                    start = PlayerDimens.MiniInset + 20.dp,
                    end = PlayerDimens.MiniInset + 20.dp,
                    bottom = 5.dp
                )
                .height(3.dp)
        ) {
            val y = size.height / 2f
            drawLine(track, Offset(0f, y), Offset(size.width, y), size.height, StrokeCap.Round)
            drawLine(
                fill,
                Offset(0f, y),
                Offset(size.width * progress().coerceIn(0f, 1f), y),
                size.height,
                StrokeCap.Round
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun MiniPlayerPreview() {
    PreviewSurface {
        MiniPlayer(
            trackState = PreviewData.trackState,
            progress = { 0.3f },
            onPlayPause = {},
            onNext = {},
            onClick = {},
            modifier = Modifier
                .padding(12.dp)
                .width(360.dp)
                .height(PlayerDimens.MiniHeight)
        )
    }
}
