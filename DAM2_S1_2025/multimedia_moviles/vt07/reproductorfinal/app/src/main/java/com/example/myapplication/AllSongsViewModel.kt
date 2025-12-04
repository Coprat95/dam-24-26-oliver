package com.example.myapplication

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.network.CoverArtService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// CAMBIO: El nombre de la clase ahora es AllSongsViewModel para que coincida con el que usa la Activity
class AllSongsViewModel(application: Application) : AndroidViewModel(application) {

    private val _songs = MutableLiveData<List<InfoCancion>>()
    val songs: LiveData<List<InfoCancion>> = _songs
    private val coverArtService = CoverArtService()

    private val COVER_ART_TAG = "CoverArtService"

    fun loadSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val songList = AudioDB.getAllSongs(getApplication())
                HiddenSongsManager.load(getApplication())
                val filteredList = songList.filter {
                    it.audioUriString?.let { uri -> !HiddenSongsManager.isHidden(uri) } ?: true
                }

                withContext(Dispatchers.Main) {
                    _songs.value = filteredList
                }

                launchCoverArtJobs(filteredList)

            } catch (e: Exception) {
                Log.e("Neonbeat-Error", "ViewModel: Error CRÍTICO en la corrutina de loadSongs. ${e.message}", e)
            }
        }
    }

    private fun launchCoverArtJobs(songs: List<InfoCancion>) {
        Log.i(COVER_ART_TAG, "Iniciando el proceso de búsqueda de carátulas para ${songs.size} canciones.")

        viewModelScope.launch(Dispatchers.IO) {
            songs.forEach { song ->
                if (song.customImageUriString.isNullOrBlank() && !song.artista.isNullOrBlank() && song.artista != "<unknown>") {
                    try {
                        Log.i(COVER_ART_TAG, "-> Buscando para '${song.titulo}' (Artista: '${song.artista}')")

                        val coverUrl = coverArtService.getCoverArtUrl(song.artista!!, song.titulo, song.album)

                        if (coverUrl != null) {
                            Log.i(COVER_ART_TAG, "-> ¡ÉXITO! Carátula encontrada para '${song.titulo}': $coverUrl")
                            song.customImageUriString = coverUrl
                            song.audioUriString?.let { SongMetadataManager.guardarCaratula(getApplication(), it, coverUrl) }

                            withContext(Dispatchers.Main) {
                                _songs.value = _songs.value?.toList()
                            }
                        } else {
                            Log.w(COVER_ART_TAG, "-> SIN ÉXITO para '${song.titulo}'. Ninguna API devolvió resultados.")
                        }
                    } catch (e: Exception) {
                        Log.e(COVER_ART_TAG, "-> ERROR FATAL buscando para '${song.titulo}': ${e.message}", e)
                    }
                    delay(1000)
                } else {
                    Log.i(COVER_ART_TAG, "-> OMITIENDO búsqueda para '${song.titulo}' (razón: ya tiene carátula o artista es desconocido).")
                }
            }
            Log.i(COVER_ART_TAG, "Proceso de búsqueda de carátulas finalizado.")
        }
    }
}
