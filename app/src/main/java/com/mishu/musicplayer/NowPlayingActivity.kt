package com.mishu.musicplayer

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NowPlayingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_now_playing)

        val titleView = findViewById<TextView>(R.id.nowTitle)
        val artistView = findViewById<TextView>(R.id.nowArtist)
        val btnPlay = findViewById<Button>(R.id.btnPlay)
        val btnNext = findViewById<Button>(R.id.btnNext)
        val btnPrev = findViewById<Button>(R.id.btnPrev)

        // Simple commands to the service via intents
        btnPlay.setOnClickListener {
            startService(Intent(this, MediaPlayerService::class.java).apply { action = MediaPlayerService.ACTION_TOGGLE_PLAY })
        }
        btnNext.setOnClickListener {
            startService(Intent(this, MediaPlayerService::class.java).apply { action = MediaPlayerService.ACTION_NEXT })
        }
        btnPrev.setOnClickListener {
            startService(Intent(this, MediaPlayerService::class.java).apply { action = MediaPlayerService.ACTION_PREV })
        }

        // Show currently playing info if available (service stores last known)
        val info = MediaPlayerService.currentSongInfo
        if (info != null) {
            titleView.text = info.title
            artistView.text = info.artist
        }
    }
}
