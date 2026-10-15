package com.example.musicplayer.presentation.ui.player

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Drives the mini ↔ full-screen player morph with a single 0..1 [progress] value.
 * Everything visual derives from it, and it is read in layout/draw lambdas, so dragging
 * the sheet animates at full frame rate without recomposing the player.
 */
@Stable
class PlayerSheetState internal constructor(
    private val scope: CoroutineScope,
    initialProgress: Float = 0f,
) {

    private val animatable = Animatable(initialProgress)

    /** 0 = collapsed mini player, 1 = full screen. */
    val progress: Float get() = animatable.value.coerceIn(0f, 1f)

    /** Changes only when crossing the midpoint, so it is cheap to read in composition. */
    val isExpanded: Boolean by derivedStateOf { animatable.value > 0.5f }

    /** Pixels the sheet travels between its collapsed and expanded positions. */
    internal var travel by mutableFloatStateOf(1f)

    fun expand() {
        scope.launch { animatable.animateTo(1f, SheetSpring) }
    }

    fun collapse() {
        scope.launch { animatable.animateTo(0f, SheetSpring) }
    }

    fun snapCollapsed() {
        scope.launch { animatable.snapTo(0f) }
    }

    internal fun dragBy(deltaPx: Float) {
        scope.launch { animatable.snapTo((animatable.value - deltaPx / travel).coerceIn(0f, 1f)) }
    }

    /** Lets go after a drag: a flick wins, otherwise go to whichever end is closer. */
    internal fun settle(velocityPx: Float) {
        val target = when {
            abs(velocityPx) > FlingThreshold -> if (velocityPx < 0) 1f else 0f
            else -> if (animatable.value > 0.5f) 1f else 0f
        }
        scope.launch {
            animatable.animateTo(target, SheetSpring, initialVelocity = -velocityPx / travel)
        }
    }

    private companion object {
        val SheetSpring = spring<Float>(dampingRatio = 0.86f, stiffness = 320f)
        const val FlingThreshold = 1200f
    }
}

@Composable
fun rememberPlayerSheetState(initialProgress: Float = 0f): PlayerSheetState {
    val scope = rememberCoroutineScope()
    return remember { PlayerSheetState(scope, initialProgress) }
}
