package com.mishu.musicplayer

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class SleepTimer(private val ctx: Context) {
    fun scheduleMinutes(minutes: Long) {
        val req = OneTimeWorkRequestBuilder<SleepWorker>()
            .setInitialDelay(minutes, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(ctx).enqueue(req)
    }
}

class SleepWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        // send stop action to service
        val intent = android.content.Intent(applicationContext, MediaPlayerService::class.java).apply { action = "stop" }
        applicationContext.startService(intent)
        return Result.success()
    }
}
