package com.example.musicplayer.domain.model

data class Track(val name: String, val path: String, val albumIconUrl: String?, var isPlaying: Boolean = false)
