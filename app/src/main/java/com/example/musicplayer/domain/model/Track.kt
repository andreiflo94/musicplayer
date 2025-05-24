package com.example.musicplayer.domain.model

data class Track(
    val path: String, val albumIconUrl: String?, var isPlaying: Boolean = false,
    val artistName: String = "", var trackName: String = ""
) {
    val name: String
        get() = if (artistName.isNotBlank() && trackName.isNotBlank()) {
            "$artistName - $trackName"
        } else {
            artistName + trackName
        }
}
