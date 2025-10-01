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
import androidx.compose.ui.draw.shadow
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
    PlaylistsScreenContent(title, playlists, onClick, onRemove)
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
        PlaylistsList(playlists, onClick, onRemove)
    }
}

@Composable
private fun ScreenTitle(title: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .wrapContentHeight(),
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun PlaylistsList(
    playlists: List<Playlist>,
    onClick: (Playlist) -> Unit,
    onRemove: (Playlist) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            playlists,
            key = { it.id } // ensures only changed items recomposes
        ) { playlist ->
            PlaylistItem(playlist, onClick, onRemove)
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
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .fillMaxWidth()
            .shadow(5.dp, MaterialTheme.shapes.small)
            .clickable { onClick(playlist) },
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.onBackground
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlaylistName(playlist.name)
            RemoveButton { onRemove(playlist) }
        }
    }
}

@Composable
private fun PlaylistName(name: String) {
    Text(
        text = name,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun RemoveButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Remove Playlist",
            tint = MaterialTheme.colorScheme.background
        )
    }
}
