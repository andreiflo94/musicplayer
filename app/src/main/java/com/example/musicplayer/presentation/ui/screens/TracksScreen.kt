package com.example.musicplayer.presentation.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.ui.components.PlaylistBottomSheet
import com.example.musicplayer.presentation.viewmodels.PlaylistBottomSheetVm


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TracksScreen(
    title: String,
    tracks: State<List<Track>>,
    playlists: State<List<Playlist>>,
    addToNewPlaylist: (String, Track) -> Unit,
    addToPlaylist: (Long, Track) -> Unit,
    onClick: (Track) -> Unit
) {
    var selectedTrack by rememberSaveable { mutableStateOf<Track?>(null) } //track to be added to playlist

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 16.dp, 10.dp, 10.dp)
                .wrapContentHeight(),
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        TrackList(
            tracks.value,
            showAddToPlaylist = { track ->
                selectedTrack = track
            },
            onClick
        )
    }

    selectedTrack?.let { track ->
        PlaylistBottomSheet(
            track = track,
            playlists = playlists.value,
            addToNewPlaylist = { playlistName, track ->
                addToNewPlaylist(playlistName, track)
            },
            addToPlaylist = { playlistId, track ->
                addToPlaylist(playlistId, track)
            },
            onDismiss = { selectedTrack = null }
        )
    }
}

@Composable
fun TrackList(
    musicFolders: List<Track>,
    showAddToPlaylist: (Track) -> Unit,
    onClick: (Track) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            bottom = 150.dp,
        ),
    ) {
        items(musicFolders) { folder ->
            TrackItem(folder, showAddToPlaylist, onClick)
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
            .padding(10.dp, 8.dp, 10.dp, 10.dp)
            .fillMaxWidth()
            .shadow(5.dp, MaterialTheme.shapes.small)
            .clickable { onClick(track) },
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.background // mai potrivit decât onBackground
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlideImage(
                    model = track.albumIconUrl,
                    contentDescription = "album image",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.secondary),
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = track.artistName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp)) // spațiu mic între texte
                    Text(
                        text = track.trackName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = { showAddToPlaylist(track) }
                ) {
                    Icon(
                        painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_playlist_add),
                        contentDescription = "AddToPlaylist",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TracksScreenPreview() {
    val sampleTracks = remember { mutableStateOf(getSampleTracks()) }
    val samplePlaylists = remember { mutableStateOf(getSamplePlaylists()) }

    MaterialTheme {
        TracksScreen(
            title = "Favorite Tracks",
            tracks = sampleTracks,
            playlists = samplePlaylists,
            addToNewPlaylist = { name, track ->
                // Fake impl
                println("Add '${track.trackName}' to new playlist '$name'")
            },
            addToPlaylist = { id, track ->
                // Fake impl
                println("Add '${track.trackName}' to playlist with id $id")
            },
            onClick = { track ->
                println("Clicked track: ${track.trackName}")
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun TrackItemPreview() {
    val folder = getSampleTrack()
    TrackItem(folder, showAddToPlaylist = {}, onClick = {})
}

private fun getSamplePlaylists(): List<Playlist> {
    return listOf(
        Playlist(1, "url1"),
        Playlist(2, "url2"),
        Playlist(3, "url3")
    )
}

private fun getSampleTracks(): List<Track> {
    return listOf(
        Track("", "url1"),
        Track("", "url2"),
        Track("", "url3")
    )
}

private fun getSampleTrack(): Track {
    return Track("", "sampleUrl")
}