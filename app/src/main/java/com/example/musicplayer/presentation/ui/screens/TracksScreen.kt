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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.ui.components.PlaylistBottomSheet

@Composable
fun TracksScreen(
    title: String,
    tracks: List<Track>,           // Pass List instead of State
    playlists: List<Playlist>,      // Pass List instead of State
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
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 16.dp),
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        TrackList(
            tracks = tracks,
            showAddToPlaylist = { track -> selectedTrack = track },
            onClick = onClick
        )
    }

    selectedTrack?.let { track ->
        PlaylistBottomSheet(
            track = track,
            playlists = playlists,
            addToNewPlaylist = addToNewPlaylist,
            addToPlaylist = addToPlaylist,
            onDismiss = { selectedTrack = null }
        )
    }
}

@Composable
fun TrackList(
    tracks: List<Track>,
    showAddToPlaylist: (Track) -> Unit,
    onClick: (Track) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 150.dp)
    ) {
        items(tracks, key = { it.trackName }) { track -> // stable key
            TrackItem(track, showAddToPlaylist, onClick)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TrackItem(
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
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlideImage(
                model = track.albumIconUrl,
                contentDescription = "album image",
                modifier = Modifier
                    .size(40.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.secondary)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
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

            IconButton(onClick = { showAddToPlaylist(track) }) {
                Icon(
                    painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_playlist_add),
                    contentDescription = "AddToPlaylist",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ---- Sample Data / Previews ----
@Composable
fun rememberSampleTracks() = remember {
    listOf(
        Track("Artist 1", "url1"),
        Track("Artist 2", "url2"),
        Track("Artist 3", "url3")
    )
}

@Composable
fun rememberSamplePlaylists() = remember {
    listOf(
        Playlist(1, "Playlist 1"),
        Playlist(2, "Playlist 2"),
        Playlist(3, "Playlist 3")
    )
}

@Preview(showBackground = true)
@Composable
fun TracksScreenPreview() {
    val tracks = rememberSampleTracks()
    val playlists = rememberSamplePlaylists()

    TracksScreen(
        title = "Favorite Tracks",
        tracks = tracks,
        playlists = playlists,
        addToNewPlaylist = { name, track -> println("Add '${track.trackName}' to new playlist '$name'") },
        addToPlaylist = { id, track -> println("Add '${track.trackName}' to playlist $id") },
        onClick = { track -> println("Clicked track: ${track.trackName}") }
    )
}
