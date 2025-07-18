package com.example.musicplayer.presentation.ui.screens


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.musicplayer.domain.model.Playlist


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PlaylistsScreen(
    title: String, playlists: State<List<Playlist>>,
    onClick: (Playlist) -> Unit,
    onRemove: (Playlist) -> Unit
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 10.dp, 10.dp, 10.dp)
                .wrapContentHeight(),
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Playlists(playlists.value, onClick, onRemove)
    }
}

@Composable
fun Playlists(playlists: List<Playlist>, onClick: (Playlist) -> Unit,
              onRemove: (Playlist) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        items(playlists) { playlist ->
            PlaylistItem(playlist, onClick, onRemove)
        }
    }
}

@Composable
fun PlaylistItem(
    playlist: Playlist,
    onClick: (Playlist) -> Unit,
    onRemove: (Playlist) -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(10.dp, 8.dp, 10.dp, 10.dp)
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
            // Display the album name
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Remove button
            IconButton(onClick = { onRemove(playlist) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove Playlist",
                    tint = MaterialTheme.colorScheme.background
                )
            }
        }
    }
}
