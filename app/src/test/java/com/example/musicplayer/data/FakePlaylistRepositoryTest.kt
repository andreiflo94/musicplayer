package com.example.musicplayer.data

import com.example.musicplayer.domain.model.Playlist
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/** The fake must follow the same rules as the real database, or tests built on it lie. */
class FakePlaylistRepositoryTest {

    @Test
    fun `fake matches the database rules`() = runBlocking {
        val repository = FakePlaylistRepository(listOf(Playlist(5, "A")))

        repository.insertTrack(5, "X - Y", "/p.mp3", null)
        repository.insertTrack(5, "X - Y", "/p.mp3", null)
        repository.insertTrackToNewPlaylist("B", "X - Y", "/p.mp3", null)
        val b = repository.getAllPlaylists().first().single { it.name == "B" }

        assertEquals(6L, b.id)
        assertEquals(1, repository.getTracksForPlaylist(5).first().size)
        assertEquals(1, repository.getTracksForPlaylist(b.id).first().size)

        repository.deletePlaylist(5)
        assertEquals(listOf(b), repository.getAllPlaylists().first())
        assertEquals(0, repository.getTracksForPlaylist(5).first().size)
    }
}
