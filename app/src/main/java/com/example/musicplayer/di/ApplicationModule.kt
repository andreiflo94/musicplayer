package com.example.musicplayer.di

import android.content.ContentResolver
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.example.musicplayer.mainfeature.data.MediaControllerManagerImpl
import com.example.musicplayer.mainfeature.domain.MediaControllerManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideMediaControllerManager(@ApplicationContext context: Context): MediaControllerManager {
        return MediaControllerManagerImpl(context)
    }
}