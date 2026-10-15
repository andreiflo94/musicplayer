package com.example.musicplayer.presentation.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.ui.components.AlbumArt
import com.example.musicplayer.presentation.ui.components.CollapsingTopBar
import com.example.musicplayer.presentation.ui.components.EmptyState
import com.example.musicplayer.presentation.ui.components.GradientPillButton
import com.example.musicplayer.presentation.ui.components.LocalContentBottomPadding
import com.example.musicplayer.presentation.ui.components.NowPlaying
import com.example.musicplayer.presentation.ui.components.TonalPillButton
import com.example.musicplayer.presentation.ui.components.TrackRow
import com.example.musicplayer.presentation.ui.components.TrackRowPlaceholder
import com.example.musicplayer.presentation.ui.components.staggeredEnter
import com.example.musicplayer.presentation.ui.theme.Motion

private val CoverShape = RoundedCornerShape(32.dp)

/**
 * A list of tracks under a big cover hero: used for both folders and playlists.
 * The hero shrinks and fades as it scrolls away, and a compact top bar takes
 * over once it's gone. All scroll-driven effects are read in the draw phase.
 *
 * @param tracks null while loading.
 */
@Composable
fun TrackCollectionScreen(
    title: String,
    coverModel: Any?,
    tracks: List<Track>?,
    nowPlaying: NowPlaying,
    onBack: () -> Unit,
    onPlay: (index: Int) -> Unit,
    onPlayAll: (shuffle: Boolean) -> Unit,
    emptyIcon: ImageVector,
    emptyTitle: String,
    emptyMessage: String,
    trailing: @Composable (Track) -> Unit = {},
) {
    val listState = rememberLazyListState()
    val revealDistance = with(LocalDensity.current) { 220.dp.toPx() }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(bottom = LocalContentBottomPadding.current),
            modifier = Modifier.fillMaxSize()
        ) {
            item(key = "hero") {
                CollectionHero(
                    title = title,
                    coverModel = coverModel,
                    trackCount = tracks?.size,
                    onPlayAll = onPlayAll,
                    modifier = Modifier.parallax(listState)
                )
            }
            when {
                tracks == null -> items(6) { TrackRowPlaceholder() }
                tracks.isEmpty() -> item(key = "empty") {
                    EmptyState(
                        emptyIcon,
                        emptyTitle,
                        emptyMessage
                    )
                }

                else -> itemsIndexed(
                    tracks,
                    key = { index, track -> "$index:${track.path}" }) { index, track ->
                    TrackRow(
                        title = track.trackName.ifBlank { track.name },
                        subtitle = track.artistName,
                        artModel = track.albumIconUrl,
                        seed = track.path,
                        isCurrent = nowPlaying.isCurrent(track.path),
                        isPlaying = nowPlaying.isPlaying,
                        onClick = { onPlay(index) },
                        trailing = { trailing(track) },
                        modifier = Modifier.staggeredEnter(index)
                    )
                }
            }
        }

        CollapsingTopBar(
            title = title,
            revealed = {
                if (listState.firstVisibleItemIndex > 0) 1f
                else ((listState.firstVisibleItemScrollOffset - revealDistance) / (revealDistance * 0.3f)).coerceIn(
                    0f,
                    1f
                )
            },
            onBack = onBack
        )
    }
}

// No translation: the rows start right under the Play/Shuffle buttons, so any downward drift
// would leave the hero showing through them as they scroll up.
private fun Modifier.parallax(listState: LazyListState): Modifier = graphicsLayer {
    if (listState.firstVisibleItemIndex == 0) {
        val offset = listState.firstVisibleItemScrollOffset.toFloat()
        val fraction = (offset / size.height).coerceIn(0f, 1f)
        alpha = 1f - fraction * 1.6f
        val scale = 1f - fraction * 0.15f
        scaleX = scale
        scaleY = scale
    } else {
        alpha = 0f
    }
}

@Composable
private fun CollectionHero(
    title: String,
    coverModel: Any?,
    trackCount: Int?,
    onPlayAll: (shuffle: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 72.dp, bottom = 12.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(220.dp)
                .drawBehind {
                    // Colored glow that makes the cover float above the aurora.
                    drawCircle(
                        Brush.radialGradient(
                            listOf(scheme.primary.copy(alpha = 0.35f), Color.Transparent),
                            radius = size.maxDimension * 0.75f
                        ),
                        radius = size.maxDimension * 0.75f
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            AlbumArt(
                model = coverModel,
                seed = title,
                shape = CoverShape,
                modifier = Modifier
                    .size(200.dp)
                    .shadow(
                        24.dp,
                        CoverShape,
                        ambientColor = scheme.primary,
                        spotColor = scheme.primary
                    )
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = scheme.onBackground,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = when (trackCount) {
                null -> "Loading…"
                1 -> "1 track"
                else -> "$trackCount tracks"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = scheme.onSurfaceVariant
        )
        Spacer(Modifier.height(18.dp))
        AnimatedVisibility(
            visible = (trackCount ?: 0) > 0,
            enter = fadeIn() + scaleIn(Motion.bouncy(), initialScale = 0.8f)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GradientPillButton("Play", Icons.Rounded.PlayArrow, onClick = { onPlayAll(false) })
                TonalPillButton("Shuffle", Icons.Rounded.Shuffle, onClick = { onPlayAll(true) })
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}
