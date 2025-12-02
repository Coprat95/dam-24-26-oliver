package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide

class HomeActivity : AppCompatActivity() {

    private lateinit var miniPlayerContainer: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        PlaylistManager.cargar(this)
        // ¡LA SOLUCIÓN! Prepara la última canción al iniciar la app.
        MusicPlayerManager.prepareLastSong(this)

        val cardAllSongs = findViewById<CardView>(R.id.cardAllSongs)
        val cardPlaylists = findViewById<CardView>(R.id.cardPlaylists)
        val fabCreatePlaylist = findViewById<ImageButton>(R.id.fabCreatePlaylist)
        miniPlayerContainer = findViewById(R.id.miniPlayer)

        cardAllSongs.setOnClickListener {
            val intent = Intent(this, AllSongsActivity::class.java)
            startActivity(intent)
        }

        cardPlaylists.setOnClickListener {
            val intent = Intent(this, PlaylistsActivity::class.java)
            startActivity(intent)
        }

        fabCreatePlaylist.setOnClickListener {
            mostrarDialogoCrearPlaylist()
        }

        setupMiniPlayer()
        setupObservers()
    }

    private fun setupMiniPlayer() {
        val miniPlayerPlay = findViewById<ImageButton>(R.id.miniPlayerPlay)
        val miniPlayerNext = findViewById<ImageButton>(R.id.miniPlayerNext)

        miniPlayerPlay.setOnClickListener { MusicPlayerManager.playPause(this) }
        miniPlayerNext.setOnClickListener { MusicPlayerManager.next(this) }

        miniPlayerContainer.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupObservers() {
        MusicPlayerManager.currentSong.observe(this) { song ->
            if (song != null) {
                miniPlayerContainer.visibility = View.VISIBLE
                updateMiniPlayerUI(song)
            } else {
                miniPlayerContainer.visibility = View.GONE
            }
        }

        MusicPlayerManager.isPlaying.observe(this) { isPlaying ->
            val miniPlayerPlay = findViewById<ImageButton>(R.id.miniPlayerPlay)
            if (isPlaying) {
                miniPlayerPlay.setImageResource(R.drawable.stop_mini)
            } else {
                miniPlayerPlay.setImageResource(R.drawable.play_mini)
            }
        }
    }

    private fun updateMiniPlayerUI(song: InfoCancion) {
        val miniPlayerTitle = findViewById<TextView>(R.id.miniPlayerTitle)
        val miniPlayerArtist = findViewById<TextView>(R.id.miniPlayerArtist)
        val miniPlayerCover = findViewById<ImageView>(R.id.miniPlayerCover)

        miniPlayerTitle.text = song.titulo
        
        // Oculta el artista si es desconocido o nulo
        if (song.artista.isNullOrEmpty() || song.artista.equals("<unknown>", ignoreCase = true)) {
            miniPlayerArtist.visibility = View.GONE
        } else {
            miniPlayerArtist.visibility = View.VISIBLE
            miniPlayerArtist.text = song.artista
        }

        if (song.customImageUriString != null) {
            Glide.with(this).load(song.customImageUriString).into(miniPlayerCover)
        } else {
            miniPlayerCover.setImageResource(R.drawable.nota_musical)
        }
    }

    private fun mostrarDialogoCrearPlaylist() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Nueva Playlist")
        builder.setMessage("Nombre de la lista:")
        val input = EditText(this)
        builder.setView(input)
        builder.setPositiveButton("Crear") { _, _ ->
            val nombre = input.text.toString()
            if (nombre.isNotEmpty()) {
                PlaylistManager.crearPlaylist(this, nombre)
                Toast.makeText(this, "Playlist '$nombre' creada", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }
}