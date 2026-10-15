package com.example.musicplayer.presentation.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.musicplayer.presentation.ui.preview.PreviewSurface
import com.example.musicplayer.presentation.ui.theme.Motion
import kotlin.math.PI
import kotlin.math.sin

/**
 * Seek bar whose played part is a living sine wave that ripples while music plays and
 * relaxes into a straight line when paused or while you scrub.
 *
 * [progress] is a lambda read only while drawing, so position updates redraw the canvas
 * without recomposing anything. [onScrub] reports the finger position (0..1) during a drag
 * and null when it ends; [onSeek] fires once with the final position.
 */
@Composable
fun WaveSeekBar(
    progress: () -> Float,
    isPlaying: Boolean,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier,
    onScrub: (Float?) -> Unit = {},
    activeColor: Color,
    inactiveColor: Color,
) {
    var scrubbing by remember { mutableStateOf(false) }
    var scrubValue by remember { mutableFloatStateOf(0f) }
    var phase by remember { mutableFloatStateOf(0f) }
    val currentOnSeek by rememberUpdatedState(onSeek)
    val currentOnScrub by rememberUpdatedState(onScrub)

    val amplitude by animateFloatAsState(
        targetValue = if (isPlaying && !scrubbing) 1f else 0f,
        animationSpec = Motion.fluid(),
        label = "waveAmplitude"
    )
    val thumbScale by animateFloatAsState(
        targetValue = if (scrubbing) 1.6f else 1f,
        animationSpec = Motion.bouncy(),
        label = "thumbScale"
    )

    // Advance the wave phase from the frame clock, so pausing freezes it smoothly in place.
    LaunchedEffect(isPlaying) {
        if (!isPlaying) return@LaunchedEffect
        var last = withFrameNanos { it }
        while (true) {
            withFrameNanos { now ->
                phase += (now - last) / 1_000_000_000f * WaveSpeed
                last = now
            }
        }
    }

    val wave = remember { Path() }

    Canvas(
        modifier
            .fillMaxWidth()
            .height(36.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    currentOnSeek((offset.x / size.width).coerceIn(0f, 1f))
                }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        scrubbing = true
                        scrubValue = (offset.x / size.width).coerceIn(0f, 1f)
                        currentOnScrub(scrubValue)
                    },
                    onDragEnd = {
                        scrubbing = false
                        currentOnSeek(scrubValue)
                        currentOnScrub(null)
                    },
                    onDragCancel = {
                        scrubbing = false
                        currentOnScrub(null)
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        scrubValue = (scrubValue + dragAmount / size.width).coerceIn(0f, 1f)
                        currentOnScrub(scrubValue)
                    }
                )
            }
    ) {
        val fraction = (if (scrubbing) scrubValue else progress()).coerceIn(0f, 1f)
        val centerY = size.height / 2f
        val strokeWidth = 4.dp.toPx()
        val thumbX = size.width * fraction
        val waveLength = 30.dp.toPx()
        val maxAmplitude = 4.dp.toPx() * amplitude

        drawLine(
            color = inactiveColor,
            start = Offset(thumbX, centerY),
            end = Offset(size.width, centerY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        wave.reset()
        wave.moveTo(0f, centerY)
        var x = 0f
        val step = 2.dp.toPx()
        while (x < thumbX) {
            // Taper the wave near both ends so it blends into the thumb.
            val taper =
                (x / waveLength).coerceAtMost(1f) * ((thumbX - x) / waveLength).coerceIn(0f, 1f)
            val y = centerY + sin(x / waveLength * 2f * PI.toFloat() - phase) * maxAmplitude * taper
            wave.lineTo(x, y)
            x += step
        }
        wave.lineTo(thumbX, centerY)
        drawPath(wave, activeColor, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))

        // Pill-shaped thumb that stretches while scrubbing.
        val thumbHeight = 18.dp.toPx() * thumbScale
        drawLine(
            color = activeColor,
            start = Offset(thumbX, centerY - thumbHeight / 2f),
            end = Offset(thumbX, centerY + thumbHeight / 2f),
            strokeWidth = 6.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

private const val WaveSpeed = 6f // radians per second

@PreviewLightDark
@Composable
private fun WaveSeekBarPreview() {
    PreviewSurface {
        Column(Modifier
            .width(320.dp)
            .padding(16.dp)) {
            listOf(true, false).forEach { playing ->
                WaveSeekBar(
                    progress = { 0.35f },
                    isPlaying = playing,
                    onSeek = {},
                    activeColor = MaterialTheme.colorScheme.primary,
                    inactiveColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
