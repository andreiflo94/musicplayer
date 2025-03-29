package com.example.musicplayer.domain.model

data class MusicFolder(
    val name: String, val path: String, val albumIconUrl: String?, var isPlaying: Boolean = false
) {

    fun isAudioFile(): Boolean {
        return path.endsWith(".mp3") || path.endsWith(".m4a")
                || path.endsWith(".mp4")
    }
}
