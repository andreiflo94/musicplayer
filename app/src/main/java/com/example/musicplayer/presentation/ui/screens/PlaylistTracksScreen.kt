package com.example.musicplayer.presentation.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.ui.components.NowPlaying
import com.example.musicplayer.presentation.ui.components.bounceClick
import com.example.musicplayer.presentation.ui.preview.PreviewData
import com.example.musicplayer.presentation.ui.preview.PreviewScreen

@Composable
fun PlaylistTracksScreen(
    title: String,
    tracks: List<Track>?,
    nowPlaying: NowPlaying,
    onBack: () -> Unit,
    onPlay: (index: Int) -> Unit,
    onPlayAll: (shuffle: Boolean) -> Unit,
    onRemove: (Track) -> Unit,
) {
    TrackCollectionScreen(
        title = title,
        coverModel = tracks?.firstNotNullOfOrNull { it.albumIconUrl },
        tracks = tracks,
        nowPlaying = nowPlaying,
        onBack = onBack,
        onPlay = onPlay,
        onPlayAll = onPlayAll,
        emptyIcon = Icons.Rounded.LibraryMusic,
        emptyTitle = "An empty stage",
        emptyMessage = "Add songs from your library with the + button next to any track.",
        trailing = { track ->
            Icon(
                imageVector = Icons.Rounded.RemoveCircleOutline,
                contentDescription = "Remove from playlist",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(44.dp)
                    .bounceClick(pressedScale = 0.8f, shape = CircleShape) { onRemove(track) }
                    .padding(10.dp)
            )
        }
    )
}

@PreviewLightDark
@Composable
private fun PlaylistTracksScreenPreview() {
    PreviewScreen {
        PlaylistTracksScreen(
            title = "Workout",
            tracks = PreviewData.tracks.take(4),
            nowPlaying = NowPlaying(path = PreviewData.tracks[2].path, isPlaying = false),
            onBack = {},
            onPlay = {},
            onPlayAll = {},
            onRemove = {}
        )
    }
}

@Preview(name = "Empty")
@Composable
private fun PlaylistTracksScreenEmptyPreview() {
    PreviewScreen {
        PlaylistTracksScreen(
            title = "Workout",
            tracks = emptyList(),
            nowPlaying = NowPlaying(),
            onBack = {},
            onPlay = {},
            onPlayAll = {},
            onRemove = {}
        )
    }
}

@Preview(name = "Loading")
@Composable
private fun PlaylistTracksScreenLoadingPreview() {
    PreviewScreen {
        PlaylistTracksScreen(
            title = "Workout",
            tracks = null,
            nowPlaying = NowPlaying(),
            onBack = {},
            onPlay = {},
            onPlayAll = {},
            onRemove = {}
        )
    }
}
