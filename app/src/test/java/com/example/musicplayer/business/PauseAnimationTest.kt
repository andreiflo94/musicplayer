package com.example.musicplayer.business

import androidx.compose.animation.core.TargetBasedAnimation
import androidx.compose.animation.core.VectorConverter
import androidx.compose.ui.graphics.Color
import com.example.musicplayer.presentation.ui.player.haloColor
import com.example.musicplayer.presentation.ui.theme.Motion
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pressing pause on a playing song crashed the app: the play button's halo fades out on the
 * app's springy motion curve, which dips just below zero, and a negative alpha is rejected.
 */
class PauseAnimationTest {

    /** Every frame of the halo's energy springing between playing (1) and paused (0). */
    private fun frames(from: Float, to: Float): List<Float> {
        val animation = TargetBasedAnimation(Motion.fluid(), Float.VectorConverter, from, to)
        return (0..animation.durationNanos step 1_000_000L).map { animation.getValueFromNanos(it) }
    }

    @Test
    fun `pausing a playing song animates the play button without crashing`() {
        val pausing = frames(from = 1f, to = 0f)
        // The curve really does overshoot past "paused"; that's what used to crash
        assertTrue(pausing.min() < 0f)

        pausing.forEach { energy ->
            val alpha = haloColor(Color(0xFFB9A6FF), energy).alpha
            assertTrue("alpha $alpha at energy $energy", alpha in 0f..0.22f)
        }
    }

    @Test
    fun `resuming a paused song animates the play button without crashing`() {
        frames(from = 0f, to = 1f).forEach { energy ->
            assertTrue(haloColor(Color(0xFF6C4DF6), energy).alpha in 0f..0.22f)
        }
    }
}
