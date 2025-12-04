package com.example.myapplication

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.network.CoverArtService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddSongsViewModel(application: Application) : AndroidViewModel(application) {

    private val songRepository: SongRepository
    private val coverArtService = CoverArtService()
    private val TAG = "AddSongsViewModel"

    private val _songs = MutableLiveData<List<InfoCancion>>()
    val songs: LiveData<List<InfoCancion>> = _songs

    init {
        val songDao = SongDatabase.getDatabase(application).songDao()
        songRepository = SongRepository(songDao, application)
    }

    fun loadSongs() {
        viewModelScope.launch {
            Log.d(TAG, "Cargando lista de canciones desde el repositorio...")
            val songList = songRepository.getAllSongs()
            val infoCancionList = songList.map { song ->
                InfoCancion(
                    titulo = song.titulo,
                    artista = song.artista ?: "Artista desconocido",
                    album = null, // Album no disponible en la clase Song
                    audioResId = null,
                    audioUriString = song.audioUriString,
                    fotoResId = 0, // El adaptador se encarga de la imagen por defecto
                    customImageUriString = song.customImageUriString
                )
            }
            _songs.value = infoCancionList
            Log.d(TAG, "Se cargaron ${infoCancionList.size} canciones. Lanzando búsqueda de carátulas.")
            launchCoverArtJobs(infoCancionList)
        }
    }

    private fun launchCoverArtJobs(songs: List<InfoCancion>) {
        Log.d("CoverArtService", "Iniciando búsqueda de carátulas para ${songs.size} canciones.")
        songs.forEachIndexed { index, song ->
            if (song.customImageUriString != null) {
                Log.d("CoverArtService", "(${index}/${songs.size}) Saltando '${song.titulo}', ya tiene carátula.")
                return@forEachIndexed
            }

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    Log.d("CoverArtService", "(${index}/${songs.size}) Buscando para '${song.titulo}'...")
                    val coverUrl = coverArtService.getCoverArtUrl(song.artista, song.titulo, song.album)
                    if (coverUrl != null) {
                        val updatedSong = song.copy(customImageUriString = coverUrl)
                        // Actualizar la base de datos para persistir la carátula
                        song.audioUriString?.let {
                            songRepository.updateCoverArt(it, coverUrl)
                        }
                        
                        // Actualizar la UI
                        withContext(Dispatchers.Main) {
                            val currentList = _songs.value?.toMutableList() ?: return@withContext
                            val songIndex = currentList.indexOfFirst { it.audioUriString == updatedSong.audioUriString }
                            if (songIndex != -1) {
                                currentList[songIndex] = updatedSong
                                _songs.value = currentList
                                Log.i(TAG, "UI actualizada para '${updatedSong.titulo}'")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("CoverArtService", "Error procesando '${song.titulo}': ${e.message}")
                }
            }
        }
    }
}