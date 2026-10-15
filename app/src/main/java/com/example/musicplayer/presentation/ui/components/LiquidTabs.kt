package com.example.musicplayer.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.QueueMusic
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.rounded.BrightnessAuto
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.musicplayer.presentation.ui.preview.PreviewSurface
import com.example.musicplayer.presentation.ui.theme.Motion
import kotlinx.coroutines.launch

/**
 * Draws a pill behind the selected one of [count] equal-width slots. Its leading edge races
 * ahead and its trailing edge lags behind, so the pill stretches like a drop of liquid while
 * it travels, then snaps back into shape. Drawn in the draw phase only.
 */
fun Modifier.liquidIndicator(
    selectedIndex: Int,
    count: Int,
    color: Color,
    inset: Float = 0f
): Modifier = composed {
    val fast = remember { Animatable(selectedIndex.toFloat()) }
    val slow = remember { Animatable(selectedIndex.toFloat()) }
    LaunchedEffect(selectedIndex) {
        launch {
            fast.animateTo(
                selectedIndex.toFloat(),
                spring(dampingRatio = 0.7f, stiffness = 900f)
            )
        }
        launch {
            slow.animateTo(
                selectedIndex.toFloat(),
                spring(dampingRatio = 0.75f, stiffness = 220f)
            )
        }
    }
    drawBehind {
        val slot = size.width / count
        val left = minOf(fast.value, slow.value) * slot + inset
        val right = (maxOf(fast.value, slow.value) + 1f) * slot - inset
        val height = size.height - inset * 2
        drawRoundRect(
            color = color,
            topLeft = Offset(left, inset),
            size = Size(right - left, height),
            cornerRadius = CornerRadius(height / 2f)
        )
    }
}

data class NavItem(val label: String, val icon: ImageVector, val selectedIcon: ImageVector = icon)

/** Floating pill navigation bar; the selected tab grows a label and the liquid pill follows. */
@Composable
fun FloatingNavBar(
    items: List<NavItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .shadow(18.dp, CircleShape, ambientColor = scheme.primary, spotColor = scheme.primary)
            .background(scheme.surfaceContainerHigh, CircleShape)
            .height(64.dp)
            .padding(6.dp)
            .liquidIndicator(selectedIndex, items.size, scheme.primary),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            NavBarItem(
                item = item,
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun NavBarItem(item: NavItem, selected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val scheme = MaterialTheme.colorScheme
    val contentColor by animateColorAsState(
        targetValue = if (selected) scheme.onPrimary else scheme.onSurfaceVariant,
        label = "navItemColor"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.12f else 1f,
        animationSpec = Motion.bouncy(),
        label = "navIconScale"
    )
    Row(
        modifier = modifier
            .fillMaxHeight()
            .bounceClick(pressedScale = 0.9f, shape = CircleShape, onClick = onClick)
            .semantics {
                this.selected = selected
                role = Role.Tab
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Icon(
            imageVector = if (selected) item.selectedIcon else item.icon,
            contentDescription = item.label,
            tint = contentColor,
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                }
        )
        AnimatedVisibility(
            visible = selected,
            enter = fadeIn() + expandHorizontally(Motion.fluid()),
            exit = fadeOut() + shrinkHorizontally(Motion.fluid())
        ) {
            Text(
                text = item.label,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                maxLines = 1,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

/** Compact segmented control with the same liquid pill (used for light/dark mode). */
@Composable
fun SegmentedSelector(
    options: List<NavItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(scheme.surfaceContainerHigh, CircleShape)
            .height(52.dp)
            .padding(4.dp)
            .liquidIndicator(selectedIndex, options.size, scheme.primary),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            val color by animateColorAsState(
                if (selected) scheme.onPrimary else scheme.onSurfaceVariant,
                label = "segmentColor"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .bounceClick(pressedScale = 0.92f, shape = CircleShape) { onSelect(index) }
                    .semantics {
                        this.selected = selected
                        role = Role.RadioButton
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        option.icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = option.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = color,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun NavControlsPreview() {
    PreviewSurface {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            FloatingNavBar(
                items = listOf(
                    NavItem("Library", Icons.Outlined.Folder, Icons.Rounded.Folder),
                    NavItem(
                        "Playlists",
                        Icons.AutoMirrored.Outlined.QueueMusic,
                        Icons.AutoMirrored.Rounded.QueueMusic
                    ),
                ),
                selectedIndex = 0,
                onSelect = {}
            )
            SegmentedSelector(
                options = listOf(
                    NavItem("Auto", Icons.Rounded.BrightnessAuto),
                    NavItem("Light", Icons.Rounded.LightMode),
                    NavItem("Dark", Icons.Rounded.DarkMode),
                ),
                selectedIndex = 2,
                onSelect = {}
            )
        }
    }
}
