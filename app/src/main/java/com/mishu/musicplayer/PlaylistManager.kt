package com.mishu.musicplayer

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistManager(private val ctx: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
    private val gson = Gson()

    fun savePlaylist(name: String, songIds: List<Long>) {
        val json = gson.toJson(songIds)
        prefs.edit().putString("playlist_" + name, json).apply()
    }

    fun loadPlaylist(name: String): List<Long> {
        val json = prefs.getString("playlist_" + name, null) ?: return emptyList()
        val type = object : TypeToken<List<Long>>() {}.type
        return gson.fromJson(json, type)
    }

    fun listPlaylists(): List<String> {
        return prefs.all.keys.filter { it.startsWith("playlist_") }.map { it.removePrefix("playlist_") }
    }
}
