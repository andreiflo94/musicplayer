package com.example.musicplayer.mainfeature.presentation.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.musicplayer.mainfeature.domain.MusicFolder


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MusicFoldersScreen(
    musicFolders: State<List<MusicFolder>>, onClick: (MusicFolder) -> Unit
) {
    MusicList(musicFolders.value, onClick)
}

@Composable
fun MusicList(musicFolders: List<MusicFolder>, onClick: (MusicFolder) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        contentPadding = PaddingValues(
            bottom = 170.dp,
        ),
    ) {
        items(musicFolders) { folder ->
            MusicFolderItem(folder, onClick)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MusicFolderItem(folder: MusicFolder, onClick: (MusicFolder) -> Unit) {
    Surface(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { onClick(folder) },
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Display the album icon
            GlideImage(
                model = folder.albumIconUrl,
                contentDescription = "album image",
                modifier = Modifier
                    .size(40.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.secondary),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Display the album name
            Text(
                text = folder.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MusicFoldersScreenPreview() {
    val musicFolders = remember { mutableStateOf(getSampleMusicFolders()) }
    MusicFoldersScreen(musicFolders) {

    }
}

@Preview(showBackground = true)
@Composable
fun MusicFolderItemPreview() {
    val folder = getSampleMusicFolder()
    MusicFolderItem(folder) {

    }
}

private fun getSampleMusicFolders(): List<MusicFolder> {
    return listOf(
        MusicFolder("Folder 1", "", "url1"), MusicFolder("Folder 2", "", "url2"), MusicFolder("Folder 3", "", "url3")
    )
}

private fun getSampleMusicFolder(): MusicFolder {
    return MusicFolder("Sample Folder", "", "sampleUrl")
}