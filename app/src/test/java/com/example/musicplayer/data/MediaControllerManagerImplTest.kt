package com.example.musicplayer.data

import android.content.Context
import com.example.musicplayer.domain.MediaControllerEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.kotlin.mock

/** Behaviour before anything is queued; playback itself is covered by the on-device test. */
class MediaControllerManagerImplTest {

    private val manager = MediaControllerManagerImpl(mock<Context>())

    @Test
    fun `nothing queued means no current track instead of a crash`() {
        assertNull(manager.getCurrentPlayingTrack())
        assertEquals("", manager.getCurrentPlayingTrackName())
        assertEquals("", manager.getCurrentPlayingTrackPath())
    }

    @Test
    fun `events start idle`() {
        assertEquals(MediaControllerEvent.Idle, manager.mediaControllerEvents.value)
    }
}
