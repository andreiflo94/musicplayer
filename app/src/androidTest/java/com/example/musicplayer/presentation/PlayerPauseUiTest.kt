package com.example.musicplayer.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.example.musicplayer.domain.model.ThemeSettings
import com.example.musicplayer.presentation.ui.player.PlayerActions
import com.example.musicplayer.presentation.ui.player.PlayerSheet
import com.example.musicplayer.presentation.ui.player.rememberPlayerSheetState
import com.example.musicplayer.presentation.ui.theme.MusicplayerTheme
import com.example.musicplayer.presentation.viewmodels.TrackState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Pressing pause in the full-screen player used to crash while its halo faded out. */
class PlayerPauseUiTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun pausingAndResumingInTheFullScreenPlayerKeepsRunning() {
        var state by mutableStateOf(
            TrackState(
                title = "One",
                artist = "Metallica",
                trackPath = "/m/1.mp3",
                durationMs = 240_000,
                isPlaying = true,
                stopped = false
            )
        )
        var toggles = 0
        val actions = PlayerActions(
            onPlayPause = { toggles++; state = state.copy(isPlaying = !state.isPlaying) },
            onNext = {}, onPrevious = {}, onSeek = {}
        )
        // The player loops animations forever (halo breath, vinyl, wave), so it never goes idle; drive
        // the frame clock by hand, well past the end of the halo spring and its overshoot
        compose.mainClock.autoAdvance = false
        compose.setContent {
            MusicplayerTheme(ThemeSettings()) {
                PlayerSheet(
                    state = rememberPlayerSheetState(initialProgress = 1f),
                    trackState = state,
                    progress = { 0.3f },
                    elapsed = { "1:12" },
                    actions = actions,
                    collapsedBottomOffset = 0.dp
                )
            }
        }

        compose.onAllNodesWithContentDescription("Pause").onFirst().performClick()
        repeat(120) { compose.mainClock.advanceTimeByFrame() }
        compose.onAllNodesWithContentDescription("Play").onFirst().performClick()
        repeat(120) { compose.mainClock.advanceTimeByFrame() }

        assertEquals(2, toggles)
    }
}
