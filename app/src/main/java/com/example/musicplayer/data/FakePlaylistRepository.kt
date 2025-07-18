package com.example.musicplayer.data

import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.domain.repo.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakePlaylistRepository : PlaylistRepository {
    private val playlists = listOf(
        Playlist(id = 1, name = "Playlist 1"),
        Playlist(id = 2, name = "Playlist 2")
    )

    override fun insertPlaylist(name: String) {
        TODO("Not yet implemented")
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flowOf(playlists)
    override fun getPlaylistById(id: Long): Playlist? {
        TODO("Not yet implemented")
    }

    override fun deletePlaylist(id: Long) {
        TODO("Not yet implemented")
    }

    override fun insertTrack(playlistId: Long, trackName: String, path: String, artPath: String?) {
        TODO("Not yet implemented")
    }

    override fun getTracksForPlaylist(playlistId: Long): List<Track> {
        TODO("Not yet implemented")
    }

    override fun deleteTrack(path: String) {
        TODO("Not yet implemented")
    }

    override fun insertTrackToNewPlaylist(
        playlistName: String,
        trackName: String,
        path: String,
        artPath: String?
    ) {
        TODO("Not yet implemented")
    }


}
