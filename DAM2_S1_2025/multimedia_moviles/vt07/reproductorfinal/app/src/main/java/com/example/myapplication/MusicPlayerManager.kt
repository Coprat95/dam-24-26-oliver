package com.example.myapplication

import android.app.PendingIntent
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class RepeatMode {
    NONE, ONE, ALL
}

object MusicPlayerManager {

    private var originalPlaylist: List<InfoCancion> = emptyList()
    var currentPlaylist: List<InfoCancion> = emptyList()
        private set
    var currentPosition: Int = -1
        private set

    // Default state: Shuffle OFF, Repeat ALL
    private val _isShuffle = MutableLiveData<Boolean>().apply { value = false }
    val isShuffle: LiveData<Boolean> = _isShuffle

    private val _repeatMode = MutableLiveData<RepeatMode>().apply { value = RepeatMode.ALL }
    val repeatMode: LiveData<RepeatMode> = _repeatMode

    private val _currentSong = MutableLiveData<InfoCancion?>()
    val currentSong: LiveData<InfoCancion?> = _currentSong

    private val _isPlaying = MutableLiveData<Boolean>().apply { value = false }
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _songDuration = MutableLiveData<Int>().apply { value = 0 }
    val songDuration: LiveData<Int> = _songDuration

    private val _songProgress = MutableLiveData<Int>().apply { value = 0 }
    val songProgress: LiveData<Int> = _songProgress

    fun play(context: Context, playlist: List<InfoCancion>, position: Int) {
        originalPlaylist = playlist
        currentPlaylist = if (_isShuffle.value == true) playlist.shuffled() else playlist
        val songToPlay = playlist.getOrNull(position) ?: return
        currentPosition = currentPlaylist.indexOf(songToPlay)

        if (currentPosition != -1) {
            val song = currentPlaylist[currentPosition]
            _currentSong.postValue(song)
            MusicPreferences.saveLastSong(context, song)
            startServiceWithAction(context, MusicService.ACTION_PLAY)
        } else {
            currentPlaylist = originalPlaylist
            currentPosition = position
            _currentSong.postValue(originalPlaylist[position])
            MusicPreferences.saveLastSong(context, originalPlaylist[position])
            startServiceWithAction(context, MusicService.ACTION_PLAY)
        }
    }

    fun prepareLastSong(context: Context) {
        if (currentSong.value != null) return
        CoroutineScope(Dispatchers.Main).launch {
            val lastSong = MusicPreferences.getLastSong(context) ?: return@launch
            val allSongs = withContext(Dispatchers.IO) { loadAllSongsFromProvider(context) }
            if (allSongs.isEmpty()) return@launch

            val lastSongIndex = allSongs.indexOfFirst { it.audioUriString == lastSong.audioUriString }

            originalPlaylist = allSongs
            currentPlaylist = if (_isShuffle.value == true) allSongs.shuffled() else allSongs
            currentPosition = if (lastSongIndex != -1) currentPlaylist.indexOf(allSongs[lastSongIndex]) else 0
            
            if (currentPosition != -1 && currentPlaylist.isNotEmpty()) {
                _currentSong.postValue(currentPlaylist[currentPosition])
            } else {
                _currentSong.postValue(currentPlaylist.firstOrNull())
            }
            _isPlaying.postValue(false)
        }
    }

    fun playPause(context: Context) {
        if (currentSong.value == null && currentPlaylist.isNotEmpty()) {
            play(context, originalPlaylist, 0)
        } else {
            val action = if (_isPlaying.value == true) MusicService.ACTION_PAUSE else MusicService.ACTION_PLAY
            startServiceWithAction(context, action)
        }
    }

    fun onSongFinished(context: Context) {
        if (currentPlaylist.isEmpty()) return

        if (_repeatMode.value == RepeatMode.ONE) {
            currentSong.value?.let {
                val originalIndex = originalPlaylist.indexOf(it)
                if (originalIndex != -1) play(context, originalPlaylist, originalIndex)
                return
            }
        }

        val isLooping = _repeatMode.value == RepeatMode.ALL
        var nextPosition = -1

        if (isLooping) {
            nextPosition = (currentPosition + 1) % currentPlaylist.size
        } else { // This is for RepeatMode.NONE
            if (currentPosition < currentPlaylist.size - 1) {
                nextPosition = currentPosition + 1
            }
        }

        if (nextPosition != -1) {
            val nextSong = currentPlaylist[nextPosition]
            val originalIndex = originalPlaylist.indexOf(nextSong)
            if (originalIndex != -1) {
                play(context, originalPlaylist, originalIndex)
            }
        } else {
            updateIsPlaying(false)
            startServiceWithAction(context, MusicService.ACTION_STOP)
        }
    }
    
