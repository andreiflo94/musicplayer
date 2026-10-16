package com.example.musicplayer.presentation.viewmodels

import androidx.media3.common.C
import androidx.media3.common.Player
import com.example.musicplayer.domain.MediaControllerEvent
import com.example.musicplayer.testutil.FakeMediaControllerManager
import com.example.musicplayer.testutil.FakeMusicFoldersRepository
import com.example.musicplayer.testutil.MainDispatcherRule
import com.example.musicplayer.testutil.clear
import com.example.musicplayer.testutil.track
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainActivityViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var manager: FakeMediaControllerManager
    private lateinit var viewModel: MainActivityViewModel
    private val tracks = listOf(track(0), track(1), track(2))

    @Before
    fun setUp() {
        manager = FakeMediaControllerManager()
        viewModel = MainActivityViewModel(manager, FakeMusicFoldersRepository())
    }

    // Stops the 100 ms polling loop so runTest can finish
    @After
    fun tearDown() = viewModel.clear()

    private fun TestScope.emit(event: MediaControllerEvent) {
        manager.mediaControllerEvents.value = event
        runCurrent()
    }

    @Test
    fun `connects to the player on creation and releases it when cleared`() {
        assertEquals(1, manager.initCalls)
        viewModel.clear()
        assertEquals(1, manager.releaseCalls)
        assertTrue(viewModel.trackState.value.stopped)
    }

    @Test
    fun `starts out stopped`() {
        val state = viewModel.trackState.value
        assertTrue(state.stopped)
        assertFalse(state.isPlaying)
        assertEquals(0f, viewModel.trackLiveProgress.value)
        assertEquals("0:00", viewModel.trackProgressFormatted.value)
    }

    @Test
    fun `startAudioPlayback fills the now-playing state`() = runTest {
        manager.duration = 185_000
        manager.hasNext = true

        viewModel.startAudioPlayback(tracks, 1)

        assertEquals(tracks, manager.trackList)
        val state = viewModel.trackState.value
        assertFalse(state.stopped)
        assertTrue(state.isPlaying)
        assertEquals("Title 1", state.title)
        assertEquals("Artist 1", state.artist)
        assertEquals("Artist 1 - Title 1", state.trackName)
        assertEquals(tracks[1].path, state.trackPath)
        assertEquals("art:${tracks[1].path}", state.trackArtUrl)
        assertEquals(185_000, state.durationMs)
        assertEquals("3:05", state.trackDurationFormatted)
        assertTrue(state.hasNextMediaItem)
    }

    @Test
    fun `unknown duration is shown as zero rather than negative`() = runTest {
        manager.duration = C.TIME_UNSET

        viewModel.startAudioPlayback(tracks, 0)

        assertEquals(0, viewModel.trackState.value.durationMs)
        assertEquals("0:00", viewModel.trackState.value.trackDurationFormatted)
    }

    @Test
    fun `progress is polled while playing and stops when paused`() = runTest {
        runCurrent()
        manager.duration = 200_000
        viewModel.startAudioPlayback(tracks, 0)
        manager.position = 50_000
        emit(MediaControllerEvent.IsPlayingChanged(true))

        advanceTimeBy(150)
        assertEquals(25f, viewModel.trackLiveProgress.value)
        assertEquals("0:50", viewModel.trackProgressFormatted.value)

        manager.isPlaying = false
        emit(MediaControllerEvent.IsPlayingChanged(false))
        assertFalse(viewModel.trackState.value.isPlaying)

        manager.position = 150_000
        advanceTimeBy(500)
        assertEquals(25f, viewModel.trackLiveProgress.value)
    }

    @Test
    fun `track transition picks up the player's new index`() = runTest {
        runCurrent()
        viewModel.startAudioPlayback(tracks, 0)
        manager.playerIndex = 2

        emit(
            MediaControllerEvent.TrackTransition(
                tracks[2],
                Player.MEDIA_ITEM_TRANSITION_REASON_AUTO
            )
        )

        assertEquals("Title 2", viewModel.trackState.value.title)
    }

    @Test
    fun `idle and ended playback hide the player`() = runTest {
        runCurrent()
        viewModel.startAudioPlayback(tracks, 0)

        emit(MediaControllerEvent.PlaybackStateChanged(Player.STATE_IDLE))
        assertTrue(viewModel.trackState.value.stopped)

        emit(MediaControllerEvent.PlaybackStateChanged(Player.STATE_READY))
        assertFalse(viewModel.trackState.value.stopped)

        emit(MediaControllerEvent.PlaybackStateChanged(Player.STATE_ENDED))
        assertTrue(viewModel.trackState.value.stopped)
        assertFalse(viewModel.trackState.value.isPlaying)
    }

    @Test
    fun `isPlaying false right after the playlist ends keeps the player hidden`() = runTest {
        runCurrent()
        viewModel.startAudioPlayback(tracks, 2)
        emit(MediaControllerEvent.IsPlayingChanged(true))

        // Media3's order at the end of the last track
        manager.isPlaying = false
        emit(MediaControllerEvent.PlaybackStateChanged(Player.STATE_ENDED))
        emit(MediaControllerEvent.IsPlayingChanged(false))

        assertTrue(viewModel.trackState.value.stopped)
        assertFalse(viewModel.trackState.value.isPlaying)
    }

    @Test
    fun `pausing keeps the player visible`() = runTest {
        runCurrent()
        viewModel.startAudioPlayback(tracks, 0)
        emit(MediaControllerEvent.IsPlayingChanged(true))

        manager.isPlaying = false
        emit(MediaControllerEvent.IsPlayingChanged(false))

        assertFalse(viewModel.trackState.value.stopped)
        assertFalse(viewModel.trackState.value.isPlaying)
        assertEquals("Title 0", viewModel.trackState.value.title)
    }

    @Test
    fun `seeking forwards the percentage and updates progress right away`() = runTest {
        manager.duration = 240_000
        viewModel.startAudioPlayback(tracks, 0)

        viewModel.onProgressUpdate(50)

        assertEquals(listOf(50L), manager.progressUpdates)
        assertEquals(50f, viewModel.trackLiveProgress.value)
        assertEquals("2:00", viewModel.trackProgressFormatted.value)
    }

    @Test
    fun `previous passes the current progress so the track can restart instead`() = runTest {
        manager.duration = 100_000
        viewModel.startAudioPlayback(tracks, 0)
        viewModel.onProgressUpdate(30)

        viewModel.playPrevious()

        assertEquals(listOf(30L), manager.previousRequests)
    }

    @Test
    fun `transport controls are forwarded`() {
        viewModel.playPauseClick()
        viewModel.skipForward()
        viewModel.stopPlaying()

        assertEquals(1, manager.playPauseCalls)
        assertEquals(1, manager.nextCalls)
        assertEquals(1, manager.stopCalls)
    }

    @Test
    fun `playAll ignores an empty list`() {
        viewModel.playAll(emptyList(), shuffle = false)

        assertTrue(manager.trackList.isEmpty())
        assertTrue(viewModel.trackState.value.stopped)
    }

    @Test
    fun `playAll starts from the first track in order`() {
        viewModel.playAll(tracks, shuffle = false)

        assertEquals(tracks, manager.trackList)
        assertEquals(0, manager.index)
    }

    @Test
    fun `shuffled playAll plays every track once`() {
        viewModel.playAll(tracks, shuffle = true)

        assertEquals(tracks.toSet(), manager.trackList.toSet())
        assertEquals(tracks.size, manager.trackList.size)
        assertEquals(0, manager.index)
    }
}
