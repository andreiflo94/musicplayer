package com.example.musicplayer.presentation.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Track

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistBottomSheetScreen(
    track: Track,
    playlists: List<Playlist>,
    addToNewPlaylist: (playListName: String, track: Track) -> Unit,
    addToPlaylist: (playlistId: Long, track: Track) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val context = LocalContext.current
    var newPlaylistName by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.onBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .background(MaterialTheme.colorScheme.onBackground)
                .padding(16.dp)
        ) {
            // Secțiunea pentru playlist nou
            NewPlaylistSection(
                newPlaylistName = newPlaylistName,
                onNameChange = { newPlaylistName = it },
                onAddPlaylist = {
                    addToNewPlaylist(newPlaylistName, track)
                    Toast.makeText(
                        context,
                        "Playlist '$newPlaylistName' created!",
                        Toast.LENGTH_SHORT
                    ).show()
                    newPlaylistName = ""
                    onDismiss()
                }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Playlists existente
            playlists.forEach { playlist ->
                PlaylistItem(
                    playlist = playlist,
                    onClick = {
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewPlaylistSection(
    newPlaylistName: String,
    onNameChange: (String) -> Unit,
    onAddPlaylist: () -> Unit
) {
    OutlinedTextField(
        value = newPlaylistName,
        onValueChange = onNameChange,
        label = { Text("Enter playlist name") },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            cursorColor = MaterialTheme.colorScheme.background,
            focusedBorderColor = MaterialTheme.colorScheme.background,
            unfocusedBorderColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
            focusedLabelColor = MaterialTheme.colorScheme.onBackground,
            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        ),
    )

    Spacer(modifier = Modifier.height(8.dp))

    val canAddNew = newPlaylistName.isNotBlank()
    Button(
        onClick = onAddPlaylist,
        modifier = Modifier.fillMaxWidth(),
        enabled = canAddNew,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(4.dp))
        Text("Add Playlist")
    }
}

@Composable
private fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(
            text = playlist.name,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
