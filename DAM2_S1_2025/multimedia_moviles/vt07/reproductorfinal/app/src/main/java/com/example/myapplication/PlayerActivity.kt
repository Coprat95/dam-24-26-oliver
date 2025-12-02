package com.example.myapplication

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback

class PlayerActivity : AppCompatActivity() {

    private lateinit var textoTitulo: TextView
    private lateinit var imagenCancion: ImageView
    private lateinit var seekBar: SeekBar
    private lateinit var tiempoActual: TextView
    private lateinit var tiempoTotal: TextView
    private lateinit var buttonPlay: ImageButton
    private lateinit var buttonShuffle: ImageButton
    private lateinit var buttonRepeat: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        setupTransitions()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        textoTitulo = findViewById(R.id.textoTitulo)
        imagenCancion = findViewById(R.id.imagenCancion)
        seekBar = findViewById(R.id.seekBar)
        tiempoActual = findViewById(R.id.tiempoActual)
        tiempoTotal = findViewById(R.id.tiempoTotal)
        buttonPlay = findViewById(R.id.buttonPlay)
        buttonShuffle = findViewById(R.id.buttonShuffle)
        buttonRepeat = findViewById(R.id.buttonRepeat)

        setupUI()
        setupObservers()
        setupListeners()
    }

    private fun setupTransitions() {
        window.requestFeature(android.view.Window.FEATURE_ACTIVITY_TRANSITIONS)
        findViewById<View>(android.R.id.content).transitionName = "shared_element_container"
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 300L
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 300L
        }
    }

    private fun setupUI() {
        val song = MusicPlayerManager.currentSong.value
        if (song != null) {
            textoTitulo.text = song.titulo
            if (song.customImageUriString != null) {
                Glide.with(this).load(song.customImageUriString).into(imagenCancion)
            } else {
                imagenCancion.setImageResource(R.drawable.nota_musical)
            }
        } else {
            finish() // Cierra si no hay canción
        }
    }

    private fun setupObservers() {
        MusicPlayerManager.currentSong.observe(this) { song ->
            textoTitulo.text = song?.titulo ?: ""
            if (song?.customImageUriString != null) {
                Glide.with(this).load(song.customImageUriString).into(imagenCancion)
            } else {
                imagenCancion.setImageResource(R.drawable.nota_musical)
            }
        }

        MusicPlayerManager.isPlaying.observe(this) { isPlaying ->
            buttonPlay.setImageResource(if (isPlaying) R.drawable.stop else R.drawable.play)
        }

        MusicPlayerManager.isShuffle.observe(this) { isShuffle ->
            val activeColor = ContextCompat.getColor(this, R.color.teal_200)
            val inactiveColor = Color.parseColor("#757575")
            if (isShuffle) {
                buttonShuffle.setColorFilter(activeColor)
            } else {
                buttonShuffle.setColorFilter(inactiveColor)
            }
        }

        MusicPlayerManager.repeatMode.observe(this) { repeatMode ->
            val activeColor = ContextCompat.getColor(this, R.color.teal_200)
            val inactiveColor = Color.parseColor("#757575")

            when (repeatMode) {
                RepeatMode.ONE -> {
                    buttonRepeat.setImageResource(R.drawable.repeat_one)
                    buttonRepeat.setColorFilter(activeColor)
                }
                RepeatMode.ALL -> {
                    buttonRepeat.setImageResource(R.drawable.repeat)
                    buttonRepeat.setColorFilter(activeColor)
                }
                else -> { // NONE or null
                    buttonRepeat.setImageResource(R.drawable.repeat)
                    buttonRepeat.setColorFilter(inactiveColor)
                }
            }
        }

        MusicPlayerManager.songProgress.observe(this) { position: Int? ->
            position?.let {
                seekBar.progress = it
                tiempoActual.text = formatTime(it)
            }
        }

        MusicPlayerManager.songDuration.observe(this) { duration: Int? ->
            duration?.let {
                seekBar.max = it
                tiempoTotal.text = formatTime(it)
            }
        }
    }

    private fun setupListeners() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finishAfterTransition() }
        findViewById<ImageButton>(R.id.btnMore).setOnClickListener { showAddPlaylistDialog() }
        findViewById<ImageButton>(R.id.buttonPlay).setOnClickListener { MusicPlayerManager.playPause(this) }
        findViewById<ImageButton>(R.id.buttonNext).setOnClickListener { MusicPlayerManager.next(this) }
        findViewById<ImageButton>(R.id.buttonBefore).setOnClickListener { MusicPlayerManager.previous(this) }
        findViewById<ImageButton>(R.id.buttonShuffle).setOnClickListener { MusicPlayerManager.toggleShuffle() }
        findViewById<ImageButton>(R.id.buttonRepeat).setOnClickListener { MusicPlayerManager.toggleRepeat() }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    tiempoActual.text = formatTime(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let { MusicPlayerManager.seekTo(this@PlayerActivity, it.progress) }
            }
        })
    }

    private fun showAddPlaylistDialog() {
        val song = MusicPlayerManager.currentSong.value ?: return
        PlaylistManager.cargar(this)
        val playlists = PlaylistManager.obtenerNombres()

        if (playlists.isEmpty()) {
            Toast.makeText(this, "No tienes ninguna playlist", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Añadir a Playlist")
            .setItems(playlists.toTypedArray()) { _, which ->
                val playlistName = playlists[which]
                PlaylistManager.agregarCancion(this, playlistName, song)
                Toast.makeText(this, "Añadida a $playlistName", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun formatTime(ms: Int): String {
        val seconds = ms / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }
}