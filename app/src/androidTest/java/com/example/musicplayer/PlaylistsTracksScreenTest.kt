package com.example.musicplayer

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.ui.screens.PlaylistTracksScreen
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class PlaylistTracksScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun playlistTracksScreen_displaysTitleAndTracks() {
        val state = mutableStateOf(
            listOf(
                Track(path = "1", albumIconUrl = "url1", artistName = "AC/DC", trackName = "Thunderstruck"),
                Track(path = "2", albumIconUrl = "url2", artistName = "Queen", trackName = "Bohemian Rhapsody")
            )
        )

        composeTestRule.setContent {
            PlaylistTracksScreen(
                title = "My Tracks",
                tracks = state,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("My Tracks").assertIsDisplayed()
        composeTestRule.onNodeWithText("AC/DC - Thunderstruck").assertIsDisplayed()
        composeTestRule.onNodeWithText("Queen - Bohemian Rhapsody").assertIsDisplayed()
    }

    @Test
    fun playlistTracksScreen_emptyList_showsNothing() {
        val state = mutableStateOf(emptyList<Track>())

        composeTestRule.setContent {
            PlaylistTracksScreen(
                title = "Empty",
                tracks = state,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("Empty").assertIsDisplayed()
        composeTestRule.onAllNodesWithContentDescription("album image").assertCountEquals(0)
    }

    @Test
    fun playlistTracksScreen_clickOnTrack_triggersCallback() {
        val clicked = mutableListOf<String>()

        val state = mutableStateOf(
            listOf(
                Track(path = "123", albumIconUrl = "url", artistName = "Artist", trackName = "ClickMe")
            )
        )

        composeTestRule.setContent {
            PlaylistTracksScreen(
                title = "Click Test",
                tracks = state,
                onClick = { clicked.add(it.path) }
            )
        }

        composeTestRule.onNodeWithText("Artist - ClickMe").performClick()

        assertEquals(listOf("123"), clicked)
    }

    @Test
    fun playlistTracksScreen_multipleItems_correctCount() {
        val state = mutableStateOf(
            List(5) { i ->
                Track(
                    path = i.toString(),
                    albumIconUrl = "url$i",
                    artistName = "Artist $i",
                    trackName = "Song $i"
                )
            }
        )

        composeTestRule.setContent {
            PlaylistTracksScreen(
                title = "Count Test",
                tracks = state,
                onClick = {}
            )
        }

        state.value.forEach {
            composeTestRule.onNodeWithText(it.name).assertIsDisplayed()
        }

        composeTestRule.onAllNodesWithContentDescription("album image")
            .assertCountEquals(5)
    }

    @Test
    fun playlistTracksScreen_updatesWhenStateChanges() {
        val state = mutableStateOf(
            listOf(
                Track(path = "1", albumIconUrl = "url", artistName = "Old", trackName = "Track")
            )
        )

        composeTestRule.setContent {
            PlaylistTracksScreen(
                title = "Dynamic",
                tracks = state,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("Old - Track").assertIsDisplayed()

        state.value = listOf(
            Track(path = "2", albumIconUrl = "url2", artistName = "New", trackName = "Hit")
        )

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Old - Track").assertDoesNotExist()
        composeTestRule.onNodeWithText("New - Hit").assertIsDisplayed()
    }

    @Test
    fun playlistTracksScreen_albumImage_hasContentDescription() {
        val state = mutableStateOf(
            listOf(
                Track(path = "1", albumIconUrl = "url", artistName = "Visual", trackName = "Test")
            )
        )

        composeTestRule.setContent {
            PlaylistTracksScreen(
                title = "Image Test",
                tracks = state,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("album image").assertIsDisplayed()
    }

    @Test
    fun playlistTracksScreen_nameFallback_whenFieldsAreMissing() {
        val state = mutableStateOf(
            listOf(
                Track(path = "1", albumIconUrl = null, artistName = "", trackName = "JustTitle"),
                Track(path = "2", albumIconUrl = null, artistName = "OnlyArtist", trackName = "")
            )
        )

        composeTestRule.setContent {
            PlaylistTracksScreen(
                title = "Fallback",
                tracks = state,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("JustTitle").assertIsDisplayed()
        composeTestRule.onNodeWithText("OnlyArtist").assertIsDisplayed()
    }
}
