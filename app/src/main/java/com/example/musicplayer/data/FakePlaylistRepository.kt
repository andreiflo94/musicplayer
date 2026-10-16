package com.example.musicplayer.data

import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.domain.repo.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory [PlaylistRepository] for previews and tests. Mirrors the database's rules: a track
 * is stored once per playlist, and deleting a playlist deletes its tracks.
 */
class FakePlaylistRepository(
    initialPlaylists: List<Playlist> = listOf(
        Playlist(id = 1, name = "Playlist 1"),
        Playlist(id = 2, name = "Playlist 2")
    )
) : PlaylistRepository {

    private data class StoredTrack(
        val playlistId: Long,
        val trackName: String,
        val path: String,
        val artPath: String?
    )

    private val playlists = MutableStateFlow(initialPlaylists)
    private val tracks = MutableStateFlow<List<StoredTrack>>(emptyList())
    private var nextId = (initialPlaylists.maxOfOrNull { it.id } ?: 0) + 1

    override fun insertPlaylist(name: String) {
        playlists.value = playlists.value + Playlist(nextId++, name)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = playlists

    override fun getPlaylistById(id: Long): Playlist? = playlists.value.firstOrNull { it.id == id }

    override fun deletePlaylist(id: Long) {
        playlists.value = playlists.value.filterNot { it.id == id }
        tracks.value = tracks.value.filterNot { it.playlistId == id }
    }

    override fun insertTrack(playlistId: Long, trackName: String, path: String, artPath: String?) {
        if (tracks.value.any { it.playlistId == playlistId && it.path == path }) return
        tracks.value = tracks.value + StoredTrack(playlistId, trackName, path, artPath)
    }

    override fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>> =
        tracks.map { all ->
            all.filter { it.playlistId == playlistId }.map {
                val (artist, title) = splitStoredTrackName(it.trackName)
                Track(
                    path = it.path,
                    albumIconUrl = it.artPath,
                    artistName = artist,
                    trackName = title
                )
            }
        }

    override fun deleteTrack(playlistId: Long, path: String) {
        tracks.value = tracks.value.filterNot { it.playlistId == playlistId && it.path == path }
    }

    override fun insertTrackToNewPlaylist(
        playlistName: String,
        trackName: String,
        path: String,
        artPath: String?
    ) {
        val id = nextId
        insertPlaylist(playlistName)
        insertTrack(id, trackName, path, artPath)
    }
}
