package com.mishu.musicplayer

import android.app.Application

class App : Application() {
    // simple in-memory holder; improve for production
    var mediaQueue: Pair<List<Song>, Int>? = null

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
    }
}
