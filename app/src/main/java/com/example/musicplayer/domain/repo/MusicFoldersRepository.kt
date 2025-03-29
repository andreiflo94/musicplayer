package com.example.musicplayer.domain.repo

import com.example.musicplayer.domain.model.MusicFolder

interface MusicFoldersRepository {

    suspend fun getMusicFolders(): List<MusicFolder>

    suspend fun getMusicFilesFromPath(path: String): List<MusicFolder>
}