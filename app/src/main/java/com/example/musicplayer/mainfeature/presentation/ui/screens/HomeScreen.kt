package com.example.musicplayer.mainfeature.presentation.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.example.musicplayer.R
import com.example.musicplayer.mainfeature.presentation.viewmodels.AudioState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    audioState: State<AudioState>,
    content: @Composable (PaddingValues) -> Unit,
    bottomPlayer: @Composable ColumnScope.() -> Unit,
) {
    val scaffoldState = rememberBottomSheetScaffoldState()

    val animatedHeight by animateDpAsState(
        targetValue = if (audioState.value.stopped) 0.dp
        else dimensionResource(id = R.dimen.app_player_bar_height), label = ""
    )

    BottomSheetScaffold(
        sheetContent = bottomPlayer,
        scaffoldState = scaffoldState,
        sheetPeekHeight = animatedHeight,
        content = content
    )
}