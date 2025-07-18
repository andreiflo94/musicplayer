package com.example.musicplayer

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.musicplayer.domain.model.MusicFolder
import com.example.musicplayer.presentation.ui.screens.MusicFoldersScreen
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class MusicFoldersScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun musicFoldersScreen_displaysTitleAndItemsAndTriggersClickCallback() {
        val clickedFolders = mutableListOf<MusicFolder>()

        val folders = listOf(
            MusicFolder(name = "Rock", path = "/rock", albumIconUrl = "url1"),
            MusicFolder(name = "Jazz", path = "/jazz", albumIconUrl = "url2"),
        )

        val state = mutableStateOf(folders)

        composeTestRule.setContent {
            MusicFoldersScreen(
                title = "My Folders",
                musicFolders = state,
                onClick = { clickedFolders.add(it) }
            )
        }

        // Title
        composeTestRule.onNodeWithText("My Folders").assertIsDisplayed()

        // Items
        folders.forEach {
            composeTestRule.onNodeWithText(it.name).assertIsDisplayed()
        }

        // Click
        composeTestRule.onNodeWithText("Rock").performClick()
        assertEquals(1, clickedFolders.size)
        assertEquals("Rock", clickedFolders[0].name)
    }

    @Test
    fun musicFoldersScreen_emptyList_showsNoItems() {
        val state = mutableStateOf(emptyList<MusicFolder>())

        composeTestRule.setContent {
            MusicFoldersScreen(
                title = "Empty",
                musicFolders = state,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("Empty").assertIsDisplayed()
        composeTestRule.onAllNodes(hasClickAction()).assertCountEquals(0)
    }

    @Test
    fun musicFoldersScreen_multipleItems_displaysCorrectCount() {
        val state = mutableStateOf(
            listOf(
                MusicFolder("Folder A", "", "urlA"),
                MusicFolder("Folder B", "", "urlB"),
                MusicFolder("Folder C", "", "urlC")
            )
        )

        composeTestRule.setContent {
            MusicFoldersScreen(
                title = "Multiple Folders",
                musicFolders = state,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("Multiple Folders").assertIsDisplayed()

        listOf("Folder A", "Folder B", "Folder C").forEach {
            composeTestRule.onNodeWithText(it).assertIsDisplayed()
        }
    }

    @Test
    fun musicFoldersScreen_updatesWhenStateChanges() {
        val state = mutableStateOf(
            listOf(
                MusicFolder("Initial", "", "initial_url")
            )
        )

        composeTestRule.setContent {
            MusicFoldersScreen(
                title = "Dynamic Update",
                musicFolders = state,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("Initial").assertIsDisplayed()

        // Update
        state.value = listOf(
            MusicFolder("Updated One", "", "url1"),
            MusicFolder("Updated Two", "", "url2")
        )

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Initial").assertDoesNotExist()
        composeTestRule.onNodeWithText("Updated One").assertIsDisplayed()
        composeTestRule.onNodeWithText("Updated Two").assertIsDisplayed()
    }
}
