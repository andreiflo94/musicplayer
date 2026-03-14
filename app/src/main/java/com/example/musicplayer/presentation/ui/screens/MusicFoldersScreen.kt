package com.example.musicplayer.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.musicplayer.domain.model.MusicFolder

@Composable
fun MusicFoldersScreen(
    title: String,
    musicFolders: List<MusicFolder>,
    onClick: (MusicFolder) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ScreenTitle(title)
        MusicList(musicFolders, onClick)
    }
}

@Composable
private fun ScreenTitle(title: String) {

    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 16.dp),
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun MusicList(
    musicFolders: List<MusicFolder>,
    onClick: (MusicFolder) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 8.dp,
            bottom = 100.dp
        )
    ) {
        items(
            items = musicFolders,
            key = { it.name } // stable key to prevent recomposition
        ) { folder ->
            MusicFolderItem(folder, onClick)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun MusicFolderItem(
    folder: MusicFolder,
    onClick: (MusicFolder) -> Unit
) {

    Surface(
        onClick = { onClick(folder) },
        shape = MaterialTheme.shapes.large,
        tonalElevation = 3.dp,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AlbumImage(folder.albumIconUrl)

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = folder.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun AlbumImage(albumIconUrl: String?) {

    GlideImage(
        model = albumIconUrl,
        contentDescription = "album image",
        modifier = Modifier
            .size(56.dp)
            .clip(MaterialTheme.shapes.medium)
    )
}
