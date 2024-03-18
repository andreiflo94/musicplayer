package com.example.musicplayer.mainfeature.domain

interface MusicFoldersRepository {

    suspend fun getMusicFolders(): List<MusicFolder>

    suspend fun getMusicFilesFromPath(path: String): List<MusicFolder>
}