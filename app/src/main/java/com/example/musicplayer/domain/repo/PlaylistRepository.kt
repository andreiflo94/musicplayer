package com.example.musicplayer.domain.repo

import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    // Playlist CRUD Operations
    fun insertPlaylist(name: String)
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getPlaylistById(id: Long): Playlist?
    fun deletePlaylist(id: Long)

    // PlaylistTrack CRUD Operations
    fun insertTrack(playlistId: Long, trackName: String, path: String, artPath: String?)
    fun getTracksForPlaylist(playlistId: Long): List<Track>
    fun deleteTrack(path: String)

    fun insertTrackToNewPlaylist(playlistName: String, trackName: String, path: String, artPath: String?)
}