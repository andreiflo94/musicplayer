package com.example.musicplayer.presentation.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.musicplayer.domain.model.Playlist

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PlaylistsScreen(
    title: String,
    playlists: List<Playlist>,
    onClick: (Playlist) -> Unit,
    onRemove: (Playlist) -> Unit
) {
    PlaylistsScreenContent(
        title = title,
        playlists = playlists,
        onClick = onClick,
        onRemove = onRemove
    )
}

@Composable
private fun PlaylistsScreenContent(
    title: String,
    playlists: List<Playlist>,
    onClick: (Playlist) -> Unit,
    onRemove: (Playlist) -> Unit
) {

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {

        ScreenTitle(title)

        PlaylistsList(
            playlists = playlists,
            onClick = onClick,
            onRemove = onRemove
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
private fun PlaylistsList(
    playlists: List<Playlist>,
    onClick: (Playlist) -> Unit,
    onRemove: (Playlist) -> Unit
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            bottom = 100.dp
        )
    ) {

        items(
            items = playlists,
            key = { it.id }
        ) { playlist ->

            PlaylistItem(
                playlist = playlist,
                onClick = onClick,
                onRemove = onRemove
            )
        }
    }
}

@Composable
private fun PlaylistItem(
    playlist: Playlist,
    onClick: (Playlist) -> Unit,
    onRemove: (Playlist) -> Unit
) {

    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onClick(playlist) },

        shape = MaterialTheme.shapes.large,
        tonalElevation = 3.dp,
        color = MaterialTheme.colorScheme.surface
    ) {

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 18.dp)
                .fillMaxWidth(),

            verticalAlignment = Alignment.CenterVertically
        ) {

            PlaylistName(
                name = playlist.name,
                modifier = Modifier.weight(1f)
            )

            RemoveButton {
                onRemove(playlist)
            }
        }
    }
}

@Composable
private fun PlaylistName(
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

@Composable
private fun RemoveButton(onClick: () -> Unit) {

    IconButton(
        onClick = onClick
    ) {

        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Remove Playlist",
            tint = MaterialTheme.colorScheme.error
        )
    }
}