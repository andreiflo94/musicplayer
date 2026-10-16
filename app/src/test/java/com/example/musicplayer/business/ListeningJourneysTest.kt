package com.example.musicplayer.business

import androidx.media3.common.Player
import com.example.musicplayer.domain.MediaControllerEvent
import com.example.musicplayer.domain.PlaybackRules
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.presentation.viewmodels.MainActivityViewModel
import com.example.musicplayer.testutil.FakeMediaControllerManager
import com.example.musicplayer.testutil.FakeMusicFoldersRepository
import com.example.musicplayer.testutil.MainDispatcherRule
import com.example.musicplayer.testutil.clear
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** What a listener does with the player, and what they should see and hear as a result. */
@OptIn(ExperimentalCoroutinesApi::class)
class ListeningJourneysTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val album = listOf(
        Track(
            "/Music/Ride the Lightning/01.mp3",
            null,
            artistName = "Metallica",
            trackName = "Fight Fire with Fire"
        ),
        Track(
            "/Music/Ride the Lightning/02.mp3",
            null,
            artistName = "Metallica",
            trackName = "Ride the Lightning"
        ),
        Track(
            "/Music/Ride the Lightning/03.mp3",
            null,
            artistName = "Metallica",
            trackName = "For Whom the Bell Tolls"
        ),
    )
    private val player = FakeMediaControllerManager()
    private lateinit var viewModel: MainActivityViewModel

    // Not a field initializer: it must be created after the rule has swapped in the test Main dispatcher
    @Before
    fun setUp() {
        viewModel = MainActivityViewModel(player, FakeMusicFoldersRepository())
    }

    // Ends each journey by closing the screen; a still-playing ViewModel polls progress forever,
    // and runTest would keep advancing virtual time through it
    private fun journey(block: suspend TestScope.() -> Unit) = runTest {
        block()
        viewModel.clear()
    }

    private fun TestScope.playerReports(event: MediaControllerEvent) {
        player.mediaControllerEvents.value = event
        runCurrent()
    }

    private fun TestScope.listenerTaps(index: Int, durationMs: Long = 240_000) {
        runCurrent()
        player.duration = durationMs
        viewModel.startAudioPlayback(album, index)
        playerReports(MediaControllerEvent.IsPlayingChanged(true))
    }

    @Test
    fun `tapping a song plays it with the rest of the album queued around it`() = journey {
        listenerTaps(1)

        assertEquals(album, player.trackList)
        assertEquals(1, player.index)
        val nowPlaying = viewModel.trackState.value
        assertFalse(nowPlaying.stopped)
        assertTrue(nowPlaying.isPlaying)
        assertEquals("Ride the Lightning", nowPlaying.title)
        assertEquals("Metallica", nowPlaying.artist)
        assertEquals("4:00", nowPlaying.trackDurationFormatted)
    }

    @Test
    fun `when a song finishes the next one takes over the now-playing info`() = journey {
        listenerTaps(0)

        player.playerIndex = 1
        playerReports(
            MediaControllerEvent.TrackTransition(
                album[1],
                Player.MEDIA_ITEM_TRANSITION_REASON_AUTO
            )
        )

        assertEquals("Ride the Lightning", viewModel.trackState.value.title)
        assertEquals(album[1].path, viewModel.trackState.value.trackPath)
    }

    @Test
    fun `the elapsed time counts up while playing and freezes while paused`() = journey {
        listenerTaps(0, durationMs = 300_000)

        player.position = 75_000
        advanceTimeBy(250)
        assertEquals("1:15", viewModel.trackProgressFormatted.value)
        assertEquals(25f, viewModel.trackLiveProgress.value)

        player.isPlaying = false
        playerReports(MediaControllerEvent.IsPlayingChanged(false))
        player.position = 90_000
        advanceTimeBy(1_000)
        assertEquals("1:15", viewModel.trackProgressFormatted.value)
    }

    @Test
    fun `pausing keeps the song on screen so the listener can resume it`() = journey {
        listenerTaps(2)

        player.isPlaying = false
        playerReports(MediaControllerEvent.IsPlayingChanged(false))

        val nowPlaying = viewModel.trackState.value
        assertFalse(nowPlaying.stopped)
        assertFalse(nowPlaying.isPlaying)
        assertEquals("For Whom the Bell Tolls", nowPlaying.title)
    }

    @Test
    fun `the player gets out of the way once the album has finished`() = journey {
        listenerTaps(2)

        player.isPlaying = false
        playerReports(MediaControllerEvent.PlaybackStateChanged(Player.STATE_ENDED))
        playerReports(MediaControllerEvent.IsPlayingChanged(false))

        assertTrue(viewModel.trackState.value.stopped)
    }

    @Test
    fun `scrubbing to the middle shows the new time immediately, even while paused`() = journey {
        listenerTaps(0, durationMs = 240_000)
        player.isPlaying = false
        playerReports(MediaControllerEvent.IsPlayingChanged(false))

        viewModel.onProgressUpdate(50)

        assertEquals(listOf(50L), player.progressUpdates)
        assertEquals("2:00", viewModel.trackProgressFormatted.value)
        assertEquals(50f, viewModel.trackLiveProgress.value)
        assertEquals(120_000L, PlaybackRules.seekPositionMs(240_000, 50))
    }

    @Test
    fun `scrubbing can't land before the start or past the end`() {
        assertEquals(0L, PlaybackRules.seekPositionMs(240_000, -10))
        assertEquals(240_000L, PlaybackRules.seekPositionMs(240_000, 130))
    }

    @Test
    fun `scrubbing before the song has loaded is ignored rather than jumping somewhere odd`() {
        assertNull(
            PlaybackRules.seekPositionMs(
                durationMs = androidx.media3.common.C.TIME_UNSET,
                percent = 50
            )
        )
        assertNull(PlaybackRules.seekPositionMs(durationMs = 0, percent = 50))
    }

    @Test
    fun `previous restarts a song that is already under way`() = journey {
        listenerTaps(1)
        viewModel.onProgressUpdate(40)

        viewModel.playPrevious()

        assertEquals(listOf(40L), player.previousRequests)
        assertFalse(
            PlaybackRules.previousGoesToPreviousSong(
                progressPercent = 40,
                hasPreviousSong = true
            )
        )
    }

    @Test
    fun `previous at the very start of a song goes back to the song before`() {
        assertTrue(
            PlaybackRules.previousGoesToPreviousSong(
                progressPercent = 0,
                hasPreviousSong = true
            )
        )
    }

    @Test
    fun `previous on the first song of the album just restarts it`() {
        assertFalse(
            PlaybackRules.previousGoesToPreviousSong(
                progressPercent = 0,
                hasPreviousSong = false
            )
        )
    }

    @Test
    fun `play all starts the album from the top in album order`() = journey {
        runCurrent()
        viewModel.playAll(album, shuffle = false)

        assertEquals(album, player.trackList)
        assertEquals(0, player.index)
        assertEquals("Fight Fire with Fire", viewModel.trackState.value.title)
    }

    @Test
    fun `an empty folder's play all button does nothing`() = journey {
        viewModel.playAll(emptyList(), shuffle = true)

        assertTrue(player.trackList.isEmpty())
        assertTrue(viewModel.trackState.value.stopped)
    }
}
