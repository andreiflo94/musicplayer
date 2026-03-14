package com.example.musicplayer.presentation.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

        ScreenTitle(title)

        PlaylistTracksList(
            tracks = tracks,
            onClick = onClick
        )
    }
}

@Composable
private fun ScreenTitle(title: String) {

    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    )
}

@Composable
private fun PlaylistTracksList(
    tracks: List<Track>,
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
            key = { it.name }
        ) { track ->

            PlaylistTrackItem(
                track = track,
                onClick = onClick
            )
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

            TrackName(
                name = track.name,
                modifier = Modifier.weight(1f)
            )
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
private fun TrackName(
    name: String,
    modifier: Modifier = Modifier
) {

    Text(
        text = name,
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface
    )
}