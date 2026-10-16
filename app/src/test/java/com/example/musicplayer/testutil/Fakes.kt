package com.example.musicplayer.testutil

import com.example.musicplayer.domain.MediaControllerEvent
import com.example.musicplayer.domain.MediaControllerManager
import com.example.musicplayer.domain.model.MusicFolder
import com.example.musicplayer.domain.model.Track
import com.example.musicplayer.domain.repo.MusicFoldersRepository
import kotlinx.coroutines.flow.MutableStateFlow

fun track(n: Int, artist: String = "Artist $n", title: String = "Title $n") =
    Track(path = "/music/album/$n.mp3", albumIconUrl = null, artistName = artist, trackName = title)

/** Stands in for the ExoPlayer-backed manager; tests drive [events] and the player fields. */
class FakeMediaControllerManager : MediaControllerManager {
    override val mediaControllerEvents =
        MutableStateFlow<MediaControllerEvent>(MediaControllerEvent.Idle)

    var initCalls = 0
    var releaseCalls = 0
    var isPlaying = false
    var position = 0L
    var duration = 0L
    var hasNext = false
    var trackList: List<Track> = emptyList()
    var index = 0
    var playerIndex = 0
    val progressUpdates = mutableListOf<Long>()
    val previousRequests = mutableListOf<Long>()
    var playPauseCalls = 0
    var nextCalls = 0
    var stopCalls = 0

    override fun init() {
        initCalls++
    }

    override fun release() {
        releaseCalls++
    }

    override fun isCurrentlyPlaying() = isPlaying
    override fun currentPlayingPosition() = position
    override fun currentTrackDuration() = duration
    override fun getCurrentPlayingTrackName() = getCurrentPlayingTrack()?.name.orEmpty()
    override fun getCurrentPlayingTrackPath() = getCurrentPlayingTrack()?.path.orEmpty()
    override fun getCurrentPlayingTrack() = trackList.getOrNull(index)
    override fun setCurrentTrackPlayingIndex() {
        index = playerIndex
    }

    override fun startAudioPlayback(trackList: List<Track>, index: Int) {
        this.trackList = trackList
        this.index = index
        this.playerIndex = index
        isPlaying = true
    }

    override fun playPauseClick() {
        playPauseCalls++
    }

    override fun hasNextMediaItem() = hasNext
    override fun seekToNextMediaItem() {
        nextCalls++
    }

    override fun seekToPreviousMediaItem(progress: Long) {
        previousRequests += progress
    }

    override fun stopPlaying() {
        stopCalls++
    }

    override fun onProgressUpdate(progress: Long) {
        progressUpdates += progress
    }
}

class FakeMusicFoldersRepository(
    private val folders: List<MusicFolder> = emptyList(),
    private val tracksByPath: Map<String, List<Track>> = emptyMap()
) : MusicFoldersRepository {
    val requestedPaths = mutableListOf<String>()

    override suspend fun getMusicFolders() = folders
    override suspend fun getMusicFilesFromPath(path: String): List<Track> {
        requestedPaths += path
        return tracksByPath[path].orEmpty()
    }

    override fun getAlbumIconUrl(albumPath: String) = "art:$albumPath"
    override fun getArtistName(filePath: String) = ""
    override fun getTrackTitle(filePath: String) = ""
}
