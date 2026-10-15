package com.example.musicplayer.presentation.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import com.example.musicplayer.presentation.ui.theme.Motion
import kotlinx.coroutines.delay

/**
 * Clickable that squishes while pressed and springs back on release.
 * The scale is only read inside the graphics layer, so pressing never recomposes the content.
 */
fun Modifier.bounceClick(
    enabled: Boolean = true,
    pressedScale: Float = 0.94f,
    shape: Shape = RectangleShape,
    onClick: () -> Unit,
): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = Motion.bouncy(),
        label = "bounce"
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            // Clipping here keeps the ripple inside rounded cards and round buttons.
            clip = shape != RectangleShape
            this.shape = shape
        }
        .clickable(
            interactionSource = interaction,
            indication = LocalIndication.current,
            enabled = enabled,
            onClick = onClick
        )
}

/**
 * Rises and fades items in the first time they appear. The first screenful cascades in
 * one after another; items scrolled into view later just pop in without a delay.
 * Remembers (per lazy item key) that it already played, so scrolling back doesn't replay it.
 */
fun Modifier.staggeredEnter(index: Int): Modifier = composed {
    // Previews render a single frame, so start them already entered.
    val inPreview = LocalInspectionMode.current
    var played by rememberSaveable { mutableStateOf(inPreview) }
    val progress = remember { Animatable(if (played) 1f else 0f) }
    LaunchedEffect(Unit) {
        if (!played) {
            if (index < CascadeCount) delay(index * CascadeStepMillis)
            progress.animateTo(1f, spring(dampingRatio = 0.75f, stiffness = 220f))
            played = true
        }
    }
    graphicsLayer {
        val p = progress.value
        alpha = p.coerceIn(0f, 1f)
        translationY = (1f - p) * 48.dp.toPx()
        val scale = 0.94f + 0.06f * p
        scaleX = scale
        scaleY = scale
    }
}

private const val CascadeCount = 12
private const val CascadeStepMillis = 45L

/** Diagonal light sweep used for loading placeholders. Animation runs in the draw phase only. */
fun Modifier.shimmer(): Modifier = composed {
    val base = MaterialTheme.colorScheme.surfaceVariant
    val highlight = MaterialTheme.colorScheme.surface
    val transition = rememberInfiniteTransition(label = "shimmer")
    val sweep by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(1300, easing = LinearEasing)),
        label = "shimmerSweep"
    )
    drawWithCache {
        val width = size.width
        onDrawBehind {
            val x = sweep * width
            drawRect(
                Brush.linearGradient(
                    colors = listOf(base, highlight, base),
                    start = Offset(x - width * 0.6f, 0f),
                    end = Offset(x, size.height)
                )
            )
        }
    }
}
