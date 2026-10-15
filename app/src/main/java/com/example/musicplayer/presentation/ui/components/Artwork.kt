package com.example.musicplayer.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.musicplayer.presentation.ui.preview.PreviewSurface
import kotlin.math.absoluteValue

/**
 * Album artwork that never shows an empty hole: a themed gradient (picked from [seed], so the
 * same album always gets the same colors) sits underneath and shows through whenever the image
 * is loading or missing.
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AlbumArt(
    model: Any?,
    seed: String,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
) {
    Box(modifier.clip(shape)) {
        GradientCover(seed = seed, modifier = Modifier.fillMaxSize())
        if (model != null && model != "") {
            GlideImage(
                model = model,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Generated cover: a diagonal gradient of two theme colors chosen from [seed], with either the
 * first letter of [label] or a music note on top.
 */
@Composable
fun GradientCover(
    seed: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    labelSize: TextUnit = 28.sp,
    shape: Shape = RectangleShape,
) {
    val scheme = MaterialTheme.colorScheme
    val swatches = listOf(scheme.primary, scheme.tertiary, scheme.secondary)
    val hash = remember(seed) { seed.hashCode().absoluteValue }
    val start = swatches[hash % swatches.size]
    val end = swatches[(hash / swatches.size + 1) % swatches.size].let {
        if (it == start) swatches[(hash + 1) % swatches.size] else it
    }
    val onCover =
        if (start.luminance() > 0.55f) Color.Black.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.92f)

    Box(
        modifier = modifier
            .clip(shape)
            .drawWithCache {
                val brush = Brush.linearGradient(
                    colors = listOf(start, end),
                    start = Offset.Zero,
                    end = Offset(size.width, size.height)
                )
                val sheen = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.28f), Color.Transparent),
                    center = Offset(size.width * 0.2f, size.height * 0.15f),
                    radius = size.maxDimension * 0.9f
                )
                onDrawBehind {
                    drawRect(brush)
                    drawRect(sheen)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        val initial = label?.trim()?.firstOrNull()?.uppercaseChar()
        if (initial != null) {
            Text(
                text = initial.toString(),
                color = onCover,
                fontSize = labelSize,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.MusicNote,
                contentDescription = null,
                tint = onCover,
                modifier = Modifier.fillMaxSize(0.42f)
            )
        }
    }
}

/** Without a loadable image, art falls back to a gradient generated from the seed. */
@Preview
@Composable
private fun GradientCoverPreview() {
    PreviewSurface {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            listOf("Workout", "Late night drive", "Focus").forEach { name ->
                GradientCover(
                    seed = name,
                    label = name,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.size(88.dp)
                )
            }
            AlbumArt(
                model = null,
                seed = "72 Seasons",
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.size(88.dp)
            )
        }
    }
}