    fun next(context: Context) {
        if (currentPlaylist.isEmpty()) return
        val nextPos = (currentPosition + 1) % currentPlaylist.size
        val nextSong = currentPlaylist[nextPos]
        val originalIndex = originalPlaylist.indexOf(nextSong)
        if (originalIndex != -1) {
            play(context, originalPlaylist, originalIndex)
        }
    }

    fun previous(context: Context) {
        if (currentPlaylist.isEmpty()) return
        val prevPos = (currentPosition - 1 + currentPlaylist.size) % currentPlaylist.size
        val prevSong = currentPlaylist[prevPos]
        val originalIndex = originalPlaylist.indexOf(prevSong)
        if (originalIndex != -1) {
            play(context, originalPlaylist, originalIndex)
        }
    }
    
    fun toggleShuffle() {
        val newShuffleState = !(_isShuffle.value ?: false)
        _isShuffle.postValue(newShuffleState)

        if (newShuffleState) {
            // If Shuffle is now ON, turn off Repeat.
            _repeatMode.postValue(RepeatMode.NONE)
        } else {
            // If Shuffle was just turned OFF, and Repeat is also OFF,
            // we must enable the default Repeat mode.
            if (_repeatMode.value == RepeatMode.NONE) {
                _repeatMode.postValue(RepeatMode.ALL)
            }
        }
        
        // Re-order the playlist based on the new state
        val currentSong = this.currentSong.value ?: return
        if (newShuffleState) {
            val shuffledList = originalPlaylist.shuffled().toMutableList()
            shuffledList.remove(currentSong)
            shuffledList.add(0, currentSong)
            this.currentPlaylist = shuffledList
            this.currentPosition = 0
        } else {
            this.currentPlaylist = originalPlaylist
            this.currentPosition = originalPlaylist.indexOf(currentSong)
        }
    }

    fun toggleRepeat() {
        // Cycle: ALL -> ONE -> NONE
        val newRepeatMode = when (_repeatMode.value) {
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.NONE
            else -> RepeatMode.ALL // This includes NONE or null
        }
        _repeatMode.postValue(newRepeatMode)

        if (newRepeatMode != RepeatMode.NONE) {
            // If a Repeat mode is now ON, turn off Shuffle.
            if (_isShuffle.value == true) {
                _isShuffle.postValue(false)
                // Revert to original playlist order
                val currentSong = this.currentSong.value ?: return
                this.currentPlaylist = originalPlaylist
                this.currentPosition = originalPlaylist.indexOf(currentSong)
            }
        } else {
            // If Repeat was just turned OFF, and Shuffle is also OFF,
            // we must enable Shuffle.
            if (_isShuffle.value == false) {
                _isShuffle.postValue(true)
                // Shuffle the playlist
                val currentSong = this.currentSong.value ?: return
                val shuffledList = originalPlaylist.shuffled().toMutableList()
                shuffledList.remove(currentSong)
                shuffledList.add(0, currentSong)
                this.currentPlaylist = shuffledList
                this.currentPosition = 0
            }
        }
    }

    fun seekTo(context: Context, position: Int) {
        val intent = Intent(context, MusicService::class.java).apply {
            action = MusicService.ACTION_SEEK_TO
            putExtra(MusicService.EXTRA_SEEK_POSITION, position)
        }
        context.startService(intent)
    }

    fun updateIsPlaying(isPlaying: Boolean) { _isPlaying.postValue(isPlaying) }
    fun updateDuration(duration: Int) { _songDuration.postValue(duration) }
    fun updateProgress(progress: Int) { _songProgress.postValue(progress) }

    private fun startServiceWithAction(context: Context, action: String) {
        val intent = Intent(context, MusicService::class.java)
        intent.action = action
        context.startService(intent)
    }
    
    fun createContentIntent(context: Context): PendingIntent {
        val intent = Intent(context, PlayerActivity::class.java)
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }
    
    private fun loadAllSongsFromProvider(context: Context): List<InfoCancion> {
        val allSongsList = mutableListOf<InfoCancion>()
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media._ID
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, sortOrder)?.use { cursor ->
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                val artist = cursor.getString(artistCol) ?: ""
                val album = cursor.getString(albumCol) ?: ""
                allSongsList.add(InfoCancion(cursor.getString(titleCol), if (artist == "<unknown>") "" else artist, album, null, uri.toString(), R.drawable.nota_musical, null))
            }
        }
        return allSongsList
    }
}
