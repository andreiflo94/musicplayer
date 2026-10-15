package com.example.musicplayer.presentation.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrightnessAuto
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicplayer.domain.model.AppTheme
import com.example.musicplayer.domain.model.DarkMode
import com.example.musicplayer.domain.model.ThemeSettings
import com.example.musicplayer.presentation.ui.components.NavItem
import com.example.musicplayer.presentation.ui.components.SegmentedSelector
import com.example.musicplayer.presentation.ui.components.bounceClick
import com.example.musicplayer.presentation.ui.components.staggeredEnter
import com.example.musicplayer.presentation.ui.preview.PreviewSurface
import com.example.musicplayer.presentation.ui.theme.Motion
import com.example.musicplayer.presentation.ui.theme.colorScheme
import com.example.musicplayer.presentation.ui.theme.displayName
import com.example.musicplayer.presentation.ui.theme.isDark
import com.example.musicplayer.presentation.ui.theme.selectableThemes
import com.example.musicplayer.presentation.viewmodels.ThemeViewModel

private val DarkModeOptions = listOf(
    NavItem("Auto", Icons.Rounded.BrightnessAuto),
    NavItem("Light", Icons.Rounded.LightMode),
    NavItem("Dark", Icons.Rounded.DarkMode),
)

private val CardShape = RoundedCornerShape(24.dp)

/**
 * Theme picker. Choices apply instantly, and because the app's color scheme is animated,
 * the whole UI (this sheet included) melts into the new palette while you watch.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemePickerSheet(onDismiss: () -> Unit) {
    val viewModel: ThemeViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        ThemePickerContent(
            settings = settings,
            onDarkModeChange = viewModel::setDarkMode,
            onThemeChange = viewModel::setTheme
        )
    }
}

@Composable
private fun ThemePickerContent(
    settings: ThemeSettings,
    onDarkModeChange: (DarkMode) -> Unit,
    onThemeChange: (AppTheme) -> Unit,
) {
    val dark = settings.isDark()
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
    ) {
        Text(
            "Appearance",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            "Pick a mood — the colors blend live.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(20.dp))
        SegmentedSelector(
            options = DarkModeOptions,
            selectedIndex = settings.darkMode.ordinal,
            onSelect = { onDarkModeChange(DarkMode.values()[it]) }
        )
        Spacer(Modifier.height(24.dp))
        Text(
            "THEME",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(12.dp))

        selectableThemes.chunked(2).forEachIndexed { rowIndex, row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEachIndexed { i, theme ->
                    ThemeCard(
                        theme = theme,
                        selected = theme == settings.theme,
                        dark = dark,
                        onClick = { onThemeChange(theme) },
                        modifier = Modifier
                            .weight(1f)
                            .staggeredEnter(rowIndex * 2 + i)
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

/** A miniature of the theme: its background, color dots and a gradient "progress bar". */
@Composable
private fun ThemeCard(
    theme: AppTheme,
    selected: Boolean,
    dark: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val preview: ColorScheme = remember(theme, dark) { theme.colorScheme(dark, context) }
    val borderWidth by animateDpAsState(
        if (selected) 3.dp else 1.dp,
        Motion.bouncy(),
        label = "themeBorder"
    )
    val borderColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        label = "themeBorderColor"
    )

    Box(
        modifier
            .bounceClick(pressedScale = 0.95f, shape = CardShape, onClick = onClick)
            .background(preview.background)
            .border(borderWidth, borderColor, CardShape)
            .padding(16.dp)
    ) {
        Column {
            Row {
                listOf(
                    preview.primary,
                    preview.secondary,
                    preview.tertiary
                ).forEachIndexed { i, color ->
                    Box(
                        Modifier
                            .offset(x = (-10 * i).dp)
                            .size(30.dp)
                            .border(2.dp, preview.background, CircleShape)
                            .background(color, CircleShape)
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(preview.surfaceVariant, CircleShape)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(0.62f)
                        .height(6.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    preview.primary,
                                    preview.tertiary
                                )
                            ), CircleShape
                        )
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                theme.displayName,
                style = MaterialTheme.typography.titleSmall,
                color = preview.onBackground
            )
        }

        AnimatedVisibility(
            visible = selected,
            enter = fadeIn() + scaleIn(Motion.bouncy(), initialScale = 0.3f),
            exit = fadeOut() + scaleOut(targetScale = 0.3f),
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = "Selected",
                tint = preview.onPrimary,
                modifier = Modifier
                    .size(26.dp)
                    .background(preview.primary, CircleShape)
                    .padding(4.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ThemePickerContentPreview() {
    PreviewSurface {
        ThemePickerContent(
            settings = ThemeSettings(theme = AppTheme.AURORA, darkMode = DarkMode.SYSTEM),
            onDarkModeChange = {},
            onThemeChange = {}
        )
    }
}
