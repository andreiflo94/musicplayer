package com.example.musicplayer.data

import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.provider.MediaStore
import android.test.mock.MockContentProvider
import android.test.mock.MockContentResolver
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.musicplayer.writeSilentWav
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Runs the real repository against real files and a fake MediaStore provider, so folder layout
 * edge cases don't depend on whatever music is on the device.
 */
@RunWith(AndroidJUnit4::class)
class MusicFoldersRepositoryInstrumentedTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var root: File
    private lateinit var repository: MusicFoldersRepositoryImpl
    private val rows = mutableListOf<Row>()

    private data class Row(
        val path: String,
        val artist: String,
        val title: String,
        val albumId: Long
    )

    /** Answers the handful of selections the app uses: `_data = ?`, `_data LIKE ?`, joined by AND/OR. */
    private inner class FakeMediaStore : MockContentProvider(context) {
        override fun query(
            uri: Uri, projection: Array<String>?, selection: String?,
            selectionArgs: Array<String>?, sortOrder: String?
        ): Cursor {
            val clauses = Regex("""_data (=|LIKE) \?""").findAll(selection.orEmpty())
                .mapIndexed { i, m ->
                    val arg = selectionArgs!![i]
                    if (m.groupValues[1] == "=") { p: String -> p == arg }
                    else { p: String -> p.startsWith(arg.removeSuffix("%")) }
                }.toList()
            val matches = rows.filter { row ->
                when {
                    clauses.isEmpty() -> true
                    selection!!.contains(" OR ") -> clauses.any { it(row.path) }
                    else -> clauses.all { it(row.path) }
                }
            }
            val columns = projection ?: arrayOf(MediaStore.Audio.Media.DATA)
            return MatrixCursor(columns).apply {
                matches.forEach { row ->
                    addRow(columns.map {
                        when (it) {
                            MediaStore.Audio.Media.DATA -> row.path
                            MediaStore.Audio.Media.ARTIST -> row.artist
                            MediaStore.Audio.Media.TITLE -> row.title
                            MediaStore.Audio.Media.ALBUM_ID -> row.albumId
                            else -> null
                        }
                    })
                }
            }
        }
    }

    private fun addTrack(
        relative: String,
        artist: String = "Artist",
        title: String = relative,
        albumId: Long = 1
    ): File {
        val file = writeSilentWav(File(root, relative), 100)
        rows += Row(file.path, artist, title, albumId)
        return file
    }

    @Before
    fun setUp() {
        root = File(context.cacheDir, "library").apply { deleteRecursively(); mkdirs() }
        val resolver = MockContentResolver(context).apply {
            addProvider(MediaStore.AUTHORITY, FakeMediaStore())
        }
        repository = MusicFoldersRepositoryImpl(resolver)
    }

    @After
    fun tearDown() {
        root.deleteRecursively()
    }

    @Test
    fun foldersAreListedOncePerDirectory() = runBlocking {
        addTrack("Rock/a.wav", artist = "Band")
        addTrack("Rock/b.wav")
        addTrack("Jazz/c.wav")

        val folders = repository.getMusicFolders()

        assertEquals(listOf("Rock", "Jazz"), folders.map { it.name })
        assertEquals(File(root, "Rock").path, folders[0].path)
    }

    @Test
    fun folderTracksExcludeSiblingFoldersSharingThePrefixAndSubfolders() = runBlocking {
        val inRock = addTrack("Rock/a.wav", artist = "Band", title = "Song A")
        addTrack("Rock Classics/b.wav")
        addTrack("Rock/Live/c.wav")

        val tracks = repository.getMusicFilesFromPath(File(root, "Rock").path)

        assertEquals(listOf(inRock.path), tracks.map { it.path })
        assertEquals("Band", tracks[0].artistName)
        assertEquals("Song A", tracks[0].trackName)
    }

    @Test
    fun missingFolderHasNoTracks() = runBlocking {
        assertEquals(0, repository.getMusicFilesFromPath(File(root, "nope").path).size)
    }

    @Test
    fun coverFileInTheFolderIsUsedForTheFolderAndItsTracks() {
        val track = addTrack("Album/a.wav")
        val cover = File(root, "Album/cover.jpg").apply { writeBytes(byteArrayOf(1)) }

        assertEquals(cover.absolutePath, repository.getAlbumIconUrl(File(root, "Album").path))
        assertEquals(cover.absolutePath, repository.getAlbumIconUrl(track.path))
    }

    @Test
    fun mediaStoreArtComesFromTheFolderItselfNotASiblingWithTheSamePrefix() {
        addTrack("Rock Classics/b.wav", albumId = 2)
        val track = addTrack("Rock/a.wav", albumId = 7)

        val expected = "content://media/external/audio/albumart/7"
        assertEquals(expected, repository.getAlbumIconUrl(File(root, "Rock").path))
        assertEquals(expected, repository.getAlbumIconUrl(track.path))
        assertEquals("", repository.getAlbumIconUrl(File(root, "Empty").path))
    }
}
