package com.example.musicplayer.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.musicplayer.presentation.ui.preview.PreviewSurface

/** Big editorial title used at the top of each tab's root screen. */
@Composable
fun ScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    subtitle: String? = null,
    horizontalPadding: Dp = 24.dp,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(
                start = horizontalPadding,
                end = horizontalPadding - 8.dp,
                top = 20.dp,
                bottom = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            if (eyebrow != null) {
                Text(
                    text = eyebrow.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(2.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), content = actions)
    }
}

/** Round tonal icon button with a springy press. */
@Composable
fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Int = 44,
) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
            .size(size.dp)
            .bounceClick(pressedScale = 0.86f, shape = CircleShape, onClick = onClick)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.85f))
            .padding((size / 4).dp)
    )
}

@Composable
fun ThemeButton(onClick: () -> Unit) =
    CircleIconButton(Icons.Rounded.Palette, contentDescription = "Themes", onClick = onClick)

/**
 * Overlay bar for detail screens: the back button is always there, while the background and
 * title fade in as [revealed] (0..1) grows. [revealed] is read only in the draw phase.
 */
@Composable
fun CollapsingTopBar(
    title: String,
    revealed: () -> Float,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = MaterialTheme.colorScheme.background
    Column(
        modifier.drawBehind { drawRect(background.copy(alpha = 0.94f * revealed())) }
    ) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleIconButton(Icons.AutoMirrored.Rounded.ArrowBack, "Back", onBack)
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .graphicsLayer {
                        val r = revealed()
                        alpha = r
                        translationY = (1f - r) * 12.dp.toPx()
                    }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ScreenHeaderPreview() {
    PreviewSurface {
        ScreenHeader(
            title = "Library",
            eyebrow = "Good evening",
            subtitle = "5 folders",
            actions = { ThemeButton(onClick = {}) }
        )
    }
}

@PreviewLightDark
@Composable
private fun CollapsingTopBarPreview() {
    PreviewSurface {
        CollapsingTopBar(title = "72 Seasons", revealed = { 1f }, onBack = {})
    }
}
