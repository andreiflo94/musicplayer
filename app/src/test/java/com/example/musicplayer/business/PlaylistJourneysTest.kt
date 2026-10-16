package com.example.musicplayer.business

import androidx.lifecycle.SavedStateHandle
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.musicplayer.data.PlaylistRepositoryImpl
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.musicplayer
import com.example.musicplayer.presentation.viewmodels.MyPlaylistsViewModel
import com.example.musicplayer.presentation.viewmodels.PlaylistBottomSheetVm
import com.example.musicplayer.presentation.viewmodels.PlaylistTracksViewModel
import com.example.musicplayer.testutil.MainDispatcherRule
import com.example.musicplayer.testutil.clear
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.Properties

/**
 * Building and curating playlists, through the same ViewModels the screens use and a real
 * SQLite database configured like the app's.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PlaylistJourneysTest {

    // Unconfined: the database emits on its own IO thread and the ViewModels pick it up right away
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val driver = JdbcSqliteDriver(
        JdbcSqliteDriver.IN_MEMORY,
        Properties().apply { put("foreign_keys", "true") }
    ).also { musicplayer.Schema.create(it) }
    private val repository = PlaylistRepositoryImpl(musicplayer(driver))

    private val addToPlaylistSheet = PlaylistBottomSheetVm(repository)
    private val playlistsScreen = MyPlaylistsViewModel(repository)

    private fun song(file: String, artist: String = "Daft Punk", title: String = file) =
        Track("/Music/$file.mp3", "/Music/cover.jpg", artistName = artist, trackName = title)

    private val zebra = song("zebra", title = "Zebra")
    private val apple = song("apple", title = "Apple")
    private val mango = song("mango", title = "Mango")

    @After
    fun tearDown() = driver.close()

    private fun playlistId(name: String) = runBlocking {
        playlistsScreen.playListsState.first().single { it.name == name }.id
    }

    private fun songsIn(name: String) =
        runBlocking { repository.getTracksForPlaylist(playlistId(name)).first() }

    private fun openPlaylist(name: String) =
        PlaylistTracksViewModel(
            repository,
            SavedStateHandle(mapOf("playlistId" to playlistId(name)))
        )

    private fun PlaylistTracksViewModel.awaitSongs(count: Int): List<Track> = runBlocking {
        withTimeout(5_000) { playlistTracksState.first { it?.size == count } }!!
    }

    @Test
    fun `saving a song to a new playlist creates the playlist with that song in it`() {
        addToPlaylistSheet.addToNewPlaylist("Road trip", zebra)

        assertEquals(
            listOf("Road trip"),
            runBlocking { playlistsScreen.playListsState.first() }.map { it.name })
        assertEquals(listOf(zebra), songsIn("Road trip"))
    }

    @Test
    fun `songs play back in the order they were added to the playlist`() {
        addToPlaylistSheet.addToNewPlaylist("Mix", zebra)
        val mix = playlistId("Mix")
        addToPlaylistSheet.addToPlaylist(mix, apple)
        addToPlaylistSheet.addToPlaylist(mix, mango)

        assertEquals(listOf("Zebra", "Apple", "Mango"), songsIn("Mix").map { it.trackName })
    }

    @Test
    fun `adding a song that is already in the playlist doesn't duplicate it`() {
        addToPlaylistSheet.addToNewPlaylist("Mix", zebra)

        addToPlaylistSheet.addToPlaylist(playlistId("Mix"), zebra)

        assertEquals(listOf(zebra), songsIn("Mix"))
    }

    @Test
    fun `the same song can live in several playlists`() {
        addToPlaylistSheet.addToNewPlaylist("Gym", zebra)
        addToPlaylistSheet.addToNewPlaylist("Chill", zebra)

        assertEquals(listOf(zebra), songsIn("Gym"))
        assertEquals(listOf(zebra), songsIn("Chill"))
    }

    @Test
    fun `an open playlist shows songs added from the library without reopening it`() {
        addToPlaylistSheet.addToNewPlaylist("Mix", zebra)
        val openMix = openPlaylist("Mix")
        openMix.awaitSongs(1)

        addToPlaylistSheet.addToPlaylist(playlistId("Mix"), apple)

        assertEquals(listOf(zebra, apple), openMix.awaitSongs(2))
        openMix.clear()
    }

    @Test
    fun `removing a song from one playlist leaves it in the others`() {
        addToPlaylistSheet.addToNewPlaylist("Gym", zebra)
        addToPlaylistSheet.addToNewPlaylist("Chill", zebra)
        val openGym = openPlaylist("Gym")
        openGym.awaitSongs(1)

        openGym.removeTrack(zebra)

        openGym.awaitSongs(0)
        assertEquals(listOf(zebra), songsIn("Chill"))
        openGym.clear()
    }

    @Test
    fun `deleting a playlist removes only that playlist`() {
        addToPlaylistSheet.addToNewPlaylist("Gym", zebra)
        addToPlaylistSheet.addToNewPlaylist("Chill", zebra)

        playlistsScreen.removePlaylist(runBlocking { playlistsScreen.playListsState.first() }.first { it.name == "Gym" })

        assertEquals(
            listOf("Chill"),
            runBlocking { playlistsScreen.playListsState.first() }.map { it.name })
        assertEquals(listOf(zebra), songsIn("Chill"))
    }

    @Test
    fun `songs with incomplete tags still show their title in a playlist`() {
        val untagged = Track("/Music/rec01.mp3", null, trackName = "Voice memo")
        val noTitle = Track("/Music/rec02.mp3", null, artistName = "Unknown band")
        addToPlaylistSheet.addToNewPlaylist("Odds", untagged)
        addToPlaylistSheet.addToPlaylist(playlistId("Odds"), noTitle)

        val shown = openPlaylist("Odds").awaitSongs(2)

        assertEquals(listOf("Voice memo", "Unknown band"), shown.map { it.name })
    }
}
