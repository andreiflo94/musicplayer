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
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.ui.components.PlaylistBottomSheet
import com.example.musicplayer.presentation.viewmodels.PlaylistBottomSheetVm


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TracksScreen(
    title: String,
    tracks: State<List<Track>>,
    onClick: (Track) -> Unit
) {
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
        TrackList(tracks.value, onClick)
    }
}

@Composable
fun TrackList(
    musicFolders: List<Track>,
    onClick: (Track) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            bottom = 150.dp,
        ),
    ) {
        items(musicFolders) { folder ->
            TrackItem(folder, onClick)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TrackItem(
    track: Track, onClick: (Track) -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .padding(10.dp, 8.dp, 10.dp, 10.dp)
            .fillMaxWidth()
            .shadow(5.dp, MaterialTheme.shapes.small)
            .clickable { onClick(track) },
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.onBackground
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Display the album icon at the start
                GlideImage(
                    model = track.albumIconUrl,
                    contentDescription = "album image",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.secondary),
                )

                // Spacer to push the icon to the end
                Spacer(modifier = Modifier.weight(0.2f))

                Column(
                    modifier = Modifier.weight(1f)
                ){
                    Text(
                        text = track.artistName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = track.trackName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                IconButton(
                    onClick = { showBottomSheet = true },
                    content = {
                        Icon(
                            painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_playlist_add),
                            contentDescription = "AddToPlaylist",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    })
            }
        }
        // Show Bottom Sheet when IconButton is clicked
        if (showBottomSheet) {
            val playlistBottomSheetVm = hiltViewModel<PlaylistBottomSheetVm>()
            PlaylistBottomSheet(
                track = track,
                playlists = playlistBottomSheetVm.playListsState.collectAsStateWithLifecycle(
                    initialValue = emptyList()
                ),
                addToNewPlaylist = { playlistName, track ->
                    playlistBottomSheetVm.addToNewPlaylist(playlistName, track)
                },
                addToPlaylist = { playlistId, track ->
                    playlistBottomSheetVm.addToPlaylist(playlistId, track)
                },
                onDismiss = { showBottomSheet = false })
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TracksScreenPreview() {
    val musicFolders = remember { mutableStateOf(getSampleTracks()) }
    TracksScreen(
        title = "Samsung",
        musicFolders
    )
    {

    }
}

@Preview(showBackground = true)
@Composable
fun TrackItemPreview() {
    val folder = getSampleTrack()
    TrackItem(folder) {

    }
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