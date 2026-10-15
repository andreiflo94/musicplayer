package com.example.musicplayer.presentation.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.ui.components.AlbumArt
import com.example.musicplayer.presentation.ui.components.GradientCover
import com.example.musicplayer.presentation.ui.components.bounceClick
import com.example.musicplayer.presentation.ui.components.staggeredEnter
import com.example.musicplayer.presentation.ui.preview.PreviewData
import com.example.musicplayer.presentation.ui.preview.PreviewSurface
import com.example.musicplayer.presentation.ui.theme.Motion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistBottomSheetScreen(
    track: Track,
    playlists: List<Playlist>,
    addToNewPlaylist: (playListName: String, track: Track) -> Unit,
    addToPlaylist: (playlistId: Long, track: Track) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    var newPlaylistName by remember { mutableStateOf("") }

    val createPlaylist = {
        val name = newPlaylistName.trim()
        if (name.isNotEmpty()) {
            addToNewPlaylist(name, track)
            Toast.makeText(context, "Playlist '$name' created!", Toast.LENGTH_SHORT).show()
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        PlaylistPickerContent(
            track = track,
            playlists = playlists,
            newPlaylistName = newPlaylistName,
            onNameChange = { newPlaylistName = it },
            onCreate = createPlaylist,
            onPick = { playlist ->
                addToPlaylist(playlist.id, track)
                Toast.makeText(
                    context,
                    "Track added to '${playlist.name}'",
                    Toast.LENGTH_SHORT
                ).show()
                onDismiss()
            }
        )
    }
}

@Composable
private fun PlaylistPickerContent(
    track: Track,
    playlists: List<Playlist>,
    newPlaylistName: String,
    onNameChange: (String) -> Unit,
    onCreate: () -> Unit,
    onPick: (Playlist) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
    ) {
        TrackSummary(track)
        Spacer(Modifier.height(20.dp))
        NewPlaylistField(
            name = newPlaylistName,
            onNameChange = onNameChange,
            onCreate = onCreate
        )
        if (playlists.isNotEmpty()) {
            Spacer(Modifier.height(20.dp))
            Text(
                text = "OR ADD TO",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            LazyColumn(Modifier.heightIn(max = 320.dp)) {
                itemsIndexed(
                    playlists,
                    key = { _, playlist -> playlist.id }) { index, playlist ->
                    PlaylistPickRow(
                        playlist = playlist,
                        modifier = Modifier.staggeredEnter(index),
                        onClick = { onPick(playlist) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TrackSummary(track: Track) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AlbumArt(
            model = track.albumIconUrl,
            seed = track.path,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.size(56.dp)
        )
        Spacer(Modifier.width(14.dp))
        Column {
            Text(
                text = "ADD TO PLAYLIST",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = track.trackName.ifBlank { track.name },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun NewPlaylistField(name: String, onNameChange: (String) -> Unit, onCreate: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        placeholder = { Text("New playlist name") },
        leadingIcon = { Icon(Icons.Rounded.Add, contentDescription = null) },
        trailingIcon = {
            // The create button pops in as soon as there's a name to create.
            AnimatedVisibility(
                visible = name.isNotBlank(),
                enter = fadeIn() + scaleIn(Motion.bouncy()),
                exit = fadeOut() + scaleOut()
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowUpward,
                    contentDescription = "Create playlist",
                    tint = scheme.onPrimary,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(40.dp)
                        .bounceClick(pressedScale = 0.85f, shape = CircleShape, onClick = onCreate)
                        .background(Brush.linearGradient(listOf(scheme.primary, scheme.tertiary)))
                        .padding(9.dp)
                )
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onCreate() }),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun PlaylistPickRow(
    playlist: Playlist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .bounceClick(pressedScale = 0.97f, shape = RoundedCornerShape(18.dp), onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GradientCover(
            seed = playlist.name,
            label = playlist.name,
            labelSize = 20.sp,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.size(44.dp)
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = playlist.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@PreviewLightDark
@Composable
private fun PlaylistPickerContentPreview() {
    PreviewSurface {
        PlaylistPickerContent(
            track = PreviewData.tracks[1],
            playlists = PreviewData.playlists,
            newPlaylistName = "",
            onNameChange = {},
            onCreate = {},
            onPick = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun PlaylistPickerContentTypingPreview() {
    PreviewSurface {
        PlaylistPickerContent(
            track = PreviewData.tracks[1],
            playlists = emptyList(),
            newPlaylistName = "Gym",
            onNameChange = {},
            onCreate = {},
            onPick = {}
        )
    }
}
