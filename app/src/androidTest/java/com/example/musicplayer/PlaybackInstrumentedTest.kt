package com.example.musicplayer

import androidx.lifecycle.ViewModelStore
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.musicplayer.data.MediaControllerManagerImpl
import com.example.musicplayer.domain.MediaControllerEvent
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.domain.repo.MusicFoldersRepository
import com.example.musicplayer.presentation.viewmodels.MainActivityViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Drives the real PlaybackService/ExoPlayer through MediaControllerManagerImpl with generated
 * WAV files. Media3 controllers are bound to the main looper, so every call goes through it.
 */
@UnstableApi
@RunWith(AndroidJUnit4::class)
class PlaybackInstrumentedTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext
    private lateinit var manager: MediaControllerManagerImpl
    private lateinit var tracks: List<Track>

    private fun <T> onMain(block: () -> T): T {
        var result: Result<T>? = null
        instrumentation.runOnMainSync { result = runCatching(block) }
        return result!!.getOrThrow()
    }

    @Before
    fun setUp() {
        val dir = File(context.cacheDir, "playback-test")
        tracks = listOf(
            Track(
                writeSilentWav(File(dir, "one.wav"), 4_000).path,
                null,
                artistName = "Test",
                trackName = "One"
            ),
            Track(
                writeSilentWav(File(dir, "two.wav"), 3_000).path,
                null,
                artistName = "Test",
                trackName = "Two"
            ),
            Track(
                writeSilentWav(File(dir, "short.wav"), 600).path,
                null,
                artistName = "Test",
                trackName = "Short"
            ),
        )
        manager = MediaControllerManagerImpl(context)
    }

    @After
    fun tearDown() {
        onMain {
            manager.stopPlaying()
            manager.release()
        }
    }

    /** Playback calls are dropped until the async controller connects, so retry until it plays. */
    private fun startWhenConnected(manager: MediaControllerManagerImpl, index: Int = 0) {
        waitUntil("controller to connect and start playing") {
            onMain {
                if (!manager.isCurrentlyPlaying()) manager.startAudioPlayback(tracks, index)
                manager.isCurrentlyPlaying()
            }
        }
    }

    @Test
    fun playsSkipsSeeksAndStops() {
        onMain { manager.init() }
        startWhenConnected(manager)

        assertEquals(tracks[0], onMain { manager.getCurrentPlayingTrack() })
        assertTrue(onMain { manager.hasNextMediaItem() })
        waitUntil("duration to be known") { onMain { manager.currentTrackDuration() } > 0 }

        onMain { manager.onProgressUpdate(50) }
        waitUntil("seek to the middle") { onMain { manager.currentPlayingPosition() } >= 1_500 }

        onMain { manager.seekToNextMediaItem() }
        waitUntil("transition to the second track") {
            onMain {
                manager.setCurrentTrackPlayingIndex()
                manager.getCurrentPlayingTrack() == tracks[1]
            }
        }
        assertTrue(onMain { manager.hasNextMediaItem() })
        // isPlaying is briefly false while the new track buffers; a click then would mean "play"
        waitUntil("second track to play") { onMain { manager.isCurrentlyPlaying() } }

        onMain { manager.playPauseClick() }
        waitUntil("pause") { !onMain { manager.isCurrentlyPlaying() } }
        assertEquals(
            MediaControllerEvent.IsPlayingChanged(false),
            manager.mediaControllerEvents.value
        )

        onMain { manager.stopPlaying() }
        waitUntil("idle after stop") {
            manager.mediaControllerEvents.value == MediaControllerEvent.PlaybackStateChanged(Player.STATE_IDLE) ||
                    onMain { !manager.isCurrentlyPlaying() }
        }
    }

    @Test
    fun seekingBeforeTheTrackLoadsIsIgnored() {
        onMain { manager.init() }
        // Before connecting there is no controller; nothing may throw
        onMain {
            manager.onProgressUpdate(50)
            manager.playPauseClick()
            manager.seekToNextMediaItem()
            manager.seekToPreviousMediaItem(0)
        }
        startWhenConnected(manager)
        assertTrue(onMain { manager.currentPlayingPosition() } >= 0)
    }

    @Test
    fun playerHidesWhenThePlaylistEnds() {
        val noArt = object : MusicFoldersRepository {
            override suspend fun getMusicFolders() =
                emptyList<com.example.musicplayer.domain.model.MusicFolder>()

            override suspend fun getMusicFilesFromPath(path: String) = emptyList<Track>()
            override fun getAlbumIconUrl(albumPath: String) = ""
            override fun getArtistName(filePath: String) = ""
            override fun getTrackTitle(filePath: String) = ""
        }
        // The ViewModel calls init() itself and collects events on the main thread
        val viewModel = onMain { MainActivityViewModel(manager, noArt) }

        waitUntil("controller to connect and start playing") {
            onMain {
                if (!manager.isCurrentlyPlaying()) viewModel.startAudioPlayback(tracks, 2)
                manager.isCurrentlyPlaying()
            }
        }
        assertFalse(viewModel.trackState.value.stopped)
        assertEquals("Short", viewModel.trackState.value.title)

        // The last track is 0.6 s long; once it ends the player sheet should go away
        waitUntil("playback to end", timeoutMs = 10_000) {
            manager.mediaControllerEvents.value.let {
                it == MediaControllerEvent.PlaybackStateChanged(Player.STATE_ENDED) ||
                        it == MediaControllerEvent.IsPlayingChanged(false)
            }
        }
        // Let any trailing callbacks for the same transition land
        instrumentation.waitForIdleSync()
        Thread.sleep(300)
        instrumentation.waitForIdleSync()

        val state = viewModel.trackState.value
        // Clear it like the framework would, stopping its progress polling
        onMain {
            ViewModelStore().apply { @Suppress("RestrictedApi") put("vm", viewModel) }.clear()
        }

        assertFalse(state.isPlaying)
        assertTrue("player should be hidden after the playlist ends", state.stopped)
    }
}
