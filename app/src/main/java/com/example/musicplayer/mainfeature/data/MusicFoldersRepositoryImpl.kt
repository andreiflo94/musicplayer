package com.example.musicplayer.mainfeature.data

import android.content.ContentResolver
import com.example.musicplayer.mainfeature.domain.MusicFolder
import com.example.musicplayer.mainfeature.domain.MusicFoldersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MusicFoldersRepositoryImpl @Inject constructor(private val contentResolver: ContentResolver) : MusicFoldersRepository {
    override suspend fun getMusicFolders() = withContext(Dispatchers.IO) {
        val musicFolders = MusicUtils.getMusicFolders(contentResolver)
        val processedList = ArrayList<MusicFolder>()
        musicFolders.map { folder ->
            processedList.add(MusicFolder(folder.name, folder.path, getAlbumIconUrl(folder.path)))
        }
        return@withContext processedList
    }

    override suspend fun getMusicFilesFromPath(path: String) = withContext(Dispatchers.IO) {
        val musicFolders = MusicUtils.getMusicFilesFromPath(contentResolver, path)
        val processedList = ArrayList<MusicFolder>()
        musicFolders.map { folder ->
            processedList.add(MusicFolder(folder.name, folder.path, getAlbumIconUrl(folder.path)))
        }
        return@withContext processedList
    }

    private fun getAlbumIconUrl(albumPath: String): String {
        // You can return a valid URL or file path to the album cover
        // For simplicity, let's assume the albumPath itself is the URL
        return albumPath
    }
}