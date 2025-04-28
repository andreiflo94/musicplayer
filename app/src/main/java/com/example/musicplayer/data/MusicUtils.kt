package com.example.musicplayer.data
import android.content.ContentResolver
import android.provider.MediaStore
import android.util.Log
import java.io.File

object MusicUtils {

    private const val TAG = "MusicUtils"

    fun getMusicFolders(contentResolver: ContentResolver): List<File> {
        val musicFolders = mutableListOf<File>()

        // Projection for the query
        val projection = arrayOf(
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION
        )

        // Selection criteria: only retrieve music files
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        // Query the media store
        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )

        cursor?.use { c ->
            val pathColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (c.moveToNext()) {
                val filePath = c.getString(pathColumn)
                val file = File(filePath).parentFile

                // Check if the folder is not already added
                if (file != null && file.isDirectory && !musicFolders.contains(file)) {
                    Log.d(TAG, file.name)
                    musicFolders.add(file)
                }
            }
        }

        // Close the cursor to avoid memory leaks
        cursor?.close()

        return musicFolders
    }

    fun getMusicFilesFromPath(contentResolver: ContentResolver, folderPath: String): List<File> {
        val musicFiles = mutableListOf<File>()

        // Check if the folder path is valid
        val folder = File(folderPath)
        if (!folder.exists() || !folder.isDirectory) {
            return musicFiles
        }

        // Projection for the query
        val projection = arrayOf(
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION
        )

        // Selection criteria: only retrieve music files in the specified folder
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DATA} LIKE ?"

        // Selection arguments: path of the specified folder
        val selectionArgs = arrayOf("${folderPath}%")

        // Query the media store
        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        cursor?.use { c ->
            val pathColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (c.moveToNext()) {
                val filePath = c.getString(pathColumn)
                val file = File(filePath)

                // Check if the file is not already added
                if (file.exists() && file.isFile && !musicFiles.contains(file)) {
                    Log.d(TAG, file.name)
                    musicFiles.add(file)
                }
            }
        }

        // Close the cursor to avoid memory leaks
        cursor?.close()

        return musicFiles
    }

    fun formatDurationFromSeconds(seconds: Long): String {
        val totalSeconds = seconds.toInt()
        val minutes = totalSeconds / 60
        val remainingSeconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, remainingSeconds)
    }

    fun formatDurationFromMillis(milliseconds: Long): String {
        val totalSeconds = (milliseconds / 1000).toInt()
        val minutes = totalSeconds / 60
        val remainingSeconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, remainingSeconds)
    }

}
