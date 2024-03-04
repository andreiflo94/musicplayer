package com.example.musicplayer.utils

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicplayer.PlaybackService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MediaControllerManager @Inject constructor(@ApplicationContext private val context: Context) {

    private lateinit var controllerFuture: ListenableFuture<MediaController>
    val controller: MediaController?
        get() = if (controllerFuture.isDone) controllerFuture.get() else null


    @UnstableApi
    fun init(controllerRunnable: Runnable) {
        controllerFuture = MediaController.Builder(
            context, SessionToken(context, ComponentName(context, PlaybackService::class.java))
        ).buildAsync()
        controllerFuture.addListener(controllerRunnable, MoreExecutors.directExecutor())
    }

    fun release() {
        controllerFuture.let {
            MediaController.releaseFuture(it)
        }
    }
}