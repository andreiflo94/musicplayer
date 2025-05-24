package com.example.musicplayer.domain.repo

import com.example.musicplayer.domain.model.MusicFolder
import com.example.musicplayer.domain.model.Track

interface MusicFoldersRepository {

    suspend fun getMusicFolders(): List<MusicFolder>

    suspend fun getMusicFilesFromPath(path: String): List<Track>

    fun getAlbumIconUrl(albumPath: String): String

    fun getArtistName(filePath: String): String

    fun getTrackTitle(filePath: String): String
}