package com.example.musicplayer.presentation.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.musicplayer.domain.model.MusicFolder
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.ui.components.AuroraBackground
import com.example.musicplayer.presentation.ui.player.PlayerActions
import com.example.musicplayer.presentation.ui.theme.MusicplayerTheme
import com.example.musicplayer.presentation.viewmodels.TrackState

/** Wraps a component preview in the app theme on the theme background. */
@Composable
fun PreviewSurface(content: @Composable () -> Unit) {
    MusicplayerTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background)) { content() }
    }
}

/** Full-screen preview with the aurora backdrop the screens are drawn over in the app. */
@Composable
fun PreviewScreen(content: @Composable () -> Unit) {
    MusicplayerTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            AuroraBackground(Modifier.fillMaxSize())
            content()
        }
    }
}

/**
 * Sample data for previews. Art URLs are null on purpose: Glide can't load in previews,
 * so artwork falls back to the generated gradient covers.
 */
object PreviewData {

    val tracks = listOf(
        "72 Seasons", "Shadows Follow", "Screaming Suicide", "Sleepwalk My Life Away",
        "You Must Burn!", "Lux Æterna", "Crown of Barbed Wire", "Chasing Light"
    ).mapIndexed { i, title ->
        Track(
            path = "/storage/emulated/0/Music/72 Seasons/${i + 1} $title.mp3",
            albumIconUrl = null,
            artistName = "Metallica",
            trackName = title
        )
    }

    val folders = listOf(
        MusicFolder("72 Seasons", "/storage/emulated/0/Music/72 Seasons", null, "Metallica"),
        MusicFolder("Over the Horizon", "/storage/emulated/0/Music/Samsung", null, "Samsung"),
        MusicFolder("Voice Recorder", "/storage/emulated/0/Recordings/Voice Recorder", null),
        MusicFolder("Road trip", "/storage/emulated/0/Music/Road trip", null),
        MusicFolder("Lo-fi", "/storage/emulated/0/Music/Lo-fi", null),
    )

    val playlists = listOf(
        Playlist(1, "Workout"),
        Playlist(2, "Late night drive"),
        Playlist(3, "Focus"),
    )

    val trackState = TrackState(
        trackName = tracks[0].name,
        title = tracks[0].trackName,
        artist = tracks[0].artistName,
        trackPath = tracks[0].path,
        trackDurationFormatted = "7:39",
        durationMs = 459_000,
        isPlaying = true,
        stopped = false,
        hasNextMediaItem = true
    )

    val playerActions = PlayerActions(onPlayPause = {}, onNext = {}, onPrevious = {}, onSeek = {})
}
