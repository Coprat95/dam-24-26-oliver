package com.example.myapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AllSongsViewModel(application: Application) : AndroidViewModel(application) {

    private val _songs = MutableLiveData<List<InfoCancion>>()
    val songs: LiveData<List<InfoCancion>> = _songs

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
        }
    }
}
