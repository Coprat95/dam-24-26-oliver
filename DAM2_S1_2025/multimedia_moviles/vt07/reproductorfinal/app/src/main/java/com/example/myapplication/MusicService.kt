package com.example.myapplication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

class MusicService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private lateinit var mediaSession: MediaSessionCompat
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "com.example.myapplication.MUSIC_CHANNEL"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        mediaSession = MediaSessionCompat(this, "MusicService").apply {
            setCallback(mediaSessionCallback)
            isActive = true
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleIntent(intent)
        return START_NOT_STICKY
    }

    private fun handleIntent(intent: Intent?) {
        val action = intent?.action ?: return
        when (action) {
            ACTION_PLAY -> MusicPlayerManager.currentSong.value?.let { prepareAndPlay(it) }
            ACTION_PAUSE -> handlePause()
            ACTION_NEXT -> MusicPlayerManager.onSongFinished(this)
            ACTION_PREVIOUS -> MusicPlayerManager.previous(this)
            ACTION_SEEK_TO -> mediaPlayer?.seekTo(intent.getIntExtra(EXTRA_SEEK_POSITION, 0))
            ACTION_STOP -> stopService()
        }
    }

    private fun prepareAndPlay(song: InfoCancion) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setOnPreparedListener(this@MusicService)
            setOnErrorListener(this@MusicService)
            setOnCompletionListener(this@MusicService)
            try {
                setDataSource(applicationContext, Uri.parse(song.audioUriString))
                prepareAsync()
            } catch (e: Exception) {
                Log.e("MusicService", "Error setting data source for ${song.titulo}", e)
                MusicPlayerManager.onSongFinished(this@MusicService)
            }
        }
    }

    private fun handlePause() {
        mediaPlayer?.pause()
        updatePlaybackState(PlaybackStateCompat.STATE_PAUSED)
        MusicPlayerManager.updateIsPlaying(false)
        stopForeground(false)
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()
        updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
        MusicPlayerManager.updateIsPlaying(true)
        MusicPlayerManager.updateDuration(mp?.duration ?: 0)
        handler.post(progressUpdater)
        MusicPlayerManager.currentSong.value?.let { updateNotification(it, true) }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        updatePlaybackState(PlaybackStateCompat.STATE_STOPPED)
        MusicPlayerManager.onSongFinished(this)
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.e("MusicService", "MediaPlayer error - what: $what, extra: $extra")
        updatePlaybackState(PlaybackStateCompat.STATE_ERROR)
        MusicPlayerManager.onSongFinished(this)
        return true
    }

    private val progressUpdater = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    MusicPlayerManager.updateProgress(it.currentPosition)
                    handler.postDelayed(this, 1000)
                }
            }
        }
    }

    private fun updatePlaybackState(state: Int) {
        val position = mediaPlayer?.currentPosition?.toLong() ?: 0L
        val playbackState = PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or PlaybackStateCompat.ACTION_SEEK_TO)
            .setState(state, position, 1.0f)
            .build()
        mediaSession.setPlaybackState(playbackState)
    }

    private fun updateNotification(song: InfoCancion, isPlaying: Boolean) {
        val metadataBuilder = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.titulo)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artista)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer?.duration?.toLong() ?: 0)

        mediaSession.setMetadata(metadataBuilder.build())

        Glide.with(this).asBitmap().load(song.customImageUriString ?: song.fotoResId).error(R.drawable.nota_musical).into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, resource)
                mediaSession.setMetadata(metadataBuilder.build())
                startForeground(NOTIFICATION_ID, buildNotification(isPlaying))
            }
            override fun onLoadCleared(placeholder: Drawable?) {
                startForeground(NOTIFICATION_ID, buildNotification(isPlaying))
            }
        })
    }

    private fun buildNotification(isPlaying: Boolean): Notification {
        val controller = mediaSession.controller
        val mediaMetadata = controller.metadata

        val playPauseAction = NotificationCompat.Action(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow, if (isPlaying) "Pause" else "Play", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE))
        val nextAction = NotificationCompat.Action(R.drawable.siguiente, "Next", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT))
        val prevAction = NotificationCompat.Action(R.drawable.anterior, "Previous", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))

        return NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle(mediaMetadata.description.title)
            setContentText(mediaMetadata.description.subtitle)
            setLargeIcon(mediaMetadata.description.iconBitmap)
            setContentIntent(MusicPlayerManager.createContentIntent(this@MusicService))
            setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this@MusicService, PlaybackStateCompat.ACTION_STOP))
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setSmallIcon(R.drawable.nota_musical)
            addAction(prevAction)
            addAction(playPauseAction)
            addAction(nextAction)
            setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken).setShowActionsInCompactView(0, 1, 2))
        }.build()
    }

    private fun stopService() {
        stopForeground(true)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Music Player", NotificationManager.IMPORTANCE_DEFAULT)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopService()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(progressUpdater)
        mediaPlayer?.release()
        mediaSession.release()
    }

    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPlay() { handleIntent(Intent(this@MusicService, MusicService::class.java).setAction(ACTION_PLAY)) }
        override fun onPause() { handleIntent(Intent(this@MusicService, MusicService::class.java).setAction(ACTION_PAUSE)) }
        override fun onSkipToNext() { handleIntent(Intent(this@MusicService, MusicService::class.java).setAction(ACTION_NEXT)) }
        override fun onSkipToPrevious() { handleIntent(Intent(this@MusicService, MusicService::class.java).setAction(ACTION_PREVIOUS)) }
        override fun onSeekTo(pos: Long) { mediaPlayer?.seekTo(pos.toInt()) }
        override fun onStop() { stopService() }
    }

    companion object {
        const val ACTION_PLAY = "action_play"
        const val ACTION_PAUSE = "action_pause"
        const val ACTION_NEXT = "action_next"
        const val ACTION_PREVIOUS = "action_previous"
        const val ACTION_STOP = "action_stop"
        const val ACTION_SEEK_TO = "action_seek_to"
        const val EXTRA_SEEK_POSITION = "extra_seek_position"
    }
}