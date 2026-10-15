package com.example.musicplayer.presentation.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.presentation.ui.components.EmptyState
import com.example.musicplayer.presentation.ui.components.GradientCover
import com.example.musicplayer.presentation.ui.components.LocalContentBottomPadding
import com.example.musicplayer.presentation.ui.components.ScreenHeader
import com.example.musicplayer.presentation.ui.components.ThemeButton
import com.example.musicplayer.presentation.ui.components.TrackRowPlaceholder
import com.example.musicplayer.presentation.ui.components.bounceClick
import com.example.musicplayer.presentation.ui.components.staggeredEnter
import com.example.musicplayer.presentation.ui.preview.PreviewData
import com.example.musicplayer.presentation.ui.preview.PreviewScreen

private val RowShape = RoundedCornerShape(24.dp)
private val CoverShape = RoundedCornerShape(18.dp)

/** @param playlists null while loading. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistsScreen(
    playlists: List<Playlist>?,
    onOpenThemes: () -> Unit,
    onClick: (Playlist) -> Unit,
    onRemove: (Playlist) -> Unit,
) {
    var pendingRemoval by remember { mutableStateOf<Playlist?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(bottom = LocalContentBottomPadding.current),
        modifier = Modifier.fillMaxSize()
    ) {
        item(key = "header") {
            ScreenHeader(
                title = "Playlists",
                eyebrow = "Your mixes",
                subtitle = playlists?.let { if (it.size == 1) "1 playlist" else "${it.size} playlists" },
                actions = { ThemeButton(onOpenThemes) }
            )
        }
        when {
            playlists == null -> items(4) { TrackRowPlaceholder() }
            playlists.isEmpty() -> item(key = "empty") {
                EmptyState(
                    icon = Icons.AutoMirrored.Rounded.QueueMusic,
                    title = "No playlists yet",
                    message = "Tap + next to any song in your library to start your first mix."
                )
            }

            else -> itemsIndexed(
                playlists,
                key = { _, playlist -> playlist.id }) { index, playlist ->
                PlaylistRow(
                    playlist = playlist,
                    onClick = { onClick(playlist) },
                    onRemove = { pendingRemoval = playlist },
                    modifier = Modifier
                        .animateItemPlacement()
                        .staggeredEnter(index)
                )
            }
        }
    }

    pendingRemoval?.let { playlist ->
        AlertDialog(
            onDismissRequest = { pendingRemoval = null },
            icon = { Icon(Icons.Rounded.DeleteOutline, contentDescription = null) },
            title = { Text("Delete “${playlist.name}”?") },
            text = { Text("The playlist goes away; your music files stay right where they are.") },
            confirmButton = {
                TextButton(onClick = {
                    onRemove(playlist)
                    pendingRemoval = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { pendingRemoval = null }) { Text("Keep") }
            },
            shape = RoundedCornerShape(28.dp)
        )
    }
}

@Composable
private fun PlaylistRow(
    playlist: Playlist,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .bounceClick(pressedScale = 0.97f, shape = RowShape, onClick = onClick)
            .background(scheme.surfaceContainer.copy(alpha = 0.7f))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GradientCover(
            seed = playlist.name,
            label = playlist.name,
            labelSize = 26.sp,
            shape = CoverShape,
            modifier = Modifier.size(60.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.titleMedium,
                color = scheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Playlist",
                style = MaterialTheme.typography.bodySmall,
                color = scheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Rounded.DeleteOutline,
            contentDescription = "Delete playlist",
            tint = scheme.onSurfaceVariant,
            modifier = Modifier
                .size(44.dp)
                .bounceClick(pressedScale = 0.8f, shape = CircleShape, onClick = onRemove)
                .padding(10.dp)
        )
    }
}

@PreviewLightDark
@Composable
private fun PlaylistsScreenPreview() {
    PreviewScreen {
        PlaylistsScreen(
            playlists = PreviewData.playlists,
            onOpenThemes = {},
            onClick = {},
            onRemove = {})
    }
}

@Preview(name = "Empty")
@Composable
private fun PlaylistsScreenEmptyPreview() {
    PreviewScreen {
        PlaylistsScreen(playlists = emptyList(), onOpenThemes = {}, onClick = {}, onRemove = {})
    }
}
