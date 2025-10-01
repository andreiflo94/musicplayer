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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.musicplayer.domain.model.Track

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PlaylistTracksScreen(
    title: String,
    tracks: List<Track>,
    onClick: (Track) -> Unit
) {
    PlaylistTracksScreenContent(
        title = title,
        tracks = tracks,
        onClick = onClick
    )
}

@Composable
private fun PlaylistTracksScreenContent(
    title: String,
    tracks: List<Track>,
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

        PlaylistTracksList(tracks, onClick)
    }
}

@Composable
private fun PlaylistTracksList(
    tracks: List<Track>,
    onClick: (Track) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 150.dp)
    ) {
        items(tracks, key = { it.name }) { track ->
            PlaylistTrackItem(track, onClick)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun PlaylistTrackItem(
    track: Track,
    onClick: (Track) -> Unit
) {
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
                GlideImage(
                    model = track.albumIconUrl,
                    contentDescription = "album image",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.secondary),
                )

                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = track.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
