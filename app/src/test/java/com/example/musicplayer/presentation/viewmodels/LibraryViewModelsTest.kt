package com.example.musicplayer.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import com.example.musicplayer.data.FakePlaylistRepository
import com.example.musicplayer.domain.model.AppTheme
import com.example.musicplayer.domain.model.DarkMode
import com.example.musicplayer.domain.model.MusicFolder
import com.example.musicplayer.domain.model.Playlist
import com.example.musicplayer.domain.model.ThemeSettings
import com.example.musicplayer.domain.repo.ThemeRepository
import com.example.musicplayer.testutil.FakeMusicFoldersRepository
import com.example.musicplayer.testutil.MainDispatcherRule
import com.example.musicplayer.testutil.clear
import com.example.musicplayer.testutil.track
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelsTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `music folders are null while loading, then the repository's list`() = runTest {
        val folders = listOf(MusicFolder("Rock", "/music/Rock", null))
        val viewModel = MusicFoldersViewModel(FakeMusicFoldersRepository(folders = folders))

        assertNull(viewModel.musicFoldersState.value)
        runCurrent()
        assertEquals(folders, viewModel.musicFoldersState.value)
    }

    @Test
    fun `tracks screen loads the folder from its navigation argument`() = runTest {
        val tracks = listOf(track(1), track(2))
        val repository =
            FakeMusicFoldersRepository(tracksByPath = mapOf("/music/My Album" to tracks))
        val viewModel =
            TracksViewModel(repository, SavedStateHandle(mapOf("path" to "/music/My Album")))

        assertNull(viewModel.tracksState.value)
        runCurrent()
        assertEquals(tracks, viewModel.tracksState.value)
        assertEquals(listOf("/music/My Album"), repository.requestedPaths)
    }

    @Test
    fun `tracks screen without a path shows an empty list instead of loading forever`() = runTest {
        val repository = FakeMusicFoldersRepository()
        val viewModel = TracksViewModel(repository, SavedStateHandle())

        runCurrent()
        assertEquals(emptyList<Any>(), viewModel.tracksState.value)
        assertEquals(emptyList<String>(), repository.requestedPaths)
    }

    @Test
    fun `playlist tracks follow additions and removals`() = runTest {
        val repository = FakePlaylistRepository(listOf(Playlist(7, "Gym")))
        val viewModel =
            PlaylistTracksViewModel(repository, SavedStateHandle(mapOf("playlistId" to 7L)))
        runCurrent()
        assertEquals(emptyList<Any>(), viewModel.playlistTracksState.value)

        // e.g. added from the Library tab while this screen stays on the back stack
        repository.insertTrack(7, track(1).name, track(1).path, null)
        runCurrent()
        assertEquals(listOf(track(1)), viewModel.playlistTracksState.value)

        viewModel.removeTrack(track(1))
        runCurrent()
        assertEquals(emptyList<Any>(), viewModel.playlistTracksState.value)
        viewModel.clear()
    }

    @Test
    fun `playlist tracks without an id are empty`() = runTest {
        val viewModel = PlaylistTracksViewModel(FakePlaylistRepository(), SavedStateHandle())
        assertEquals(emptyList<Any>(), viewModel.playlistTracksState.value)
    }

    @Test
    fun `bottom sheet adds to an existing or a new playlist`() = runTest {
        val repository = FakePlaylistRepository(listOf(Playlist(1, "Chill")))
        val viewModel = PlaylistBottomSheetVm(repository)

        viewModel.addToPlaylist(1, track(1))
        viewModel.addToNewPlaylist("Road trip", track(2))
        runCurrent()

        assertEquals(listOf("Chill", "Road trip"), viewModel.playListsState.first().map { it.name })
        assertEquals(listOf(track(1)), repository.getTracksForPlaylist(1).first())
        val roadTrip = repository.getAllPlaylists().first().single { it.name == "Road trip" }
        assertEquals(listOf(track(2)), repository.getTracksForPlaylist(roadTrip.id).first())
    }

    @Test
    fun `removing a playlist deletes it`() = runTest {
        val repository = FakePlaylistRepository(listOf(Playlist(1, "A"), Playlist(2, "B")))
        val viewModel = MyPlaylistsViewModel(repository)

        viewModel.removePlaylist(Playlist(1, "A"))

        assertEquals(listOf(Playlist(2, "B")), viewModel.playListsState.first())
    }

    @Test
    fun `theme view model writes through to the repository`() {
        val repository = object : ThemeRepository {
            override val settings = MutableStateFlow(ThemeSettings())
            override fun setTheme(theme: AppTheme) {
                settings.value = settings.value.copy(theme = theme)
            }

            override fun setDarkMode(darkMode: DarkMode) {
                settings.value = settings.value.copy(darkMode = darkMode)
            }
        }
        val viewModel = ThemeViewModel(repository)

        viewModel.setTheme(AppTheme.OCEAN)
        viewModel.setDarkMode(DarkMode.DARK)

        assertEquals(ThemeSettings(AppTheme.OCEAN, DarkMode.DARK), viewModel.settings.value)
    }
}
