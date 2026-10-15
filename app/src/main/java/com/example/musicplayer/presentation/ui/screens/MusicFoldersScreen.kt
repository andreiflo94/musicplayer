package com.example.musicplayer.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.musicplayer.domain.model.MusicFolder
import com.example.musicplayer.presentation.ui.components.AlbumArt
import com.example.musicplayer.presentation.ui.components.EmptyState
import com.example.musicplayer.presentation.ui.components.LocalContentBottomPadding
import com.example.musicplayer.presentation.ui.components.ScreenHeader
import com.example.musicplayer.presentation.ui.components.ThemeButton
import com.example.musicplayer.presentation.ui.components.bounceClick
import com.example.musicplayer.presentation.ui.components.shimmer
import com.example.musicplayer.presentation.ui.components.staggeredEnter
import com.example.musicplayer.presentation.ui.preview.PreviewData
import com.example.musicplayer.presentation.ui.preview.PreviewScreen
import java.util.Calendar

private val CardShape = RoundedCornerShape(26.dp)

/** @param musicFolders null while loading. */
@Composable
fun MusicFoldersScreen(
    musicFolders: List<MusicFolder>?,
    onOpenThemes: () -> Unit,
    onClick: (MusicFolder) -> Unit,
) {
    val greeting = remember { greeting() }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            bottom = LocalContentBottomPadding.current
        ),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(key = "header", span = { GridItemSpan(maxLineSpan) }) {
            ScreenHeader(
                title = "Library",
                eyebrow = greeting,
                subtitle = musicFolders?.let { if (it.size == 1) "1 folder" else "${it.size} folders" },
                actions = { ThemeButton(onOpenThemes) },
                horizontalPadding = 8.dp // the grid already pads by 16dp
            )
        }
        when {
            musicFolders == null -> items(6) { FolderCardPlaceholder() }
            musicFolders.isEmpty() -> item(key = "empty", span = { GridItemSpan(maxLineSpan) }) {
                EmptyState(
                    icon = Icons.Rounded.LibraryMusic,
                    title = "No music yet",
                    message = "Copy some audio files to this device and they'll show up here."
                )
            }

            else -> itemsIndexed(
                musicFolders,
                key = { _, folder -> folder.path }) { index, folder ->
                FolderCard(
                    folder,
                    onClick = { onClick(folder) },
                    modifier = Modifier.staggeredEnter(index)
                )
            }
        }
    }
}

@Composable
private fun FolderCard(folder: MusicFolder, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Column(
        modifier
            .bounceClick(pressedScale = 0.95f, shape = CardShape, onClick = onClick)
            .padding(4.dp)
    ) {
        AlbumArt(
            model = folder.albumIconUrl,
            seed = folder.path,
            shape = CardShape,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .shadow(10.dp, CardShape, ambientColor = scheme.primary, spotColor = scheme.primary)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = folder.name,
            style = MaterialTheme.typography.titleSmall,
            color = scheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        if (folder.artistName.isNotBlank()) {
            Text(
                text = folder.artistName,
                style = MaterialTheme.typography.bodySmall,
                color = scheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
private fun FolderCardPlaceholder() {
    Column(Modifier.padding(4.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(CardShape)
                .shimmer()
        )
        Spacer(Modifier.height(10.dp))
        Box(
            Modifier
                .fillMaxWidth(0.7f)
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .shimmer()
        )
    }
}

private fun greeting(): String = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
    in 5..11 -> "Good morning"
    in 12..17 -> "Good afternoon"
    in 18..22 -> "Good evening"
    else -> "Late night listening"
}

@PreviewLightDark
@Composable
private fun MusicFoldersScreenPreview() {
    PreviewScreen {
        MusicFoldersScreen(musicFolders = PreviewData.folders, onOpenThemes = {}, onClick = {})
    }
}

@Preview(name = "Loading")
@Composable
private fun MusicFoldersScreenLoadingPreview() {
    PreviewScreen {
        MusicFoldersScreen(musicFolders = null, onOpenThemes = {}, onClick = {})
    }
}

@Preview(name = "Empty")
@Composable
private fun MusicFoldersScreenEmptyPreview() {
    PreviewScreen {
        MusicFoldersScreen(musicFolders = emptyList(), onOpenThemes = {}, onClick = {})
    }
}
