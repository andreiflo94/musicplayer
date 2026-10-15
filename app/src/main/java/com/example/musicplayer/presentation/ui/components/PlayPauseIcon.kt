package com.example.musicplayer.presentation.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.example.musicplayer.presentation.ui.preview.PreviewSurface

/**
 * A play triangle that melts into two pause bars (and back) instead of swapping icons.
 * The triangle is split down the middle into two quads; each quad morphs into one bar.
 * Only the canvas redraws while morphing — nothing recomposes.
 */
@Composable
fun PlayPauseIcon(
    isPlaying: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val morph by animateFloatAsState(
        targetValue = if (isPlaying) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 420f),
        label = "playPauseMorph"
    )
    val left = remember { Path() }
    val right = remember { Path() }

    Canvas(modifier.semantics { contentDescription = if (isPlaying) "Pause" else "Play" }) {
        val t = morph
        // A twist that peaks halfway through the morph (30°) and settles back to upright.
        rotate(degrees = 120f * t * (1f - t)) {
            left.morphQuad(PlayLeft, PauseLeft, t, this)
            right.morphQuad(PlayRight, PauseRight, t, this)
            drawPath(left, color, style = Fill)
            drawPath(right, color, style = Fill)
            // A hairline stroke with round joins softens the corners.
            val stroke = Stroke(width = size.minDimension * 0.06f, join = StrokeJoin.Round)
            drawPath(left, color, style = stroke)
            drawPath(right, color, style = stroke)
        }
    }
}

// Points are in a unit box, ordered top-left, top-right, bottom-right, bottom-left.
private val PlayLeft = floatArrayOf(0.26f, 0.16f, 0.56f, 0.33f, 0.56f, 0.67f, 0.26f, 0.84f)
private val PlayRight = floatArrayOf(0.56f, 0.33f, 0.86f, 0.5f, 0.86f, 0.5f, 0.56f, 0.67f)
private val PauseLeft = floatArrayOf(0.22f, 0.18f, 0.42f, 0.18f, 0.42f, 0.82f, 0.22f, 0.82f)
private val PauseRight = floatArrayOf(0.58f, 0.18f, 0.78f, 0.18f, 0.78f, 0.82f, 0.58f, 0.82f)

private fun Path.morphQuad(from: FloatArray, to: FloatArray, t: Float, scope: DrawScope) {
    val w = scope.size.width
    val h = scope.size.height
    reset()
    for (i in 0 until 4) {
        val x = lerp(from[i * 2], to[i * 2], t) * w
        val y = lerp(from[i * 2 + 1], to[i * 2 + 1], t) * h
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

@Preview(name = "Playing / paused")
@Composable
private fun PlayPauseIconPreview() {
    PreviewSurface {
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            PlayPauseIcon(
                isPlaying = true,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(32.dp)
            )
            PlayPauseIcon(
                isPlaying = false,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
