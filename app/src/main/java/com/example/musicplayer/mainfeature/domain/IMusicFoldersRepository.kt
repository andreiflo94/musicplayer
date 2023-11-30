package com.example.musicplayer.mainfeature.domain

interface IMusicFoldersRepository {

    fun getMusicFolders(): List<MusicFolder>

    fun getMusicFilesFromPath(path: String): List<MusicFolder>
}