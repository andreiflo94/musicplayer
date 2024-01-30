package com.example.musicplayer.mainfeature.domain

interface MusicFoldersRepository {

    fun getMusicFolders(): List<MusicFolder>

    fun getMusicFilesFromPath(path: String): List<MusicFolder>
}