package com.example.musicplayer.presentation

import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.QueueMusic
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import com.example.musicplayer.presentation.ui.components.AuroraBackground
import com.example.musicplayer.presentation.ui.components.FloatingNavBar
import com.example.musicplayer.presentation.ui.components.LocalContentBottomPadding
import com.example.musicplayer.presentation.ui.components.NavItem
import com.example.musicplayer.presentation.ui.player.PlayerActions
import com.example.musicplayer.presentation.ui.player.PlayerDimens
import com.example.musicplayer.presentation.ui.player.PlayerSheet
import com.example.musicplayer.presentation.ui.player.rememberPlayerSheetState
import com.example.musicplayer.presentation.ui.screens.BottomNavigationScreen
import com.example.musicplayer.presentation.ui.screens.ThemePickerSheet
import com.example.musicplayer.presentation.ui.theme.Motion
import com.example.musicplayer.presentation.viewmodels.MainActivityViewModel

private val NavBarHeight = 64.dp
private val NavBarMargin = 12.dp
private val PlayerGap = 10.dp

private val Tabs = listOf(
    NavItem("Library", Icons.Outlined.Folder, Icons.Rounded.Folder),
    NavItem(
        "Playlists",
        Icons.AutoMirrored.Outlined.QueueMusic,
        Icons.AutoMirrored.Rounded.QueueMusic
    ),
)

/**
 * Root of the granted-permission UI: aurora backdrop, the library tabs, the morphing player
 * sheet and the floating nav bar, which slides away as the player expands.
 */
@OptIn(UnstableApi::class)
@Composable
fun AppShell() {
    val viewModel: MainActivityViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
    val trackState by viewModel.trackState.collectAsStateWithLifecycle()
    val liveProgress = viewModel.trackLiveProgress.collectAsStateWithLifecycle()
    val elapsed = viewModel.trackProgressFormatted.collectAsStateWithLifecycle()

    val sheetState = rememberPlayerSheetState()
    val isTablet = LocalConfiguration.current.screenWidthDp > 600
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showThemes by rememberSaveable { mutableStateOf(false) }
    val playerVisible = !trackState.stopped

    LaunchedEffect(playerVisible) {
        if (!playerVisible) sheetState.snapCollapsed()
    }

    val systemNavInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val navArea = if (isTablet) 0.dp else NavBarHeight + NavBarMargin
    val playerBottomOffset = systemNavInset + navArea + if (isTablet) NavBarMargin else PlayerGap
    val contentBottomPadding by animateDpAsState(
        targetValue = systemNavInset + navArea + 16.dp +
                if (playerVisible) PlayerDimens.MiniHeight + PlayerGap else 0.dp,
        animationSpec = Motion.fluid(),
        label = "contentBottomPadding"
    )

    val actions = remember(viewModel) {
        PlayerActions(
            onPlayPause = viewModel::playPauseClick,
            onNext = viewModel::skipForward,
            onPrevious = viewModel::playPrevious,
            onSeek = { fraction -> viewModel.onProgressUpdate((fraction * 100).toLong()) }
        )
    }

    CompositionLocalProvider(LocalContentBottomPadding provides contentBottomPadding) {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            AuroraBackground(Modifier.fillMaxSize())

            BottomNavigationScreen(
                isTablet = isTablet,
                selectedTab = selectedTab,
                onOpenThemes = { showThemes = true }
            )

            // Fade the content out under the floating controls so they stay legible.
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(contentBottomPadding)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background.copy(alpha = 0.92f)
                            )
                        )
                    )
            )

            AnimatedVisibility(
                visible = playerVisible,
                enter = slideInVertically(Motion.fluid()) { it / 2 } + fadeIn(),
                exit = slideOutVertically(Motion.calm()) { it / 2 } + fadeOut()
            ) {
                PlayerSheet(
                    state = sheetState,
                    trackState = trackState,
                    progress = { liveProgress.value / 100f },
                    elapsed = { elapsed.value },
                    actions = actions,
                    collapsedBottomOffset = playerBottomOffset
                )
            }

            if (!isTablet) {
                FloatingNavBar(
                    items = Tabs,
                    selectedIndex = selectedTab,
                    onSelect = { selectedTab = it },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(start = 40.dp, end = 40.dp, bottom = NavBarMargin)
                        .widthIn(max = 320.dp)
                        .fillMaxWidth()
                        .graphicsLayer {
                            // Sink below the screen edge as the player takes over.
                            val p = sheetState.progress
                            translationY = p * size.height * 2.5f
                            alpha = 1f - p
                        }
                )
            }
        }
    }

    if (showThemes) {
        ThemePickerSheet(onDismiss = { showThemes = false })
    }
}
