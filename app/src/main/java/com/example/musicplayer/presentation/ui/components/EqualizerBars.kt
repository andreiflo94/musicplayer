package com.example.musicplayer.presentation.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicplayer.presentation.ui.preview.PreviewSurface
import com.example.musicplayer.presentation.ui.theme.Motion

/**
 * Bouncing "now playing" bars. When paused they don't freeze mid-air: they ease down
 * into a calm resting row of dots.
 */
@Composable
fun EqualizerBars(
    isPlaying: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    barCount: Int = 4,
) {
    val transition = rememberInfiniteTransition(label = "equalizer")
    val bars: List<State<Float>> = List(barCount) { i ->
        transition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 380 + i * 130 % 400,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar$i"
        )
    }
    val energy by animateFloatAsState(
        targetValue = if (isPlaying) 1f else 0f,
        animationSpec = Motion.fluid(),
        label = "equalizerEnergy"
    )

    Canvas(modifier) {
        val gap = size.width / (barCount * 3f - 1f)
        val barWidth = gap * 2f
        val minHeight = barWidth
        bars.forEachIndexed { i, bar ->
            val height = minHeight + (size.height - minHeight) * bar.value * energy
            drawRoundRect(
                color = color,
                topLeft = Offset(i * (barWidth + gap), size.height - height),
                size = Size(barWidth, height),
                cornerRadius = CornerRadius(barWidth / 2f)
            )
        }
    }
}

@Preview(name = "Playing / paused")
@Composable
private fun EqualizerBarsPreview() {
    PreviewSurface {
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            EqualizerBars(
                isPlaying = true,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            EqualizerBars(
                isPlaying = false,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
