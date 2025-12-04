package com.example.myapplication

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import java.util.ArrayList

class PlaylistDetailActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: SongsAdapter
    private lateinit var miniPlayerContainer: ConstraintLayout
    private lateinit var btnAction: AppCompatImageButton
    private lateinit var btnSelectAll: AppCompatImageButton

    private var nombrePlaylist: String = ""
    private var cancionesLista: ArrayList<InfoCancion> = ArrayList()
    private var isSelectionMode = false

    private val addSongsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedSongs = result.data?.getSerializableExtra("SELECTED_SONGS") as? ArrayList<InfoCancion>
            if (selectedSongs != null) {
                PlaylistManager.agregarCanciones(this, nombrePlaylist, selectedSongs)
                actualizarLista()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setupTransitions()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_detail)

        findViewById<View>(R.id.main)?.let { mainView ->
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        nombrePlaylist = intent.getStringExtra("PLAYLIST_NAME") ?: ""

        val tvTitle = findViewById<TextView>(R.id.tvPlaylistName)
        tvTitle.text = nombrePlaylist

        recyclerView = findViewById(R.id.recyclerViewSongs)
        tvEmpty = findViewById(R.id.tvEmpty)
        val btnBack = findViewById<AppCompatImageButton>(R.id.btnBack)
        btnAction = findViewById(R.id.btnAction)
        btnSelectAll = findViewById(R.id.btnSelectAll)
        val btnPlayPlaylist = findViewById<MaterialButton>(R.id.btnPlayPlaylist)
        val btnShufflePlaylist = findViewById<MaterialButton>(R.id.btnShufflePlaylist)
        miniPlayerContainer = findViewById(R.id.miniPlayer)

        btnBack.setOnClickListener { finish() }

        btnAction.setOnClickListener { 
            if (isSelectionMode) {
                val selectedSongs = adapter.getSelectedItems()
                if (selectedSongs.isNotEmpty()) {
                    cancionesLista.removeAll(selectedSongs.toSet())
                    PlaylistManager.actualizarPlaylist(this, nombrePlaylist, cancionesLista)
                    adapter.setMode(false)
                    actualizarLista()
                    Toast.makeText(this, "${selectedSongs.size} canciones eliminadas", Toast.LENGTH_SHORT).show()
                }
            } else {
                val intent = Intent(this, AddSongsToPlaylistActivity::class.java)
                addSongsLauncher.launch(intent)
            }
        }

        btnSelectAll.setOnClickListener { adapter.toggleSelectAll() }
        
        btnPlayPlaylist.setOnClickListener {
            if (cancionesLista.isNotEmpty()) {
                if (MusicPlayerManager.isShuffle.value == true) {
                    MusicPlayerManager.toggleShuffle()
                }
                playSong(0)
            }
        }

        btnShufflePlaylist.setOnClickListener {
            if (cancionesLista.isNotEmpty()) {
                if (MusicPlayerManager.isShuffle.value == false) {
                    MusicPlayerManager.toggleShuffle()
                }
                val position = (0 until cancionesLista.size).random()
                playSong(position)
            }
        }

        adapter = SongsAdapter(cancionesLista,
            onItemClick = { position, _ ->
                MusicPlayerManager.play(this, cancionesLista, position)
                adapter.setPlayingState(true)
            },
            onMoreClick = { position, view -> showPopupMenu(position, view) },
            onSelectionModeChange = { selectionMode ->
                isSelectionMode = selectionMode
                if (isSelectionMode) {
                    btnAction.setImageResource(R.drawable.ic_delete)
                    btnAction.contentDescription = "Borrar"
                    btnSelectAll.visibility = View.VISIBLE
                    btnAction.setColorFilter(ContextCompat.getColor(this, android.R.color.white))
                } else {
                    btnAction.setImageResource(R.drawable.ic_add_circle)
                    btnAction.contentDescription = "Añadir Canción a Playlist"
                    btnSelectAll.visibility = View.GONE
                    btnAction.clearColorFilter()
                }
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        setupMiniPlayer()
        setupObservers()
        actualizarLista()
    }

    private fun setupTransitions() {
        window.requestFeature(android.view.Window.FEATURE_ACTIVITY_TRANSITIONS)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false
    }

    private fun actualizarLista() {
        HiddenSongsManager.load(this)
        PlaylistManager.cargar(this)
        val storedSongs = PlaylistManager.obtenerCanciones(nombrePlaylist)
        cancionesLista.clear()

        for (song in storedSongs) {
            val artist = song.artista ?: ""
            val updatedSong = song.copy(
                artista = if (artist == "<unknown>") "" else artist,
                customImageUriString = SongMetadataManager.getCaratula(this, song.audioUriString ?: "") ?: song.customImageUriString
            )
            if (!HiddenSongsManager.isHidden(updatedSong.audioUriString ?: ""))
                cancionesLista.add(updatedSong)
        }

        adapter.updateSongs(cancionesLista)
        actualizarVistaVacia()
        findViewById<MaterialButton>(R.id.btnPlayPlaylist).isEnabled = cancionesLista.isNotEmpty()
        findViewById<MaterialButton>(R.id.btnShufflePlaylist).isEnabled = cancionesLista.isNotEmpty()
    }

    private fun actualizarVistaVacia() {
        if (cancionesLista.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun playSong(position: Int) {
        MusicPlayerManager.play(this, cancionesLista, position)
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
            adapter.setCurrentPlayingSong(song)
            if (song != null) {
                miniPlayerContainer.visibility = View.VISIBLE
                updateMiniPlayerUI(song)
            } else {
                miniPlayerContainer.visibility = View.GONE
            }
        }

        MusicPlayerManager.isPlaying.observe(this) { isPlaying ->
            adapter.setPlayingState(isPlaying)
            findViewById<ImageButton>(R.id.miniPlayerPlay).setImageResource(if (isPlaying) R.drawable.stop_mini else R.drawable.play_mini)
            MusicPlayerManager.currentSong.value?.let {
                updateMiniPlayerUI(it)
            }
        }
    }

    private fun updateMiniPlayerUI(song: InfoCancion) {
        val miniPlayerTitle = findViewById<TextView>(R.id.miniPlayerTitle)
        val miniPlayerArtist = findViewById<TextView>(R.id.miniPlayerArtist)
        val miniPlayerCover = findViewById<ImageView>(R.id.miniPlayerCover)
        val miniPlayerEqualizer = findViewById<ImageView>(R.id.miniPlayerEqualizer)
        val avd = miniPlayerEqualizer.drawable as? AnimatedVectorDrawable

        miniPlayerTitle.text = song.titulo
        miniPlayerArtist.text = song.artista

        val isPlaying = MusicPlayerManager.isPlaying.value ?: false

        if (isPlaying) {
            miniPlayerCover.visibility = View.GONE
            miniPlayerEqualizer.visibility = View.VISIBLE
            avd?.start()
        } else {
            miniPlayerEqualizer.visibility = View.GONE
            avd?.stop()
            miniPlayerCover.visibility = View.VISIBLE

            if (song.customImageUriString != null) {
                miniPlayerCover.clearColorFilter()
                Glide.with(this).load(song.customImageUriString).into(miniPlayerCover)
            } else {
                miniPlayerCover.setImageResource(R.drawable.nota_musical)
                miniPlayerCover.setColorFilter(ContextCompat.getColor(this, android.R.color.white))
            }
        }
    }

    private fun showPopupMenu(position: Int, view: View) {
        val popup = PopupMenu(this, view)
        popup.menu.add("Reproducir")
        popup.menu.add("Eliminar de Playlist")

        popup.setOnMenuItemClickListener { item ->
            when (item.title) {
                "Reproducir" -> {
                    playSong(position)
                    true
                }
                "Eliminar de Playlist" -> {
                    cancionesLista.removeAt(position)
                    PlaylistManager.actualizarPlaylist(this, nombrePlaylist, cancionesLista)
                    adapter.notifyItemRemoved(position)
                    actualizarVistaVacia()
                    Toast.makeText(this, "Eliminada de $nombrePlaylist", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    override fun onResume() {
        super.onResume()
        actualizarLista()
    }
}