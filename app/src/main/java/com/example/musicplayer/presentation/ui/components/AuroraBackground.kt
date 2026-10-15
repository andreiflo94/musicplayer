package com.example.musicplayer.presentation.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.musicplayer.presentation.ui.preview.PreviewScreen
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Soft glowing blobs of the theme's accent colors drifting slowly behind the content, like
 * northern lights. One looping clock drives all of them, and it is read only while drawing,
 * so the content on top never recomposes or redraws because of it.
 */
@Composable
fun AuroraBackground(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val strength = if (scheme.background.luminance() < 0.5f) 0.22f else 0.14f
    val clock by rememberInfiniteTransition(label = "aurora").animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(26_000, easing = LinearEasing)),
        label = "auroraClock"
    )

    // Own layer: redrawing the blobs every frame never invalidates anything else.
    Canvas(modifier.graphicsLayer()) {
        val t = clock
        blob(
            scheme.primary.copy(alpha = strength),
            0.15f + 0.12f * cos(t),
            0.08f + 0.06f * sin(t),
            0.75f
        )
        blob(
            scheme.tertiary.copy(alpha = strength * 0.8f),
            0.9f + 0.1f * sin(t + 1.3f),
            0.3f + 0.1f * cos(t * 2),
            0.6f
        )
        blob(
            scheme.secondary.copy(alpha = strength * 0.6f),
            0.4f + 0.2f * sin(t + 3f),
            0.85f + 0.06f * cos(t),
            0.7f
        )
    }
}

private fun DrawScope.blob(color: Color, x: Float, y: Float, radiusFraction: Float) {
    val center = Offset(size.width * x, size.height * y)
    val radius = size.maxDimension * radiusFraction
    drawCircle(
        brush = Brush.radialGradient(listOf(color, Color.Transparent), center, radius),
        radius = radius,
        center = center
    )
}

@PreviewLightDark
@Composable
private fun AuroraBackgroundPreview() {
    PreviewScreen {}
}
