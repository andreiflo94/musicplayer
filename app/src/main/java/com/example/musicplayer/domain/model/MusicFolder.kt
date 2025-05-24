package com.example.musicplayer.domain.model

data class MusicFolder(
    val name: String, val path: String, val albumIconUrl: String?,
    val artistName: String = ""
)
