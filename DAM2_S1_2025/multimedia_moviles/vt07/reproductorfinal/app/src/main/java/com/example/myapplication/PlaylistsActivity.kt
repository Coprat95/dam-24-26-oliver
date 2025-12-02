package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PlaylistsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlaylistAdapter
    private var playlists = mutableListOf<Pair<String, ArrayList<InfoCancion>>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlists)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        val fabCreatePlaylist = findViewById<ImageButton>(R.id.fabCreatePlaylistFromPlaylists)
        fabCreatePlaylist.setOnClickListener { showCreatePlaylistDialog() }

        recyclerView = findViewById(R.id.recyclerViewPlaylists)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = PlaylistAdapter(
            playlists,
            onPlaylistClick = { playlist ->
                val intent = Intent(this, PlaylistDetailActivity::class.java)
                intent.putExtra("PLAYLIST_NAME", playlist.first)
                startActivity(intent)
            },
            onPlaylistPlayClick = { playlist ->
                if (playlist.second.isNotEmpty()) {
                    val position = if (MusicPlayerManager.isShuffle.value == true) (0 until playlist.second.size).random() else 0
                    MusicPlayerManager.play(this, playlist.second, position)
                    val intent = Intent(this, PlaylistDetailActivity::class.java)
                    intent.putExtra("PLAYLIST_NAME", playlist.first)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "La playlist está vacía", Toast.LENGTH_SHORT).show()
                }
            },
            onOptionsMenuClick = { playlist, view ->
                showPlaylistOptions(playlist, view)
            }
        )
        recyclerView.adapter = adapter

        loadPlaylists()
    }

    override fun onResume() {
        super.onResume()
        loadPlaylists()
    }

    private fun loadPlaylists() {
        PlaylistManager.cargar(this)
        playlists.clear()
        playlists.addAll(PlaylistManager.playlists.toList())
        adapter.notifyDataSetChanged()

        val tvEmpty = findViewById<View>(R.id.tvEmptyPlaylists)
        tvEmpty.visibility = if (playlists.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showCreatePlaylistDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Nueva Playlist")
        val input = EditText(this)
        builder.setView(input)
        builder.setPositiveButton("Crear") { _, _ ->
            val playlistName = input.text.toString()
            if (playlistName.isNotEmpty()) {
                PlaylistManager.crearPlaylist(this, playlistName)
                loadPlaylists()
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun showPlaylistOptions(playlist: Pair<String, ArrayList<InfoCancion>>, view: View) {
        val popup = PopupMenu(this, view)
        popup.menu.add("Renombrar")
        popup.menu.add("Eliminar")
        popup.setOnMenuItemClickListener { item ->
            when (item.title) {
                "Renombrar" -> {
                    showRenamePlaylistDialog(playlist)
                    true
                }
                "Eliminar" -> {
                    PlaylistManager.playlists.remove(playlist.first)
                    PlaylistManager.guardar(this)
                    loadPlaylists()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showRenamePlaylistDialog(playlist: Pair<String, ArrayList<InfoCancion>>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Renombrar Playlist")
        val input = EditText(this)
        input.setText(playlist.first)
        builder.setView(input)
        builder.setPositiveButton("Guardar") { _, _ ->
            val newName = input.text.toString()
            if (newName.isNotEmpty()) {
                val songs = PlaylistManager.playlists.remove(playlist.first)
                if (songs != null) {
                    PlaylistManager.playlists[newName] = songs
                    PlaylistManager.guardar(this)
                    loadPlaylists()
                }
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }
}
