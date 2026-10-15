package com.example.musicplayer.presentation.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FolderOff
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.musicplayer.presentation.ui.preview.PreviewSurface

private val PillShape = CircleShape

/** Primary call to action: a glossy primary→tertiary gradient pill. */
@Composable
fun GradientPillButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val brush = Brush.horizontalGradient(listOf(scheme.primary, scheme.tertiary))
    PillContent(
        text = text,
        icon = icon,
        contentColor = scheme.onPrimary,
        modifier = modifier
            .bounceClick(shape = PillShape, onClick = onClick)
            .background(brush)
    )
}

/** Secondary action: a soft tonal pill. */
@Composable
fun TonalPillButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PillContent(
        text = text,
        icon = icon,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = modifier
            .bounceClick(shape = PillShape, onClick = onClick)
            .background(MaterialTheme.colorScheme.secondaryContainer)
    )
}

@Composable
private fun PillContent(text: String, icon: ImageVector, contentColor: Color, modifier: Modifier) {
    Row(
        modifier = modifier.padding(horizontal = 22.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
        Text(text, style = MaterialTheme.typography.labelLarge, color = contentColor)
    }
}

/** Friendly placeholder for empty lists; the icon gently floats and glows. */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "emptyFloat")
    val float by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(2200, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "emptyFloatValue"
    )
    val scheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(112.dp)
                .graphicsLayer { translationY = (float - 0.5f) * 16.dp.toPx() }
                .drawBehind {
                    drawCircle(
                        Brush.radialGradient(
                            listOf(
                                scheme.primary.copy(alpha = 0.18f + 0.1f * float),
                                Color.Transparent
                            )
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .size(72.dp)
                    .background(
                        Brush.linearGradient(listOf(scheme.primary, scheme.tertiary)),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = scheme.onPrimary,
                    modifier = Modifier.size(34.dp)
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            color = scheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = scheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@PreviewLightDark
@Composable
private fun PillButtonsPreview() {
    PreviewSurface {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            GradientPillButton("Play", Icons.Rounded.PlayArrow, onClick = {})
            TonalPillButton("Shuffle", Icons.Rounded.Shuffle, onClick = {})
        }
    }
}

@PreviewLightDark
@Composable
private fun EmptyStatePreview() {
    PreviewSurface {
        EmptyState(
            icon = Icons.Rounded.FolderOff,
            title = "Nothing here",
            message = "This folder has no playable tracks."
        )
    }
}
