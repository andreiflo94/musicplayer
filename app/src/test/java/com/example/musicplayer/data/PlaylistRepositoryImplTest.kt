package com.example.musicplayer.data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.musicplayer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.util.Properties

class PlaylistRepositoryImplTest {

    private lateinit var driver: JdbcSqliteDriver
    private lateinit var repository: PlaylistRepositoryImpl

    @Before
    fun setUp() {
        // Same as the app's driver callback: foreign keys on, so ON DELETE CASCADE fires
        driver = JdbcSqliteDriver(
            JdbcSqliteDriver.IN_MEMORY,
            Properties().apply { put("foreign_keys", "true") }
        )
        musicplayer.Schema.create(driver)
        repository = PlaylistRepositoryImpl(musicplayer(driver))
    }

    @After
    fun tearDown() = driver.close()

    private fun tracks(playlistId: Long) =
        runBlocking { repository.getTracksForPlaylist(playlistId).first() }

    private fun playlists() = runBlocking { repository.getAllPlaylists().first() }

    private val song = Track(
        path = "/music/a.mp3", albumIconUrl = "/music/cover.jpg",
        artistName = "Metallica", trackName = "One"
    )

    @Test
    fun `playlists can be created, looked up and deleted`() {
        repository.insertPlaylist("Chill")
        repository.insertPlaylist("Gym")

        val all = playlists()
        assertEquals(listOf("Chill", "Gym"), all.map { it.name })
        assertEquals(Playlist(all[1].id, "Gym"), repository.getPlaylistById(all[1].id))

        repository.deletePlaylist(all[0].id)
        assertEquals(listOf("Gym"), playlists().map { it.name })
        assertNull(repository.getPlaylistById(all[0].id))
    }

    @Test
    fun `tracks round-trip through the stored name`() {
        repository.insertPlaylist("Chill")
        val id = playlists().single().id

        repository.insertTrack(id, song.name, song.path, song.albumIconUrl)

        assertEquals(listOf(song), tracks(id))
    }

    @Test
    fun `tracks with a blank artist or title can be read back`() {
        // Track.name has no " - " for these; reading them used to throw IndexOutOfBounds
        repository.insertPlaylist("Odd tags")
        val id = playlists().single().id
        val noArtist = Track(path = "/music/b.mp3", albumIconUrl = null, trackName = "Untitled 3")
        val noTitle = Track(path = "/music/c.mp3", albumIconUrl = null, artistName = "Somebody")

        repository.insertTrack(id, noArtist.name, noArtist.path, null)
        repository.insertTrack(id, noTitle.name, noTitle.path, null)

        val names = tracks(id).associate { it.path to it.name }
        assertEquals("Untitled 3", names[noArtist.path])
        assertEquals("Somebody", names[noTitle.path])
    }

    @Test
    fun `a track is stored once per playlist but can be in several playlists`() {
        repository.insertPlaylist("A")
        repository.insertPlaylist("B")
        val (a, b) = playlists().map { it.id }

        repository.insertTrack(a, song.name, song.path, null)
        repository.insertTrack(a, song.name, song.path, null)
        repository.insertTrack(b, song.name, song.path, null)

        assertEquals(1, tracks(a).size)
        assertEquals(1, tracks(b).size)
    }

    @Test
    fun `deleting a track only affects that playlist`() {
        repository.insertPlaylist("A")
        repository.insertPlaylist("B")
        val (a, b) = playlists().map { it.id }
        repository.insertTrack(a, song.name, song.path, null)
        repository.insertTrack(b, song.name, song.path, null)

        repository.deleteTrack(a, song.path)

        assertEquals(emptyList<Track>(), tracks(a))
        assertEquals(1, tracks(b).size)
    }

    @Test
    fun `deleting a playlist cascades to its tracks`() {
        repository.insertPlaylist("A")
        val id = playlists().single().id
        repository.insertTrack(id, song.name, song.path, null)

        repository.deletePlaylist(id)

        val count = driver.executeQuery(
            null,
            "SELECT COUNT(*) FROM PlaylistTrack",
            { c -> c.next(); app.cash.sqldelight.db.QueryResult.Value(c.getLong(0)) },
            0
        ).value
        assertEquals(0L, count)
    }

    @Test
    fun `adding to a new playlist creates it with the track`() {
        repository.insertPlaylist("Existing")

        repository.insertTrackToNewPlaylist("Fresh", song.name, song.path, song.albumIconUrl)

        val fresh = playlists().single { it.name == "Fresh" }
        assertEquals(listOf(song), tracks(fresh.id))
        assertEquals(emptyList<Track>(), tracks(playlists().single { it.name == "Existing" }.id))
    }

    @Test
    fun `track flow re-emits when the playlist changes`() = runBlocking {
        repository.insertPlaylist("A")
        val id = playlists().single().id
        val flow = repository.getTracksForPlaylist(id)
        assertEquals(0, flow.first().size)

        repository.insertTrack(id, song.name, song.path, null)

        assertEquals(1, flow.first().size)
    }

    @Test
    fun `stored names split on the first separator only`() {
        assertEquals("AC/DC" to "Back In Black", splitStoredTrackName("AC/DC - Back In Black"))
        assertEquals("Artist" to "Song - Live", splitStoredTrackName("Artist - Song - Live"))
        assertEquals("" to "Just a title", splitStoredTrackName("Just a title"))
        assertEquals("" to "", splitStoredTrackName(""))
    }
}
