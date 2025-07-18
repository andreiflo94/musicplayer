package com.example.musicplayer

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.swipeDown
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.ui.screens.TracksScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TracksScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val sampleTracks = listOf(
        Track(
            path = "/music/track1.mp3",
            albumIconUrl = "url1",
            artistName = "Artist 1",
            trackName = "Track 1"
        ),
        Track(
            path = "/music/track2.mp3",
            albumIconUrl = "url2",
            artistName = "Artist 2",
            trackName = "Track 2"
        )
    )

    private val samplePlaylists = listOf(
        Playlist(id = 1L, name = "Playlist 1"),
        Playlist(id = 2L, name = "Playlist 2")
    )

    @Test
    fun title_isDisplayed() {
        setContent()
        composeTestRule.onNodeWithText("My Tracks").assertIsDisplayed()
    }

    @Test
    fun trackItems_areDisplayed() {
        setContent()
        for (track in sampleTracks) {
            composeTestRule.onNodeWithText(track.artistName).assertIsDisplayed()
            composeTestRule.onNodeWithText(track.trackName).assertIsDisplayed()
        }
    }


    @Test
    fun clickingTrack_callsOnClick() {
        val tracks = mutableStateOf(sampleTracks)
        val playlists = mutableStateOf(samplePlaylists)
        var clickedTrackPath: String? = null
        composeTestRule.setContent {
            MaterialTheme {
                TracksScreen(
                    title = "My Tracks",
                    tracks = tracks,
                    playlists = playlists,
                    addToNewPlaylist = { _, _ -> },
                    addToPlaylist = { _, _ -> },
                    onClick = { track -> clickedTrackPath = track.path }
                )
            }
        }

        composeTestRule.onNodeWithText(sampleTracks[0].trackName).performClick()

        assert(clickedTrackPath == sampleTracks[0].path)
    }

    @Test
    fun clickingAddToPlaylist_showsBottomSheet() {
        setContent()
        composeTestRule.onAllNodesWithContentDescription("AddToPlaylist").onFirst().performClick()

        composeTestRule.onNodeWithText("Playlist 1").assertIsDisplayed()
    }

    @Test
    fun dismissBottomSheet_hidesIt_bySwipeDown() {
        val tracks = mutableStateOf(sampleTracks)
        val playlists = mutableStateOf(samplePlaylists)
        composeTestRule.setContent {
            MaterialTheme {
                TracksScreen(
                    title = "My Tracks",
                    tracks = tracks,
                    playlists = playlists,
                    addToNewPlaylist = { _, _ -> },
                    addToPlaylist = { _, _ -> },
                    onClick = {}
                )
            }
        }

        // Open the bottom sheet by clicking "AddToPlaylist" icon on first track
        composeTestRule.onAllNodesWithContentDescription("AddToPlaylist").onFirst().performClick()

        // Verify bottom sheet is shown (checking for playlist text)
        composeTestRule.onNodeWithText("Playlist 1").assertIsDisplayed()

        // Find the bottom sheet node (adjust selector if needed)
        val bottomSheetNode = composeTestRule.onNodeWithText("Playlist 1").fetchSemanticsNode()

        // Perform swipe down on the bottom sheet
        composeTestRule.onNode(hasText("Playlist 1")).performGesture {
            swipeDown()
        }

        // Alternatively, if you can target the sheet container by contentDescription or testTag:
        // composeTestRule.onNodeWithTag("PlaylistBottomSheet").performGesture { swipeDown() }

        // Verify bottom sheet is dismissed
        composeTestRule.onNodeWithText("Playlist 1").assertDoesNotExist()
    }


    @Test
    fun playlistsAreDisplayedInBottomSheet() {
        setContent()
        composeTestRule.onAllNodesWithContentDescription("AddToPlaylist").onFirst().performClick()

        for (playlist in samplePlaylists) {
            composeTestRule.onNodeWithText(playlist.name).assertIsDisplayed()
        }
    }

    private fun setContent() {
        val tracks = mutableStateOf(sampleTracks)
        val playlists = mutableStateOf(samplePlaylists)
        composeTestRule.setContent {
            MaterialTheme {
                TracksScreen(
                    title = "My Tracks",
                    tracks = tracks,
                    playlists = playlists,
                    addToNewPlaylist = { _, _ -> },
                    addToPlaylist = { _, _ -> },
                    onClick = {}
                )
            }
        }
    }
}

