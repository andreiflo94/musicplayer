package com.example.musicplayer.data

import com.example.musicplayer.domain.model.Track
import org.junit.Assert.assertEquals
import org.junit.Test

class MusicUtilsTest {

    @Test
    fun `millis are formatted as m-ss`() {
        assertEquals("0:00", MusicUtils.formatDurationFromMillis(0))
        assertEquals("0:00", MusicUtils.formatDurationFromMillis(-5_000))
        assertEquals("0:00", MusicUtils.formatDurationFromMillis(999))
        assertEquals("0:05", MusicUtils.formatDurationFromMillis(5_400))
        assertEquals("3:05", MusicUtils.formatDurationFromMillis(185_000))
        assertEquals("61:01", MusicUtils.formatDurationFromMillis(3_661_000))
    }

    @Test
    fun `seconds are formatted as m-ss`() {
        assertEquals("0:00", MusicUtils.formatDurationFromSeconds(0))
        assertEquals("1:00", MusicUtils.formatDurationFromSeconds(60))
        assertEquals("10:09", MusicUtils.formatDurationFromSeconds(609))
    }
}

class TrackTest {

    @Test
    fun `name joins artist and title only when both are present`() {
        assertEquals("A - T", Track("/p", null, artistName = "A", trackName = "T").name)
        assertEquals("T", Track("/p", null, trackName = "T").name)
        assertEquals("A", Track("/p", null, artistName = "A").name)
        assertEquals("", Track("/p", null).name)
    }
}
