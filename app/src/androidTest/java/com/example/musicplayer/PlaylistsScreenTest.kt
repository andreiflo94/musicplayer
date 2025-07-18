package com.example.musicplayer

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.presentation.ui.screens.PlaylistsScreen
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class PlaylistsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun playlistsScreen_displaysTitleAndItemsAndTriggersCallbacks() {
        val clickedPlaylists = mutableListOf<Playlist>()
        val removedPlaylists = mutableListOf<Playlist>()

        val playlists = listOf(
            Playlist(id = 1, name = "Rock Classics"),
            Playlist(id = 2, name = "Jazz Vibes"),
        )

        val state = mutableStateOf(playlists)

        composeTestRule.setContent {
            PlaylistsScreen(
                title = "My Playlists",
                playlists = state,
                onClick = { clickedPlaylists.add(it) },
                onRemove = { removedPlaylists.add(it) }
            )
        }

        // Assert title is shown
        composeTestRule
            .onNodeWithText("My Playlists")
            .assertIsDisplayed()

        // Assert all playlist names are shown
        playlists.forEach { playlist ->
            composeTestRule
                .onNodeWithText(playlist.name)
                .assertIsDisplayed()
        }

        // Click on a playlist item
        composeTestRule
            .onNodeWithText("Rock Classics")
            .performClick()

        assertEquals(1, clickedPlaylists.size)
        assertEquals("Rock Classics", clickedPlaylists[0].name)

        // Click on delete icon for "Jazz Vibes"
        composeTestRule
            .onAllNodesWithContentDescription("Remove Playlist")[1]
            .performClick()

        assertEquals(1, removedPlaylists.size)
        assertEquals("Jazz Vibes", removedPlaylists[0].name)
    }

    @Test
    fun playlistsScreen_emptyList_showsNoItems() {
        val state = mutableStateOf(emptyList<Playlist>())

        composeTestRule.setContent {
            PlaylistsScreen(
                title = "Empty List",
                playlists = state,
                onClick = {},
                onRemove = {}
            )
        }

        composeTestRule.onNodeWithText("Empty List").assertIsDisplayed()

        composeTestRule
            .onAllNodesWithContentDescription("Remove Playlist")
            .assertCountEquals(0)
    }

    @Test
    fun playlistsScreen_multipleItems_displaysCorrectCount() {
        val state = mutableStateOf(
            listOf(
                Playlist(1, "Playlist A"),
                Playlist(2, "Playlist B"),
                Playlist(3, "Playlist C")
            )
        )

        composeTestRule.setContent {
            PlaylistsScreen(
                title = "Multiple Items",
                playlists = state,
                onClick = {},
                onRemove = {}
            )
        }

        composeTestRule
            .onAllNodesWithContentDescription("Remove Playlist")
            .assertCountEquals(3)

        listOf("Playlist A", "Playlist B", "Playlist C").forEach {
            composeTestRule.onNodeWithText(it).assertIsDisplayed()
        }
    }

    @Test
    fun playlistsScreen_removeEachItem_individualCallbacksAreTriggered() {
        val removed = mutableListOf<Long>()

        val state = mutableStateOf(
            listOf(
                Playlist(1, "P1"),
                Playlist(2, "P2"),
                Playlist(3, "P3")
            )
        )

        composeTestRule.setContent {
            PlaylistsScreen(
                title = "Test Remove",
                playlists = state,
                onClick = {},
                onRemove = { removed.add(it.id) }
            )
        }

        val removeButtons = composeTestRule.onAllNodesWithContentDescription("Remove Playlist")

        removeButtons[0].performClick()
        removeButtons[1].performClick()

        assertEquals(listOf(1, 2).toString(), removed.toString())
    }

    @Test
    fun playlistsScreen_updatesWhenStateChanges() {
        val playlistState = mutableStateOf(
            listOf(
                Playlist(1, "Old One")
            )
        )

        composeTestRule.setContent {
            PlaylistsScreen(
                title = "Dynamic Update",
                playlists = playlistState,
                onClick = {},
                onRemove = {}
            )
        }

        composeTestRule.onNodeWithText("Old One").assertIsDisplayed()

        // Update state
        playlistState.value = listOf(
            Playlist(2, "New One"),
            Playlist(3, "Another One")
        )

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Old One").assertDoesNotExist()
        composeTestRule.onNodeWithText("New One").assertIsDisplayed()
        composeTestRule.onNodeWithText("Another One").assertIsDisplayed()
    }
}
