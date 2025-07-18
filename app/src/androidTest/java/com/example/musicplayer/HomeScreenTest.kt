package com.example.musicplayer

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.musicplayer.presentation.ui.screens.HomeScreen
import com.example.musicplayer.presentation.viewmodels.AudioState
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun defaultAudioState(
        stopped: Boolean,
        isPlaying: Boolean = true
    ) = AudioState(
        trackName = "Test Song",
        trackArtUrl = "url",
        progress = 0.3f,
        trackProgressFormatted = "0:45",
        trackDurationFormatted = "2:30",
        isPlaying = isPlaying,
        stopped = stopped,
        hasNextMediaItem = true
    )

    @Test
    fun homeScreen_displaysMainContent_always() {
        val audioState = mutableStateOf(defaultAudioState(stopped = true))

        composeTestRule.setContent {
            HomeScreen(
                audioState = audioState,
                content = {
                    androidx.compose.material3.Text("Main Screen")
                },
                audioPlayer = {
                    androidx.compose.material3.Text("Player Widget")
                }
            )
        }

        composeTestRule.onNodeWithText("Main Screen").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysAudioPlayer_whenPlayingAndNotStopped() {
        val audioState = mutableStateOf(defaultAudioState(stopped = false, isPlaying = true))

        composeTestRule.setContent {
            HomeScreen(
                audioState = audioState,
                content = {
                    androidx.compose.material3.Text("Main UI")
                },
                audioPlayer = {
                    androidx.compose.material3.Text("Audio Now Playing")
                }
            )
        }

        composeTestRule.onNodeWithText("Main UI").assertIsDisplayed()
        composeTestRule.onNodeWithText("Audio Now Playing").assertIsDisplayed()
    }

    @Test
    fun homeScreen_doesNotDisplayAudioPlayer_whenStoppedIsTrue() {
        val audioState = mutableStateOf(defaultAudioState(stopped = true, isPlaying = false))

        composeTestRule.setContent {
            HomeScreen(
                audioState = audioState,
                content = {
                    androidx.compose.material3.Text("Visible Content")
                },
                audioPlayer = {
                    androidx.compose.material3.Text("Hidden Audio Bar")
                }
            )
        }

        composeTestRule.onNodeWithText("Visible Content").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hidden Audio Bar").assertDoesNotExist()
    }

    @Test
    fun homeScreen_audioPlayerAppears_whenStateChangesToNotStopped() {
        val audioState = mutableStateOf(defaultAudioState(stopped = true))

        composeTestRule.setContent {
            HomeScreen(
                audioState = audioState,
                content = {
                    androidx.compose.material3.Text("Main Content")
                },
                audioPlayer = {
                    androidx.compose.material3.Text("Dynamic Player")
                }
            )
        }

        composeTestRule.onNodeWithText("Dynamic Player").assertDoesNotExist()

        audioState.value = audioState.value.copy(stopped = false)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Dynamic Player").assertIsDisplayed()
    }

    @Test
    fun homeScreen_audioPlayerDisappears_whenStateChangesToStopped() {
        val audioState = mutableStateOf(defaultAudioState(stopped = false))

        composeTestRule.setContent {
            HomeScreen(
                audioState = audioState,
                content = {
                    androidx.compose.material3.Text("Main UI")
                },
                audioPlayer = {
                    androidx.compose.material3.Text("Audio Footer")
                }
            )
        }

        composeTestRule.onNodeWithText("Audio Footer").assertIsDisplayed()

        // Se oprește redarea
        audioState.value = audioState.value.copy(stopped = true)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Audio Footer").assertDoesNotExist()
    }

    @Test
    fun homeScreen_displaysTrackName_whenAudioPlaying() {
        val audioState =
            mutableStateOf(defaultAudioState(stopped = false).copy(trackName = "Cool Track"))

        composeTestRule.setContent {
            HomeScreen(
                audioState = audioState,
                content = {
                    androidx.compose.material3.Text("Container")
                },
                audioPlayer = {
                    androidx.compose.material3.Text(audioState.value.trackName)
                }
            )
        }

        composeTestRule.onNodeWithText("Cool Track").assertIsDisplayed()
    }

    @Test
    fun homeScreen_recomposes_whenProgressUpdates() {
        val audioState = mutableStateOf(defaultAudioState(stopped = false).copy(progress = 0.2f))

        composeTestRule.setContent {
            HomeScreen(
                audioState = audioState,
                content = {
                    androidx.compose.material3.Text("UI Root")
                },
                audioPlayer = {
                    androidx.compose.material3.Text("Progress: ${audioState.value.progress}")
                }
            )
        }

        composeTestRule.onNodeWithText("Progress: 0.2").assertIsDisplayed()

        audioState.value = audioState.value.copy(progress = 0.6f)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Progress: 0.6").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysDurationFormatted_correctly() {
        val audioState = mutableStateOf(
            defaultAudioState(stopped = false).copy(
                trackDurationFormatted = "4:20"
            )
        )

        composeTestRule.setContent {
            HomeScreen(
                audioState = audioState,
                content = {
                    androidx.compose.material3.Text("Base UI")
                },
                audioPlayer = {
                    androidx.compose.material3.Text("Duration: ${audioState.value.trackDurationFormatted}")
                }
            )
        }

        composeTestRule.onNodeWithText("Duration: 4:20").assertIsDisplayed()
    }

    @Test
    fun homeScreen_onlyAudioPlayerRecomposes_whenProgressChanges() {
        val audioState = mutableStateOf(defaultAudioState(stopped = false, isPlaying = true, ).copy(progress = 0.1f))

        var contentRecomposeCount = 0
        var audioPlayerRecomposeCount = 0

        composeTestRule.setContent {
            HomeScreen(
                audioState = audioState,
                content = {
                    contentRecomposeCount++
                    androidx.compose.material3.Text("Main UI")
                },
                audioPlayer = {
                    audioPlayerRecomposeCount++
                    androidx.compose.material3.Text("Progress: ${audioState.value.progress}")
                }
            )
        }

        assertEquals(1, contentRecomposeCount)
        assertEquals(1, audioPlayerRecomposeCount)

        audioState.value = audioState.value.copy(progress = 0.8f)
        composeTestRule.waitForIdle()

        assertEquals(1, contentRecomposeCount)
        assertEquals(2, audioPlayerRecomposeCount)
    }

}
