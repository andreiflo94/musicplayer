package com.example.musicplayer.data

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.musicplayer.musicplayer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Properties

/** Upgrades a database created by the original (v1) schema with 1.sqm. */
class SchemaMigrationTest {

    private lateinit var driver: JdbcSqliteDriver

    @Before
    fun setUp() {
        driver = JdbcSqliteDriver(
            JdbcSqliteDriver.IN_MEMORY,
            Properties().apply { put("foreign_keys", "true") }
        )
        // v1 schema, as first shipped: `path` alone was the primary key
        driver.execute(
            null,
            "CREATE TABLE Playlist (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL)",
            0
        )
        driver.execute(
            null,
            """
            CREATE TABLE PlaylistTrack (
                playlist_id INTEGER NOT NULL,
                track_name TEXT NOT NULL,
                path TEXT NOT NULL PRIMARY KEY,
                art_path TEXT,
                FOREIGN KEY (playlist_id) REFERENCES Playlist(id) ON DELETE CASCADE
            )
            """.trimIndent(),
            0
        )
    }

    @After
    fun tearDown() = driver.close()

    private fun exec(sql: String) = driver.execute(null, sql, 0)

    @Test
    fun `schema version counts the migration files`() {
        assertEquals(2L, musicplayer.Schema.version)
    }

    @Test
    fun `migration keeps tracks, drops orphans and allows a track in several playlists`() {
        exec("INSERT INTO Playlist (id, name) VALUES (1, 'Chill'), (2, 'Gym')")
        // Foreign keys were off in v1, so rows of deleted playlists were left behind
        exec("PRAGMA foreign_keys = OFF")
        exec("INSERT INTO PlaylistTrack VALUES (1, 'A - One', '/m/1.mp3', NULL)")
        exec("INSERT INTO PlaylistTrack VALUES (1, 'B - Two', '/m/2.mp3', '/m/cover.jpg')")
        exec("INSERT INTO PlaylistTrack VALUES (99, 'Gone - Orphan', '/m/3.mp3', NULL)")
        exec("PRAGMA foreign_keys = ON")

        musicplayer.Schema.migrate(driver, 1, 2)

        val repository = PlaylistRepositoryImpl(musicplayer(driver))
        val chill = runBlocking { repository.getTracksForPlaylist(1).first() }
        assertEquals(listOf("/m/1.mp3", "/m/2.mp3"), chill.map { it.path })
        assertEquals("/m/cover.jpg", chill[1].albumIconUrl)
        assertEquals(2L, count("SELECT COUNT(*) FROM PlaylistTrack"))

        // Impossible in v1: the same file in a second playlist
        repository.insertTrack(2, "A - One", "/m/1.mp3", null)
        assertEquals(1, runBlocking { repository.getTracksForPlaylist(2).first() }.size)

        // The rebuilt table still cascades
        repository.deletePlaylist(1)
        assertEquals(1L, count("SELECT COUNT(*) FROM PlaylistTrack"))
    }

    private fun count(sql: String): Long =
        driver.executeQuery(
            null,
            sql,
            { c -> c.next(); QueryResult.Value(c.getLong(0)!!) },
            0
        ).value
}
