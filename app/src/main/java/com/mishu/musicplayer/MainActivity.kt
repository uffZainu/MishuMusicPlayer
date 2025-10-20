package com.mishu.musicplayer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val songs = mutableListOf<Song>()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) loadSongs()
            else Toast.makeText(this, "Storage permission required to load songs", Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            loadSongs()
        } else {
            requestPermissionLauncher.launch(permission)
        }

        findViewById<View>(R.id.openNowPlaying)?.setOnClickListener {
            startActivity(Intent(this, NowPlayingActivity::class.java))
        }
    }

    private fun loadSongs() {
        songs.clear()
        val projection = arrayOf(
            android.provider.MediaStore.Audio.Media._ID,
            android.provider.MediaStore.Audio.Media.TITLE,
            android.provider.MediaStore.Audio.Media.ARTIST,
            android.provider.MediaStore.Audio.Media.DATA
        )
        val selection = "${android.provider.MediaStore.Audio.Media.IS_MUSIC}=1"
        val sortOrder = android.provider.MediaStore.Audio.Media.TITLE + " ASC"
        val uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor = contentResolver.query(uri, projection, selection, null, sortOrder)
        cursor?.use {
            val idCol = it.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media._ID)
            val titleCol = it.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.TITLE)
            val artistCol = it.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.ARTIST)
            val dataCol = it.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.DATA)
            while (it.moveToNext()) {
                val id = it.getLong(idCol)
                val title = it.getString(titleCol) ?: "Unknown"
                val artist = it.getString(artistCol) ?: "Unknown"
                val data = it.getString(dataCol)
                songs.add(Song(id, title, artist, data))
            }
        }
        val adapter = MusicAdapter(songs) { song ->
            MediaPlayerService.start(this, songs, songs.indexOf(song))
            startActivity(Intent(this, NowPlayingActivity::class.java))
        }
        recyclerView.adapter = adapter
        findViewById<View>(R.id.emptyView).visibility = if (songs.isEmpty()) View.VISIBLE else View.GONE
    }
}
