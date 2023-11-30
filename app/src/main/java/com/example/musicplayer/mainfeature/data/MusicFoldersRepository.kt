package com.example.musicplayer.mainfeature.data

import android.content.ContentResolver
import com.example.musicplayer.mainfeature.domain.IMusicFoldersRepository
import com.example.musicplayer.mainfeature.domain.MusicFolder
import javax.inject.Inject

class MusicFoldersRepository @Inject constructor(private val contentResolver: ContentResolver): IMusicFoldersRepository {
    override fun getMusicFolders(): List<MusicFolder> {
        val musicFolders = MusicUtils.getMusicFolders(contentResolver)
        val processedList = ArrayList<MusicFolder>()
        musicFolders.map { folder ->
            processedList.add(MusicFolder(folder.name, folder.path, getAlbumIconUrl(folder.path)))
        }
        return processedList
    }

    override fun getMusicFilesFromPath(path: String): List<MusicFolder> {
        val musicFolders = MusicUtils.getMusicFilesFromPath(contentResolver, path)
        val processedList = ArrayList<MusicFolder>()
        musicFolders.map { folder ->
            processedList.add(MusicFolder(folder.name, folder.path, getAlbumIconUrl(folder.path)))
        }
        return processedList
    }

    private fun getAlbumIconUrl(albumPath: String): String {
        // You can return a valid URL or file path to the album cover
        // For simplicity, let's assume the albumPath itself is the URL
        return albumPath
    }
}