package com.example.musicplayer.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Track

@Composable
fun TracksScreen(
    title: String,
    tracks: List<Track>,
    playlists: List<Playlist>,
    addToNewPlaylist: (String, Track) -> Unit,
    addToPlaylist: (Long, Track) -> Unit,
    onClick: (Track) -> Unit
) {

    var selectedTrack by remember { mutableStateOf<Track?>(null) }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {

        ScreenTitle(title)

        TrackList(
            tracks = tracks,
            showAddToPlaylist = { selectedTrack = it },
            onClick = onClick
        )
    }

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

@Composable
private fun ScreenTitle(title: String) {

    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    )
}

@Composable
private fun TrackList(
    tracks: List<Track>,
    showAddToPlaylist: (Track) -> Unit,
    onClick: (Track) -> Unit
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 8.dp,
            bottom = 100.dp
        )
    ) {

        items(
            items = tracks,
            key = { it.trackName }
        ) { track ->

            TrackItem(
                track = track,
                showAddToPlaylist = showAddToPlaylist,
                onClick = onClick
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun TrackItem(
    track: Track,
    showAddToPlaylist: (Track) -> Unit,
    onClick: (Track) -> Unit
) {

    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onClick(track) },

        shape = MaterialTheme.shapes.large,
        tonalElevation = 3.dp,
        color = MaterialTheme.colorScheme.surface
    ) {

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .fillMaxWidth(),

            verticalAlignment = Alignment.CenterVertically
        ) {

            AlbumImage(track.albumIconUrl)

            Spacer(modifier = Modifier.width(16.dp))

            TrackInfo(
                track = track,
                modifier = Modifier.weight(1f)
            )

            AddToPlaylistButton {
                showAddToPlaylist(track)
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun AlbumImage(albumIconUrl: String?) {

    GlideImage(
        model = albumIconUrl,
        contentDescription = "album image",
        modifier = Modifier
            .size(56.dp)
            .clip(MaterialTheme.shapes.medium)
    )
}

@Composable
private fun TrackInfo(
    track: Track,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier) {

        Text(
            text = track.trackName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = track.artistName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AddToPlaylistButton(onClick: () -> Unit) {

    IconButton(onClick = onClick) {

        Icon(
            painter = painterResource(
                id = androidx.media3.session.R.drawable.media3_icon_playlist_add
            ),
            contentDescription = "Add to playlist",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}