package com.example.musicplayer.mainfeature.domain

data class MusicFolder(val name: String, val path: String, val albumIconUrl: String?) {

    fun isAudioFile(): Boolean {
        return path.endsWith(".mp3") || path.endsWith(".m4a")
                || path.endsWith(".mp4")
    }
}
