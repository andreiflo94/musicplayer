package com.example.musicplayer.di

import android.content.ContentResolver
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.musicplayer.data.MediaControllerManagerImpl
import com.example.musicplayer.data.ThemeRepositoryImpl
import com.example.musicplayer.domain.MediaControllerManager
import com.example.musicplayer.domain.repo.ThemeRepository
import com.example.musicplayer.musicplayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideMediaControllerManager(@ApplicationContext context: Context): MediaControllerManager {
        return MediaControllerManagerImpl(context)
    }

    @Provides
    @Singleton
    fun provideAndroidSqliteDriver(@ApplicationContext context: Context): AndroidSqliteDriver {
        return AndroidSqliteDriver(
            schema = musicplayer.Schema,
            context = context,
            name = "musicplayer.db",
            callback = object : AndroidSqliteDriver.Callback(musicplayer.Schema) {
                // SQLite leaves foreign keys off by default; needed for ON DELETE CASCADE
                override fun onConfigure(db: SupportSQLiteDatabase) {
                    super.onConfigure(db)
                    db.setForeignKeyConstraintsEnabled(true)
                }
            }
        )
    }

    @Provides
    @Singleton
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }

    @Provides
    @Singleton
    fun provideDatabase(driver: AndroidSqliteDriver): musicplayer {
        return musicplayer(driver)
    }

    @Provides
    @Singleton
    fun provideThemeRepository(@ApplicationContext context: Context): ThemeRepository {
        return ThemeRepositoryImpl(context)
    }

}