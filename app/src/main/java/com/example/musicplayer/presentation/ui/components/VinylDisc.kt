package com.example.musicplayer.presentation.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.musicplayer.presentation.ui.preview.PreviewSurface

/**
 * A vinyl record: grooves, a light sheen and a colored center label.
 * Brushes are cached and [rotation] is read only while drawing, so spinning it only redraws.
 */
@Composable
fun VinylDisc(
    labelColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier,
    rotation: () -> Float = { 0f },
) {
    Spacer(
        modifier.drawWithCache {
            val radius = size.minDimension / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            val grooveStroke = Stroke(width = radius * 0.006f)
            val sheen = Brush.sweepGradient(
                0f to Color.Transparent,
                0.12f to Color.White.copy(alpha = 0.16f),
                0.25f to Color.Transparent,
                0.62f to Color.Transparent,
                0.75f to Color.White.copy(alpha = 0.12f),
                0.88f to Color.Transparent,
                center = center
            )
            val label = Brush.radialGradient(
                colors = listOf(labelColor, accentColor),
                center = center,
                radius = radius * 0.34f
            )
            onDrawBehind {
                drawCircle(Color(0xFF111114), radius, center)
                var r = radius * 0.94f
                while (r > radius * 0.4f) {
                    drawCircle(Color.White.copy(alpha = 0.05f), r, center, style = grooveStroke)
                    r -= radius * 0.035f
                }
                drawCircle(sheen, radius, center)
                // The sheen stays put while the label (and its dot) turn underneath it.
                rotate(rotation(), center) {
                    drawCircle(label, radius * 0.33f, center)
                    drawCircle(
                        Color.White.copy(alpha = 0.55f),
                        radius * 0.035f,
                        center + Offset(radius * 0.2f, 0f)
                    )
                }
                drawCircle(Color(0xFF111114), radius * 0.045f, center)
            }
        }
    )
}

@PreviewLightDark
@Composable
private fun VinylDiscPreview() {
    PreviewSurface {
        VinylDisc(
            labelColor = MaterialTheme.colorScheme.primary,
            accentColor = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .padding(16.dp)
                .size(200.dp)
        )
    }
}
