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

class AllSongsViewModel(application: Application) : AndroidViewModel(application) {

    private val _songs = MutableLiveData<List<InfoCancion>>()
    val songs: LiveData<List<InfoCancion>> = _songs
    private val coverArtService = CoverArtService()
    private val TAG = "CoverArtService"

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
        Log.i(TAG, "Iniciando el proceso de búsqueda de carátulas para ${songs.size} canciones.")
        viewModelScope.launch(Dispatchers.IO) {
            songs.forEach { song ->
                if (song.customImageUriString.isNullOrBlank()) {
                    var artistToSearch: String? = song.artista
                    var titleToSearch: String? = song.titulo
                    var albumToSearch: String? = song.album
                    val searchSource: String

                    if (artistToSearch.isNullOrBlank() || artistToSearch == "<unknown>" || artistToSearch == "Artista Desconocido") {
                        searchSource = "nombre de archivo"
                        Log.d(TAG, "launchCoverArtJobs: Artista inválido para '${song.titulo}'. Usando nombre de archivo como fuente.")
                        val (deducedArtist, deducedTitle) = coverArtService.parseInfoFromName(song.titulo)
                        artistToSearch = deducedArtist
                        titleToSearch = deducedTitle ?: song.titulo
                        albumToSearch = null // Si deducimos, el álbum original no es fiable
                    } else {
                        searchSource = "metadatos ID3"
                    }

                    if (!artistToSearch.isNullOrBlank()) {
                        Log.d(TAG, "launchCoverArtJobs: Lanzando búsqueda para '${titleToSearch}' (Artista: '${artistToSearch}') usando $searchSource.")
                        try {
                            val coverUrl = coverArtService.getCoverArtUrl(artistToSearch, titleToSearch!!, albumToSearch)
                            if (coverUrl != null) {
                                Log.i(TAG, "launchCoverArtJobs: ¡ÉXITO! URL encontrada para '${song.titulo}': $coverUrl")
                                song.customImageUriString = coverUrl
                                song.audioUriString?.let { SongMetadataManager.guardarCaratula(getApplication(), it, coverUrl) }
                                withContext(Dispatchers.Main) {
                                    _songs.value = _songs.value?.toList()
                                }
                            } else {
                                Log.w(TAG, "launchCoverArtJobs: SIN ÉXITO para '${song.titulo}'.")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "launchCoverArtJobs: ERROR FATAL buscando para '${song.titulo}': ${e.message}", e)
                        }
                    } else {
                        Log.w(TAG, "launchCoverArtJobs: Búsqueda CANCELADA para '${song.titulo}', no se pudo determinar un artista válido.")
                    }
                    delay(1000)
                } else {
                    Log.i(TAG, "launchCoverArtJobs: OMITIENDO búsqueda para '${song.titulo}' (razón: ya tiene carátula).")
                }
            }
            Log.i(TAG, "Proceso de búsqueda de carátulas finalizado.")
        }
    }
}
