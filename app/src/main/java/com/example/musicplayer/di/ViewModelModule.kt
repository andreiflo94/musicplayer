package com.example.musicplayer.di

import android.content.ContentResolver
import androidx.lifecycle.SavedStateHandle
import com.example.musicplayer.data.MusicFoldersRepositoryImpl
import com.example.musicplayer.data.PlaylistRepositoryImpl
import com.example.musicplayer.domain.repo.MusicFoldersRepository
import com.example.musicplayer.domain.repo.PlaylistRepository
import com.example.musicplayer.musicplayer
import com.example.musicplayer.presentation.viewmodels.MusicFoldersViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {


    @Provides
    fun providePlaylistRepository(database: musicplayer): PlaylistRepository {
        return PlaylistRepositoryImpl(database)
    }

    @Provides
    fun provideMusicFoldersRepository(contentResolver: ContentResolver): MusicFoldersRepository {
        return MusicFoldersRepositoryImpl(contentResolver)
    }
}
