package com.example.musicplayer.presentation.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.FolderOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.ui.components.NowPlaying
import com.example.musicplayer.presentation.ui.components.bounceClick
import com.example.musicplayer.presentation.ui.preview.PreviewData
import com.example.musicplayer.presentation.ui.preview.PreviewScreen

@Composable
fun TracksScreen(
    title: String,
    tracks: List<Track>?,
    playlists: List<Playlist>,
    nowPlaying: NowPlaying,
    onBack: () -> Unit,
    addToNewPlaylist: (String, Track) -> Unit,
    addToPlaylist: (Long, Track) -> Unit,
    onPlay: (index: Int) -> Unit,
    onPlayAll: (shuffle: Boolean) -> Unit,
) {
    var selectedTrack by remember { mutableStateOf<Track?>(null) }

    TrackCollectionScreen(
        title = title,
        coverModel = tracks?.firstOrNull()?.albumIconUrl,
        tracks = tracks,
        nowPlaying = nowPlaying,
        onBack = onBack,
        onPlay = onPlay,
        onPlayAll = onPlayAll,
        emptyIcon = Icons.Rounded.FolderOff,
        emptyTitle = "Nothing here",
        emptyMessage = "This folder has no playable tracks.",
        trailing = { track ->
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.PlaylistAdd,
                contentDescription = "Add to playlist",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(44.dp)
                    .bounceClick(pressedScale = 0.8f, shape = CircleShape) { selectedTrack = track }
                    .padding(10.dp)
            )
        }
    )

    selectedTrack?.let { track ->
        PlaylistBottomSheetScreen(
            track = track,
            playlists = playlists,
            addToNewPlaylist = addToNewPlaylist,
            addToPlaylist = addToPlaylist,
            onDismiss = { selectedTrack = null }
        )
    }
}

@PreviewLightDark
@Composable
private fun TracksScreenPreview() {
    PreviewScreen {
        TracksScreen(
            title = "72 Seasons",
            tracks = PreviewData.tracks,
            playlists = PreviewData.playlists,
            nowPlaying = NowPlaying(path = PreviewData.tracks[0].path, isPlaying = true),
            onBack = {},
            addToNewPlaylist = { _, _ -> },
            addToPlaylist = { _, _ -> },
            onPlay = {},
            onPlayAll = {}
        )
    }
}
