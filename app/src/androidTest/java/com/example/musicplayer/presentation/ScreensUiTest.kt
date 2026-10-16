package com.example.musicplayer.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.ThemeSettings
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.ui.components.NowPlaying
import com.example.musicplayer.presentation.ui.screens.PlaylistTracksScreen
import com.example.musicplayer.presentation.ui.screens.PlaylistsScreen
import com.example.musicplayer.presentation.ui.screens.Screen
import com.example.musicplayer.presentation.ui.screens.TracksScreen
import com.example.musicplayer.presentation.ui.theme.MusicplayerTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ScreensUiTest {

    @get:Rule
    val compose = createComposeRule()

    private val tracks = listOf(
        Track("/m/1.mp3", null, artistName = "Metallica", trackName = "One"),
        Track("/m/2.mp3", null, artistName = "Metallica", trackName = "Fade to Black"),
        Track("/m/3.mp3", null, trackName = "Untagged"),
    )

    @Test
    fun folderPathsSurviveNavigationEncoding() {
        // Spaces used to come back as "+", so folders with spaces opened empty
        val path = "/storage/emulated/0/Music/My Album + Live (100%) #1"
        var received: String? = null
        compose.setContent {
            val nav = rememberNavController()
            NavHost(nav, startDestination = "start") {
                composable("start") {
                    LaunchedEffect(Unit) { nav.navigate(Screen.TracksScreen.createRoute(path)) }
                }
                composable(
                    Screen.TracksScreen.route + "/{path}",
                    arguments = listOf(navArgument("path") { type = NavType.StringType })
                ) { entry ->
                    received = entry.arguments?.getString("path")
                    Text("arrived")
                }
            }
        }
        compose.onNodeWithText("arrived").assertIsDisplayed()
        assertEquals(path, received)
    }

    @Test
    fun playlistTitlesSurviveNavigationEncoding() {
        val title = "Rock & Roll? 50% off/on"
        var received: Pair<Long, String>? = null
        compose.setContent {
            val nav = rememberNavController()
            NavHost(nav, startDestination = "start") {
                composable("start") {
                    LaunchedEffect(Unit) {
                        nav.navigate(
                            Screen.PlaylistTracksScreen.createRoute(
                                12,
                                title
                            )
                        )
                    }
                }
                composable(
                    Screen.PlaylistTracksScreen.route + "?playlistId={playlistId}&playlistTitle={playlistTitle}",
                    arguments = listOf(
                        navArgument("playlistId") { type = NavType.LongType; defaultValue = -1 },
                        navArgument("playlistTitle") {
                            type = NavType.StringType; defaultValue = ""
                        }
                    )
                ) { entry ->
                    received =
                        entry.arguments!!.getLong("playlistId") to entry.arguments!!.getString("playlistTitle")!!
                    Text("arrived")
                }
            }
        }
        compose.onNodeWithText("arrived").assertIsDisplayed()
        assertEquals(12L to title, received)
    }

    @Test
    fun tracksScreenListsTracksAndForwardsPlayback() {
        val played = mutableListOf<Int>()
        val playAll = mutableListOf<Boolean>()
        compose.setContent {
            MusicplayerTheme(ThemeSettings()) {
                TracksScreen(
                    title = "Master of Puppets",
                    tracks = tracks,
                    playlists = emptyList(),
                    nowPlaying = NowPlaying(),
                    onBack = {},
                    addToNewPlaylist = { _, _ -> },
                    addToPlaylist = { _, _ -> },
                    onPlay = { played += it },
                    onPlayAll = { playAll += it },
                )
            }
        }

        compose.onNodeWithText("3 tracks").assertIsDisplayed()
        compose.onNodeWithText("Fade to Black").performClick()
        compose.onNodeWithText("Untagged").assertIsDisplayed()
        compose.onNodeWithText("Play").performClick()
        compose.onNodeWithText("Shuffle").performClick()

        compose.runOnIdle {
            assertEquals(listOf(1), played)
            assertEquals(listOf(false, true), playAll)
        }
    }

    @Test
    fun tracksScreenAddsATrackToANewOrExistingPlaylist() {
        val created = mutableListOf<Pair<String, Track>>()
        val added = mutableListOf<Pair<Long, Track>>()
        compose.setContent {
            MusicplayerTheme(ThemeSettings()) {
                TracksScreen(
                    title = "Album",
                    tracks = tracks,
                    playlists = listOf(Playlist(4, "Gym")),
                    nowPlaying = NowPlaying(),
                    onBack = {},
                    addToNewPlaylist = { name, track -> created += name to track },
                    addToPlaylist = { id, track -> added += id to track },
                    onPlay = {},
                    onPlayAll = {},
                )
            }
        }

        compose.onAllNodesWithContentDescription("Add to playlist").onFirst().performClick()
        compose.onNodeWithText("New playlist name").performTextInput("  Road trip ")
        compose.onNodeWithContentDescription("Create playlist").performClick()
        compose.runOnIdle { assertEquals(listOf("Road trip" to tracks[0]), created) }

        compose.onAllNodesWithContentDescription("Add to playlist").onFirst().performClick()
        compose.onNodeWithText("Gym").performClick()
        compose.runOnIdle { assertEquals(listOf(4L to tracks[0]), added) }
    }

    @Test
    fun emptyAndLoadingStatesAreShown() {
        val list = androidx.compose.runtime.mutableStateOf<List<Track>?>(null)
        compose.setContent {
            MusicplayerTheme(ThemeSettings()) {
                PlaylistTracksScreen(
                    title = "Empty",
                    tracks = list.value,
                    nowPlaying = NowPlaying(),
                    onBack = {},
                    onPlay = {},
                    onPlayAll = {},
                    onRemove = {},
                )
            }
        }
        compose.onNodeWithText("Loading…").assertIsDisplayed()

        list.value = emptyList()
        compose.onNodeWithText("An empty stage").assertIsDisplayed()
        compose.onNodeWithText("0 tracks").assertIsDisplayed()
    }

    @Test
    fun playlistTrackCanBeRemoved() {
        val removed = mutableListOf<Track>()
        compose.setContent {
            MusicplayerTheme(ThemeSettings()) {
                PlaylistTracksScreen(
                    title = "Gym",
                    tracks = tracks.take(1),
                    nowPlaying = NowPlaying(),
                    onBack = {},
                    onPlay = {},
                    onPlayAll = {},
                    onRemove = { removed += it },
                )
            }
        }
        compose.onNodeWithContentDescription("Remove from playlist").performClick()
        compose.runOnIdle { assertEquals(tracks.take(1), removed) }
    }

    @Test
    fun deletingAPlaylistAsksFirst() {
        val removed = mutableListOf<Playlist>()
        compose.setContent {
            MusicplayerTheme(ThemeSettings()) {
                PlaylistsScreen(
                    playlists = listOf(Playlist(1, "Chill")),
                    onOpenThemes = {},
                    onClick = {},
                    onRemove = { removed += it },
                )
            }
        }

        compose.onNodeWithContentDescription("Delete playlist").performClick()
        compose.onNodeWithText("Keep").performClick()
        compose.runOnIdle { assertEquals(emptyList<Playlist>(), removed) }

        compose.onNodeWithContentDescription("Delete playlist").performClick()
        compose.onNodeWithText("Delete").performClick()
        compose.runOnIdle { assertEquals(listOf(Playlist(1, "Chill")), removed) }
    }
}
