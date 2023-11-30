package com.example.musicplayer.mainfeature.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MusicOverlay(isPlaying: Boolean) {
    val overlayText = if (isPlaying) "Music is playing" else "Music is paused"
    val overlayColor = if (isPlaying) Color.Green else Color.Red

    // Get the context to control the overlay
    val context = LocalContext.current

    // Conditional rendering of the overlay based on playback status
    if (isPlaying) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // You can customize the content of your overlay here
            Text(
                text = overlayText,
                color = Color.White,
                modifier = Modifier.background(color = overlayColor)
            )
            // Add other UI elements as needed for playback controls, track info, etc.
        }
    }
    // Return an empty box if playback is paused
    else {
        Box(modifier = Modifier.fillMaxSize()) {}
    }
}

@Preview
@Composable
fun PreviewMusicOverlay() {
    // Preview the overlay screen
    MusicOverlay(isPlaying = true)
}
