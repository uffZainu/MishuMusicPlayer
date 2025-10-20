package com.mishu.musicplayer

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class PlayerWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val views = RemoteViews(context.packageName, R.layout.widget_player)
        val intent = Intent(context, MediaPlayerService::class.java).apply { action = MediaPlayerService.ACTION_TOGGLE_PLAY }
        val p = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.widgetPlay, p)
        val cn = ComponentName(context, PlayerWidget::class.java)
        appWidgetManager.updateAppWidget(cn, views)
    }
}
