package com.example.musicplayer.presentation.ui.components

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
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
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
import com.example.musicplayer.domain.model.MusicFolder
import com.example.musicplayer.domain.model.Playlist

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistBottomSheet(
    folder: MusicFolder,
    playlists: State<List<Playlist>>,
    addToNewPlaylist: (playListName: String, musicFolder: MusicFolder) -> Unit,
    addToPlaylist: (playlistId: Long, musicFolder: MusicFolder) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = false) // Ensure it expands
    var newPlaylistName by remember { mutableStateOf("") }
    val context = LocalContext.current // Get the context for showing Toast

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.onBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f) // <-- Adjust this to control how much space it takes
                .background(MaterialTheme.colorScheme.onBackground)
                .padding(16.dp)
        ) {
            // Input Field for New Playlist with Placeholder
            OutlinedTextField(
                value = newPlaylistName,
                onValueChange = { newPlaylistName = it },
                label = { Text("Enter playlist name") }, // Placeholder text
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black, // Text color
                    placeholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), // Hint color
                    cursorColor = MaterialTheme.colorScheme.background, // Cursor color
                    focusedBorderColor = MaterialTheme.colorScheme.background, // Border when focused
                    unfocusedBorderColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f), // Border when not focused
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground, // Label color when focused
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f) // Label color when not focused
                ),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Add Playlist Button
            Button(
                onClick = {
                    if (newPlaylistName.isNotBlank()) {
                        addToNewPlaylist(newPlaylistName, folder)
                        Toast.makeText(context, "Playlist '$newPlaylistName' created!", Toast.LENGTH_SHORT).show() // Show Toast
                        newPlaylistName = ""
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = newPlaylistName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background, // Background color
                    contentColor = MaterialTheme.colorScheme.onBackground, // Text/Icon color
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), // Disabled background
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) // Disabled text/icon
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Playlist")
            }

            Divider()

            // List of Existing Playlists
            playlists.value.forEach { playlist ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* Handle adding song to this playlist */ }
                        .padding(vertical = 12.dp)
                        .clickable {
                            addToPlaylist(playlist.id, folder)
                            Toast.makeText(context, "Track added to '${playlist.name}'", Toast.LENGTH_SHORT).show() // Show Toast
                            onDismiss()
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    Text(text = playlist.name, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
