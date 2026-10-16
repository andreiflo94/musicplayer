package com.example.musicplayer.presentation

import android.Manifest
import android.os.Build
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.media3.common.util.UnstableApi
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

/** Launches the real app (Hilt graph, theme, permission gate, tabs) on the device. */
@UnstableApi
class MainActivityTest {

    private val compose = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules: RuleChain = RuleChain
        .outerRule(
            GrantPermissionRule.grant(
                if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_AUDIO
                else Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
        .around(compose)

    @Test
    fun opensOnTheLibraryAndSwitchesTabs() {
        compose.onAllNodesWithText("Library").onFirst().assertIsDisplayed()

        compose.onNodeWithContentDescription("Playlists").performClick()
        compose.waitUntil(5_000) {
            // the tab label plus the Playlists screen header
            compose.onAllNodesWithText("Playlists").fetchSemanticsNodes().size >= 2
        }

        compose.onNodeWithContentDescription("Themes").performClick()
        compose.onNodeWithText("Ocean").assertExists()
    }
}
