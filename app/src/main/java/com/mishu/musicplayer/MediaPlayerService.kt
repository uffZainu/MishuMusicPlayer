package com.mishu.musicplayer

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import androidx.media.session.MediaSessionCompat
import android.support.v4.media.session.MediaSessionCompat as MediaSessionCompatSupport

class MediaPlayerService : Service() {

    private val binder = LocalBinder()
    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSessionCompat
    private var queue: List<Song> = emptyList()
    private var index: Int = 0

    companion object {
        var currentSongInfo: Song? = null

        const val ACTION_TOGGLE_PLAY = "action_toggle_play"
        const val ACTION_NEXT = "action_next"
        const val ACTION_PREV = "action_prev"

        fun start(context: Context, songs: List<Song>, startIndex: Int = 0) {
            val intent = Intent(context, MediaPlayerService::class.java)
            intent.action = "start"                context.startService(intent)
            // save queue in a simple singleton (for demo). A robust app would persist or bind properly.
            (context.applicationContext as? App)?.mediaQueue = Pair(songs, startIndex)
        }
    }

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSessionCompat(this, "MishuMediaSession")
        // restore queue if set on application context
        val app = applicationContext as? App
        val mq = app?.mediaQueue
        if (mq != null) {
            queue = mq.first
            index = mq.second
            prepareAndPlay(index)
        }
    }

    private fun prepareAndPlay(i: Int) {
        if (queue.isEmpty()) return
        index = i.coerceIn(queue.indices)
        val song = queue[index]
        player.setMediaItem(MediaItem.fromUri(song.data))
        player.prepare()
        player.play()
        currentSongInfo = song
        startForeground(1, buildNotification(song))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_NEXT -> next()
            ACTION_PREV -> prev()
            ACTION_TOGGLE_PLAY -> {
                if (player.isPlaying) player.pause() else player.play()
            }
            else -> {
                // started normally, nothing else
            }
        }
        return START_STICKY
    }

    private fun next() {
        if (queue.isEmpty()) return
        val nextIndex = (index + 1) % queue.size
        prepareAndPlay(nextIndex)
    }

    private fun prev() {
        if (queue.isEmpty()) return
        val prevIndex = if (index - 1 < 0) queue.size - 1 else index - 1
        prepareAndPlay(prevIndex)
    }

    private fun buildNotification(song: Song): Notification {
        val playIntent = Intent(this, MediaPlayerService::class.java).apply { action = ACTION_TOGGLE_PLAY }
        val nextIntent = Intent(this, MediaPlayerService::class.java).apply { action = ACTION_NEXT }
        val prevIntent = Intent(this, MediaPlayerService::class.java).apply { action = ACTION_PREV }

        val pPlay = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val pNext = PendingIntent.getService(this, 1, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val pPrev = PendingIntent.getService(this, 2, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, NotificationHelper.CHANNEL_ID)
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .setSmallIcon(R.drawable.ic_music_note)
            .addAction(R.drawable.ic_prev, "Prev", pPrev)
            .addAction(R.drawable.ic_play, "Play", pPlay)
            .addAction(R.drawable.ic_next, "Next", pNext)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        stopForeground(true)
    }

    inner class LocalBinder : Binder() {
        fun getService(): MediaPlayerService = this@MediaPlayerService
    }
}
