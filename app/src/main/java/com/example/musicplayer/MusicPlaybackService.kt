package com.example.musicplayer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.example.musicplayer.mainfeature.presentation.ui.screens.AudioFileState
import java.io.IOException

class MusicPlaybackService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "music_channel"
        const val EXTRA_AUDIO_FILE_PATH = "extra_audio_file_path"
        const val STOP_SERVICE_ACTION = "STOP_SERVICE_ACTION"
        const val PLAY_PAUSE_ACTION = "PLAY_PAUSE_ACTION"
        const val PLAY_NEW_TRACK_ACTION = "PLAY_ACTION"
    }

    private lateinit var mediaPlayer: MediaPlayer
    private var audioFilePath: String = ""
    private lateinit var mediaSession: MediaSessionCompat
    private var countDownTimer: CountDownTimer? = null
    private var audioFileState: AudioFileState = AudioFileState.IDLE

    override fun onCreate() {
        super.onCreate()
        Log.d("MusicPlaybackService", "onCreate")

        initMediaPlayer()
        initMediaSession()

        createNotificationChannel()
        updateNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MusicPlaybackService", "onStartCommand")
        // Handle the action to stop the service triggered by deleteIntent
        if (intent?.action == STOP_SERVICE_ACTION) {
            audioFileState = AudioFileState.STOPED
            stopMediaPlayer()
            stopSelf()
        } else if (intent?.action == PLAY_PAUSE_ACTION) {
            if (audioFilePath.isNotBlank()) {
                // Handle play/pause action
                if (mediaPlayer.isPlaying) {
                    audioFileState = AudioFileState.PAUSED
                    pauseMediaPlayer()
                } else {
                    audioFileState = AudioFileState.PLAYING
                    if (mediaPlayer.currentPosition > 0) {
                        resumeMediaPlayer()
                    } else {
                        startMediaPlayer()
                    }
                }
            }
        } else if (intent?.action == PLAY_NEW_TRACK_ACTION && intent.hasExtra(EXTRA_AUDIO_FILE_PATH)) {
            audioFilePath = intent.getStringExtra(EXTRA_AUDIO_FILE_PATH) ?: ""
            stopMediaPlayer()
            startMediaPlayer()
            audioFileState = AudioFileState.PLAYING
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.d("MusicPlaybackService", "onDestroy" + audioFilePath)
        updateMusicPlaybackStatus(mediaPlayer.isPlaying)
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun initMediaPlayer(){
        mediaPlayer = MediaPlayer()

        // Set up media player settings
        mediaPlayer.setOnPreparedListener {
            Log.d("MusicPlaybackService", "setOnPreparedListener")
            mediaPlayer.start()
            updateNotification()
            setupTimerForPlaybackProgress()
            startTimerForPlaybackProgress()
        }

        mediaPlayer.setOnCompletionListener {
            Log.d("MusicPlaybackService", "setOnCompletionListener")
            stopTimerForPlaybackProgress()
            stopSelf()
        }
    }

    private fun initMediaSession(){
        // Initialize MediaSessionCompat
        mediaSession = MediaSessionCompat(this, "MusicPlayerSession")
        mediaSession.isActive = true
    }

    private fun startMediaPlayer() {
        try {
            Log.d("MusicPlaybackService", "startMediaPlayer" + audioFilePath)
            mediaPlayer.setDataSource(audioFilePath)
            mediaPlayer.prepareAsync()

        } catch (e: IOException) {
            Log.d("MusicPlaybackService", "IOException" + e.message)
            stopSelf()
        }
    }

    private fun pauseMediaPlayer() {
        if (mediaPlayer.isPlaying) {
            Log.d("MusicPlaybackService", "pauseMediaPlayer" + audioFilePath)
            mediaPlayer.pause()
            updateNotification()
            stopTimerForPlaybackProgress()
        }
    }

    private fun resumeMediaPlayer() {
        Log.d("MusicPlaybackService", "resume" + audioFilePath)
        startTimerForPlaybackProgress()
        mediaPlayer.start()
        updateNotification()
    }

    private fun stopMediaPlayer() {
        stopTimerForPlaybackProgress()
        mediaPlayer.stop()
        mediaPlayer.reset()
    }

    private fun updateNotification() {
        Log.d("MusicPlaybackService", "updateNotification")

        // Create pending intent for the play/pause action
        val playPauseIntent = Intent(this, MusicPlaybackService::class.java)
        playPauseIntent.action = PLAY_PAUSE_ACTION
        playPauseIntent.putExtra(EXTRA_AUDIO_FILE_PATH, audioFilePath)
        val pendingPlayPauseIntent = PendingIntent.getService(
            this,
            0,
            playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create a PendingIntent for the close button to stop the service
        val closeIntent = Intent(this, MusicPlaybackService::class.java)
        closeIntent.action = STOP_SERVICE_ACTION
        val pendingCloseIntent = PendingIntent.getService(
            this,
            0,
            closeIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        // Create a notification with play/pause action
        val playPauseActionText = if (mediaPlayer.isPlaying) "Pause" else "Play"
        val playPauseIcon =
            if (mediaPlayer.isPlaying) R.drawable.ic_not_pause else R.drawable.ic_not_play

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music Player")
            .setContentText("Playing music")
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_music_note)
            .addAction(playPauseIcon, playPauseActionText, pendingPlayPauseIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1)
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            this,
                            PlaybackStateCompat.ACTION_STOP
                        )
                    )
            )

        val closeAction = NotificationCompat.Action.Builder(
            R.drawable.ic_not_close,
            "Close",
            pendingCloseIntent
        ).build()
        notificationBuilder.addAction(closeAction)

        val notification = notificationBuilder.build()
        startForeground(NOTIFICATION_ID, notification)
        updateMusicPlaybackStatus(mediaPlayer.isPlaying)
    }

    private fun createNotificationChannel() {
        Log.d("MusicPlaybackService", "createNotificationChannel")
        // Create a notification channel if Android version is Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Player Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun updateMusicPlaybackStatus(isPlaying: Boolean) {
        Log.d("MusicPlaybackService", "updateMusicPlaybackStatus " + isPlaying)
        val intent = Intent(MusicBroadcastReceiver.UPDATE_PLAYBACK_STATUS)
        intent.putExtra(MusicBroadcastReceiver.AUDIO_STATE, audioFileState.name)
        sendBroadcast(intent)
    }

    private fun updateMusicPlaybackProgress(progress: Int, totalDuration: Int) {
        Log.d("MusicPlaybackService", "updateMusicPlaybackProgress " + progress)
        val intent = Intent(MusicBroadcastReceiver.UPDATE_PLAYBACK_STATUS)
        intent.putExtra(MusicBroadcastReceiver.PLAYBACK_PROGRESS, progress)
        intent.putExtra(MusicBroadcastReceiver.PLAYBACK_TOTAL_DURATION, totalDuration)
        sendBroadcast(intent)
    }

    private fun setupTimerForPlaybackProgress(){
        val totalTimeInMillis: Int = mediaPlayer.duration // Total time in milliseconds (e.g., 1 minute)
        val intervalInMillis: Long = 1000 // Interval in milliseconds (e.g., 1 second)

        countDownTimer = object : CountDownTimer(totalTimeInMillis.toLong(), intervalInMillis) {
            override fun onTick(millisUntilFinished: Long) {
                val currentPosition = mediaPlayer.currentPosition

                // Calculate progress percentage
                val progress = (currentPosition.toFloat() / totalTimeInMillis.toFloat()) * 100

                updateMusicPlaybackProgress(progress.toInt(), totalTimeInMillis)
            }

            override fun onFinish() {
                // This method will be called when the timer finishes
                // Perform actions when the timer finishes
            }
        }
    }

    private fun startTimerForPlaybackProgress(){
        countDownTimer?.start()
    }

    private fun stopTimerForPlaybackProgress(){
        countDownTimer?.cancel()
    }
}
