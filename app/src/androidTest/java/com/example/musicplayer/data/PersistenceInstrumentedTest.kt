package com.example.musicplayer.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.musicplayer.di.ApplicationModule
import com.example.musicplayer.domain.model.AppTheme
import com.example.musicplayer.domain.model.DarkMode
import com.example.musicplayer.domain.model.ThemeSettings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** The production SQLite driver and SharedPreferences, as wired in ApplicationModule. */
@RunWith(AndroidJUnit4::class)
class PersistenceInstrumentedTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val module = ApplicationModule()

    @Before
    @After
    fun reset() {
        context.deleteDatabase(DB_NAME)
        context.getSharedPreferences("theme_settings", Context.MODE_PRIVATE).edit().clear().commit()
    }

    @Test
    fun appDriverEnforcesForeignKeysSoDeletesCascade() = runBlocking {
        val driver = module.provideAndroidSqliteDriver(context)
        try {
            val repository = PlaylistRepositoryImpl(module.provideDatabase(driver))
            repository.insertTrackToNewPlaylist("Chill", "A - One", "/m/1.mp3", null)
            val id = repository.getAllPlaylists().first().single().id
            assertEquals(1, repository.getTracksForPlaylist(id).first().size)

            repository.deletePlaylist(id)
            assertEquals(0L, countTracks(driver))
        } finally {
            driver.close()
        }
    }

    @Test
    fun v1DatabaseIsUpgradedOnOpen() = runBlocking {
        // A database as the first release created it
        SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(DB_NAME), null).use { db ->
            db.execSQL("CREATE TABLE Playlist (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL)")
            db.execSQL(
                "CREATE TABLE PlaylistTrack (playlist_id INTEGER NOT NULL, track_name TEXT NOT NULL, " +
                        "path TEXT NOT NULL PRIMARY KEY, art_path TEXT, " +
                        "FOREIGN KEY (playlist_id) REFERENCES Playlist(id) ON DELETE CASCADE)"
            )
            db.execSQL("INSERT INTO Playlist (id, name) VALUES (1, 'Old'), (2, 'Other')")
            db.execSQL("INSERT INTO PlaylistTrack VALUES (1, 'A - One', '/m/1.mp3', NULL)")
            db.execSQL("INSERT INTO PlaylistTrack VALUES (42, 'Orphan - Row', '/m/2.mp3', NULL)")
            db.version = 1
        }

        val driver = module.provideAndroidSqliteDriver(context)
        try {
            val repository = PlaylistRepositoryImpl(module.provideDatabase(driver))
            assertEquals(
                listOf("Old", "Other"),
                repository.getAllPlaylists().first().map { it.name })
            assertEquals(
                listOf("/m/1.mp3"),
                repository.getTracksForPlaylist(1).first().map { it.path })
            assertEquals(1L, countTracks(driver))

            repository.insertTrack(2, "A - One", "/m/1.mp3", null)
            assertEquals(1, repository.getTracksForPlaylist(2).first().size)
        } finally {
            driver.close()
        }
    }

    @Test
    fun themeSettingsPersistAcrossInstances() {
        assertEquals(
            ThemeSettings(AppTheme.AURORA, DarkMode.SYSTEM),
            ThemeRepositoryImpl(context).settings.value
        )

        ThemeRepositoryImpl(context).apply {
            setTheme(AppTheme.FOREST)
            setDarkMode(DarkMode.LIGHT)
            assertEquals(ThemeSettings(AppTheme.FOREST, DarkMode.LIGHT), settings.value)
        }

        assertEquals(
            ThemeSettings(AppTheme.FOREST, DarkMode.LIGHT),
            ThemeRepositoryImpl(context).settings.value
        )
    }

    @Test
    fun unknownStoredThemeFallsBackToDefaults() {
        context.getSharedPreferences("theme_settings", Context.MODE_PRIVATE).edit()
            .putString("theme", "REMOVED_THEME").putString("dark_mode", "sepia").commit()

        assertEquals(ThemeSettings(), ThemeRepositoryImpl(context).settings.value)
    }

    private fun countTracks(driver: app.cash.sqldelight.db.SqlDriver): Long =
        driver.executeQuery(
            null, "SELECT COUNT(*) FROM PlaylistTrack",
            { c -> c.next(); app.cash.sqldelight.db.QueryResult.Value(c.getLong(0)!!) }, 0
        ).value

    private companion object {
        const val DB_NAME = "musicplayer.db"
    }
}
