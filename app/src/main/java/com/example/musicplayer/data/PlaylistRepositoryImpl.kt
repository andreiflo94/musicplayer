package com.example.musicplayer.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.domain.repo.PlaylistRepository
import com.example.musicplayer.musicplayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class PlaylistRepositoryImpl(database: musicplayer) : PlaylistRepository {
    private val queries = database.musicplayerQueries

    // Playlist CRUD Operations
    override fun insertPlaylist(name: String) {
        queries.insertPlaylist(name)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return queries.selectAllPlaylists().asFlow()
            .mapToList(Dispatchers.IO).map {
                it.map { playlist ->
                    Playlist(playlist.id, playlist.name)
                }
            }
    }

    override fun getPlaylistById(id: Long): Playlist? {
        queries.selectPlaylistById(id).executeAsOneOrNull()?.let {
            return Playlist(it.id, it.name)
        } ?: run {
            return null
        }
    }

    override fun deletePlaylist(id: Long) {
        queries.deletePlaylistById(id)
    }

    // PlaylistTrack CRUD Operations
    override fun insertTrack(playlistId: Long, trackName: String, path: String, artPath: String?) {
        val exists = queries.trackExistsInPlaylist(playlistId, path)
        if (exists.executeAsOne())
            return
        queries.insertTrack(playlistId, trackName, path, artPath)
    }

    override fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>> {
        return queries.selectTracksByPlaylistId(playlistId).asFlow()
            .mapToList(Dispatchers.IO).map { rows ->
                rows.map {
                    val (artist, title) = splitStoredTrackName(it.track_name)
                    Track(
                        artistName = artist,
                        trackName = title,
                        path = it.path,
                        albumIconUrl = it.art_path
                    )
                }
            }
    }

    override fun deleteTrack(playlistId: Long, path: String) {
        queries.deleteTrackById(playlistId, path)
    }

    override fun insertTrackToNewPlaylist(
        playlistName: String,
        trackName: String,
        path: String,
        artPath: String?
    ) {
        queries.transaction {
            // Insert the new playlist
            queries.insertPlaylist(playlistName)

            // Insert the track into the new playlist (using last inserted playlist id)
            val playlistId = queries.selectLastInsertedPlaylistId()
                .executeAsOne() // Replace this query accordingly

            queries.insertTrack(playlistId, trackName, path, artPath)
        }
    }
}

/**
 * Splits a stored [Track.name] back into (artist, title). Track.name drops the " - " separator
 * when either part is blank, so a name without one is treated as a bare title.
 */
internal fun splitStoredTrackName(storedName: String): Pair<String, String> {
    val parts = storedName.split(" - ", limit = 2)
    return if (parts.size == 2) parts[0] to parts[1] else "" to storedName
}
