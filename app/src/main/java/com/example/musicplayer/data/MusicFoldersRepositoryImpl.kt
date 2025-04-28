package com.example.musicplayer.data

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import com.example.musicplayer.domain.model.MusicFolder
import com.example.musicplayer.domain.repo.MusicFoldersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class MusicFoldersRepositoryImpl @Inject constructor(private val contentResolver: ContentResolver) :
    MusicFoldersRepository {
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

    override fun getAlbumIconUrl(albumPath: String): String {
        // Check for common album cover image files in the folder
        val possibleCoverFiles = listOf("cover.jpg", "folder.jpg", "album.jpg", "thumb.jpg")
        val albumCoverFile = possibleCoverFiles.map { File(albumPath, it) }
            .firstOrNull { it.exists() }

        if (albumCoverFile != null) {
            return albumCoverFile.absolutePath
        }

        // Try to get album art using MediaStore
        val projection = arrayOf(MediaStore.Audio.Media.ALBUM_ID)
        val selection = "${MediaStore.Audio.Media.DATA} LIKE ?"
        val selectionArgs = arrayOf("$albumPath%") // Matches files in the folder

        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val albumIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
                if (albumIdColumn != -1) {
                    val albumId = cursor.getLong(albumIdColumn)

                    // Query album art using the retrieved album ID
                    val albumArtUri = Uri.parse("content://media/external/audio/albumart/$albumId")
                    return albumArtUri.toString()
                }
            }
        }

        // Default fallback if no album art found
        return ""
    }


}