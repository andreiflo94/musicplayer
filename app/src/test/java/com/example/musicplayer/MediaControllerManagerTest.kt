package com.example.musicplayer

import com.example.musicplayer.domain.MediaControllerManager
import com.example.musicplayer.domain.model.Track
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class MediaControllerManagerTest {

    private lateinit var mediaControllerManager: MediaControllerManager

    @Before
    fun setUp() {
        mediaControllerManager = mock(MediaControllerManager::class.java)
    }

    @Test
    fun testIsCurrentlyPlaying() {
        `when`(mediaControllerManager.isCurrentlyPlaying()).thenReturn(true)
        assertTrue(mediaControllerManager.isCurrentlyPlaying())
    }

    @Test
    fun testCurrentPlayingPosition() {
        `when`(mediaControllerManager.currentPlayingPosition()).thenReturn(5000L)
        assertEquals(5000L, mediaControllerManager.currentPlayingPosition())
    }

    @Test
    fun testCurrentTrackDuration() {
        `when`(mediaControllerManager.currentTrackDuration()).thenReturn(180000L)
        assertEquals(180000L, mediaControllerManager.currentTrackDuration())
    }

    @Test
    fun testGetCurrentPlayingTrackName() {
        `when`(mediaControllerManager.getCurrentPlayingTrackName()).thenReturn("Song Title")
        assertEquals("Song Title", mediaControllerManager.getCurrentPlayingTrackName())
    }

    @Test
    fun testStartAudioPlayback() {
        val trackList = listOf(
            Track(
                path = "song1.mp3",
                albumIconUrl = ""
            ), Track(path = "song2.mp3", albumIconUrl = "")
        )
        val index = 1
        doNothing().`when`(mediaControllerManager).startAudioPlayback(trackList, index)
        mediaControllerManager.startAudioPlayback(trackList, index)
        verify(mediaControllerManager).startAudioPlayback(trackList, index)
    }

    @Test
    fun testPlayPauseClick() {
        doNothing().`when`(mediaControllerManager).playPauseClick()
        mediaControllerManager.playPauseClick()
        verify(mediaControllerManager).playPauseClick()
    }

    @Test
    fun testHasNextMediaItem() {
        `when`(mediaControllerManager.hasNextMediaItem()).thenReturn(true)
        assertTrue(mediaControllerManager.hasNextMediaItem())
    }

    @Test
    fun testSeekToNextMediaItem() {
        doNothing().`when`(mediaControllerManager).seekToNextMediaItem()
        mediaControllerManager.seekToNextMediaItem()
        verify(mediaControllerManager).seekToNextMediaItem()
    }

    @Test
    fun testSeekToPreviousMediaItem() {
        val progress = 3000L
        doNothing().`when`(mediaControllerManager).seekToPreviousMediaItem(progress)
        mediaControllerManager.seekToPreviousMediaItem(progress)
        verify(mediaControllerManager).seekToPreviousMediaItem(progress)
    }

    @Test
    fun testStopPlaying() {
        doNothing().`when`(mediaControllerManager).stopPlaying()
        mediaControllerManager.stopPlaying()
        verify(mediaControllerManager).stopPlaying()
    }

    @Test
    fun testOnProgressUpdate() {
        val progress = 15000L
        doNothing().`when`(mediaControllerManager).onProgressUpdate(progress)
        mediaControllerManager.onProgressUpdate(progress)
        verify(mediaControllerManager).onProgressUpdate(progress)
    }
}
