package com.example.musicplayer.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.musicplayer.presentation.ui.preview.PreviewData
import com.example.musicplayer.presentation.ui.preview.PreviewSurface
import com.example.musicplayer.presentation.ui.theme.Motion

private val RowShape = RoundedCornerShape(20.dp)
private val ArtShape = RoundedCornerShape(14.dp)

/**
 * One line in any track list. The current track gets a tinted background, a colored title
 * and live equalizer bars over its artwork; all of it animates in and out.
 */
@Composable
fun TrackRow(
    title: String,
    subtitle: String,
    artModel: Any?,
    seed: String,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: @Composable () -> Unit = {},
) {
    val scheme = MaterialTheme.colorScheme
    val background by animateColorAsState(
        targetValue = if (isCurrent) scheme.primaryContainer.copy(alpha = 0.7f) else Color.Transparent,
        animationSpec = Motion.fluid(),
        label = "rowBackground"
    )
    val titleColor by animateColorAsState(
        targetValue = if (isCurrent) scheme.primary else scheme.onSurface,
        animationSpec = Motion.fluid(),
        label = "rowTitle"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .bounceClick(pressedScale = 0.97f, shape = RowShape, onClick = onClick)
            .background(background)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(52.dp)
                .clip(ArtShape)
        ) {
            AlbumArt(
                model = artModel,
                seed = seed,
                modifier = Modifier.fillMaxSize(),
                shape = ArtShape
            )
            androidx.compose.animation.AnimatedVisibility(
                visible = isCurrent,
                enter = fadeIn() + scaleIn(initialScale = 0.6f, animationSpec = Motion.bouncy()),
                exit = fadeOut() + scaleOut(targetScale = 0.6f),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    EqualizerBars(
                        isPlaying = isPlaying,
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(Modifier.width(14.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        trailing()
    }
}

/** Skeleton version of [TrackRow] shown while a list loads. */
@Composable
fun TrackRowPlaceholder(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(52.dp)
                .clip(ArtShape)
                .shimmer()
        )
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Box(
                Modifier
                    .fillMaxWidth(0.7f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .shimmer()
            )
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .fillMaxWidth(0.4f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .shimmer()
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun TrackRowPreview() {
    PreviewSurface {
        Column {
            PreviewData.tracks.take(3).forEachIndexed { i, track ->
                TrackRow(
                    title = track.trackName,
                    subtitle = track.artistName,
                    artModel = null,
                    seed = track.path,
                    isCurrent = i == 0,
                    isPlaying = true,
                    onClick = {}
                )
            }
            TrackRowPlaceholder()
        }
    }
}
