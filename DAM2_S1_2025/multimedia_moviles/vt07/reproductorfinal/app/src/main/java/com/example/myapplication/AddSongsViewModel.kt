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

class AddSongsViewModel(application: Application) : AndroidViewModel(application) {

    private val _songs = MutableLiveData<List<InfoCancion>>()
    val songs: LiveData<List<InfoCancion>> = _songs
    private val coverArtService = CoverArtService()

    fun loadSongs() {
        viewModelScope.launch {
            val songList = withContext(Dispatchers.IO) {
                AudioDB.getAllSongs(getApplication())
            }
            withContext(Dispatchers.IO) {
                HiddenSongsManager.load(getApplication())
            }
            val filteredList = songList.filter { song ->
                song.audioUriString?.let { !HiddenSongsManager.isHidden(it) } ?: true
            }
            _songs.postValue(filteredList)

            launch(Dispatchers.IO) {
                var listUpdated = false
                for (song in filteredList) {
                    if (song.customImageUriString == null && song.artista.isNotEmpty()) {
                        try {
                            Log.d("CoverArt", "Buscando carátula para: ${song.artista}")
                            val coverUrl = coverArtService.getCoverArtUrl(song.artista)
                            if (coverUrl != null) {
                                Log.d("CoverArt", "Carátula encontrada: $coverUrl")
                                song.customImageUriString = coverUrl
                                song.audioUriString?.let { uri ->
                                    SongMetadataManager.guardarCaratula(getApplication(), uri, coverUrl)
                                }
                                listUpdated = true
                            } else {
                                Log.d("CoverArt", "No se encontró carátula para: ${song.artista}")
                            }
                        } catch (e: Exception) {
                            Log.e("CoverArt", "Error buscando carátula: ${e.message}")
                        }
                        delay(1000) // Pausa de 1 segundo para no sobrecargar la API
                    }
                }
                
                if (listUpdated) {
                    _songs.postValue(filteredList)
                }
            }
        }
    }
}
