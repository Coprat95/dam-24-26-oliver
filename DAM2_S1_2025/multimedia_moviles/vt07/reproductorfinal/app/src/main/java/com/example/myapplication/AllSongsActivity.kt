package com.example.myapplication

import android.app.ActivityOptions
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import java.io.File
import java.io.FileOutputStream
import java.util.ArrayList
import java.util.UUID

class AllSongsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: SongsAdapter
    private lateinit var miniPlayerContainer: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var btnAction: AppCompatImageButton
    private lateinit var btnSelectAll: AppCompatImageButton
    private val viewModel: AllSongsViewModel by viewModels()

    private var selectedSongPosition: Int = -1
    private var isSelectionMode = false

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) viewModel.loadSongs() else Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
    }

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { 
            val newCoverUri = guardarImagenInterna(it)
            if (newCoverUri != null && selectedSongPosition != -1) {
                val song = viewModel.songs.value?.get(selectedSongPosition)
                if (song != null) {
                    song.customImageUriString = newCoverUri
                    if (song.audioUriString != null) {
                        SongMetadataManager.guardarCaratula(this, song.audioUriString, newCoverUri)
                    }
                    adapter.notifyItemChanged(selectedSongPosition)
                    Toast.makeText(this, "Carátula actualizada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { agregarCancionManual(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setupTransitions()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_songs)

        findViewById<View>(R.id.main).let { mainView ->
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        recyclerView = findViewById(R.id.recyclerViewSongs)
        tvEmpty = findViewById(R.id.tvEmpty)
        val btnBack = findViewById<AppCompatImageButton>(R.id.btnBack)
        btnAction = findViewById(R.id.btnAction)
        btnSelectAll = findViewById(R.id.btnSelectAll)
        miniPlayerContainer = findViewById(R.id.miniPlayer)

        btnBack.setOnClickListener { finish() }
        btnAction.setOnClickListener { 
            if (isSelectionMode) {
                val selectedSongs = adapter.getSelectedItems()
                if (selectedSongs.isNotEmpty()) {
                    AlertDialog.Builder(this)
                        .setTitle("Ocultar Canciones")
                        .setMessage("¿Seguro que quieres ocultar ${selectedSongs.size} canciones?")
                        .setPositiveButton("Ocultar") { _, _ ->
                            selectedSongs.forEach { song ->
                                if (song.audioUriString != null) {
                                    HiddenSongsManager.hideSong(this, song.audioUriString)
                                }
                            }
                            HiddenSongsManager.load(this)
                            viewModel.loadSongs()
                            adapter.setMode(false)
                            Toast.makeText(this, "${selectedSongs.size} canciones ocultas", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                }
            } else {
                filePickerLauncher.launch("audio/*")
            }
        }

        btnSelectAll.setOnClickListener { adapter.toggleSelectAll() }

        adapter = SongsAdapter(emptyList(),
            onItemClick = { position, _ ->
                MusicPlayerManager.play(this, viewModel.songs.value ?: emptyList(), position)
                adapter.setPlayingState(true)
            },
            onMoreClick = { position, view -> showPopupMenu(position, view) },
            onSelectionModeChange = { selectionMode ->
                isSelectionMode = selectionMode
                if (isSelectionMode) {
                    btnAction.setImageResource(R.drawable.ic_delete)
                    btnAction.contentDescription = "Ocultar"
                    btnAction.setColorFilter(ContextCompat.getColor(this, android.R.color.white))
                    btnSelectAll.visibility = View.VISIBLE
                } else {
                    btnAction.setImageResource(R.drawable.ic_add_circle)
                    btnAction.contentDescription = "Añadir Canción"
                    btnAction.setColorFilter(ContextCompat.getColor(this, R.color.teal_200))
                    btnSelectAll.visibility = View.GONE
                }
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel.songs.observe(this) { songs ->
            val currentSongs = ArrayList(songs)
            adapter.updateSongs(currentSongs)
            if (currentSongs.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                tvEmpty.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }

        setupMiniPlayer()
        setupObservers()
        checkPermissionsAndLoadSongs()
    }

    private fun setupTransitions() {
        window.requestFeature(android.view.Window.FEATURE_ACTIVITY_TRANSITIONS)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadSongs()
    }

    private fun getFileExtension(uri: Uri): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun guardarAudioInterno(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val directory = File(filesDir, "songs")
            if (!directory.exists()) directory.mkdirs()

            val extension = getFileExtension(uri) ?: "mp3"
            val fileName = "song_${UUID.randomUUID()}.$extension"
            val file = File(directory, fileName)

            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun guardarImagenInterna(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val directory = File(filesDir, "covers")
            if (!directory.exists()) directory.mkdirs()

            val fileName = "cover_${UUID.randomUUID()}.jpg"
            val file = File(filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun checkPermissionsAndLoadSongs() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) android.Manifest.permission.READ_MEDIA_AUDIO else android.Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) viewModel.loadSongs() else requestPermissionLauncher.launch(permission)
    }

    private fun agregarCancionManual(uri: Uri) {
        var nombreCancion = "Canción Importada"
        try {
            contentResolver.query(uri, null, null, null, null)?.use {
                if (it.moveToFirst()) {
                    val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (idx != -1) nombreCancion = it.getString(idx).substringBeforeLast('.')
                }
            }
        } catch (e: Exception) { e.printStackTrace() }

        val rutaInterna = guardarAudioInterno(uri)
        if (rutaInterna != null) {
            val file = File(rutaInterna)
            val nuevaUri = Uri.fromFile(file).toString()

            SongMetadataManager.guardarTitulo(this, nuevaUri, nombreCancion)

            viewModel.loadSongs()
            Toast.makeText(this, "Canción añadida", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error al añadir la canción", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playSong(position: Int) {
        MusicPlayerManager.play(this, viewModel.songs.value ?: emptyList(), position)
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
        miniPlayerArtist.text = song.artista

        if (song.customImageUriString != null) {
            Glide.with(this).load(song.customImageUriString).into(miniPlayerCover)
        } else {
            miniPlayerCover.setImageResource(R.drawable.nota_musical)
        }
    }

    private fun showPopupMenu(position: Int, view: View) {
        val cancion = viewModel.songs.value?.get(position) ?: return
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.song_options, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_play -> { playSong(position); true }
                R.id.action_change_cover -> { selectedSongPosition = position; imagePickerLauncher.launch("image/*"); true }
                R.id.action_add_to_playlist -> { mostrarDialogoSeleccionLista(cancion); true }
                R.id.action_rename -> { mostrarDialogoRenombrar(cancion); true }
                R.id.action_delete -> { mostrarDialogoEliminar(cancion); true }
                R.id.action_credits -> { startActivity(Intent(this, CreditsActivity::class.java)); true }
                else -> false
            }
        }
        popup.show()
    }

    private fun mostrarDialogoSeleccionLista(cancion: InfoCancion) {
        PlaylistManager.cargar(this)
        val listasDisponibles = PlaylistManager.obtenerNombres()

        if (listasDisponibles.isEmpty()) {
            Toast.makeText(this, "No tienes listas creadas.", Toast.LENGTH_LONG).show()
            return
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Añadir a Playlist")

        builder.setItems(listasDisponibles.toTypedArray()) { _: DialogInterface, which: Int ->
            val nombreLista = listasDisponibles[which]
            val uri = cancion.audioUriString
            val globalCover = if (uri != null) SongMetadataManager.getCaratula(uri) else null
            val globalTitle = if (uri != null) SongMetadataManager.getTitulo(uri) else null

            val cancionAGuardar = cancion.copy(
                titulo = globalTitle ?: cancion.titulo,
                customImageUriString = globalCover ?: cancion.customImageUriString
            )

            PlaylistManager.cargar(this)
            PlaylistManager.agregarCancion(this, nombreLista, cancionAGuardar)

            Toast.makeText(this, "Añadida a '$nombreLista'", Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }

    private fun mostrarDialogoRenombrar(cancion: InfoCancion) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cambiar Nombre")
        val input = EditText(this)
        input.setText(cancion.titulo)
        builder.setView(input)

        builder.setPositiveButton("Guardar") { _, _ ->
            val nuevoNombre = input.text.toString()
            if (nuevoNombre.isNotEmpty()) {
                cancion.titulo = nuevoNombre
                if (cancion.audioUriString != null) {
                    SongMetadataManager.guardarTitulo(this, cancion.audioUriString, nuevoNombre)
                }
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Nombre guardado", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun mostrarDialogoEliminar(cancion: InfoCancion) {
        AlertDialog.Builder(this)
            .setTitle("Ocultar Canción")
            .setMessage("¿Seguro que quieres ocultar '${cancion.titulo}' de esta lista?")
            .setPositiveButton("Ocultar") { _, _ ->
                if (cancion.audioUriString != null) {
                    HiddenSongsManager.hideSong(this, cancion.audioUriString)
                    HiddenSongsManager.load(this)
                    viewModel.loadSongs()
                    Toast.makeText(this, "Canción oculta", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error: No se pudo ocultar la canción", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}