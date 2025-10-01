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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
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
        TrackList(tracks, showAddToPlaylist = { selectedTrack = it }, onClick)
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 16.dp),
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground
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
        contentPadding = PaddingValues(bottom = 150.dp)
    ) {
        items(tracks, key = { it.trackName }) {
            TrackItem(it, showAddToPlaylist, onClick)
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
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .fillMaxWidth()
            .shadow(5.dp, MaterialTheme.shapes.small)
            .clickable { onClick(track) },
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.onBackground
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AlbumImage(track.albumIconUrl)
            Spacer(modifier = Modifier.width(8.dp))
            TrackInfo(track)
            Spacer(modifier = Modifier.weight(1f))
            AddToPlaylistButton { showAddToPlaylist(track) }
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
            .size(40.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.secondary)
    )
}

@Composable
private fun TrackInfo(track: Track) {
    Column{
        Text(
            text = track.artistName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = track.trackName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun AddToPlaylistButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_playlist_add),
            contentDescription = "AddToPlaylist",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
