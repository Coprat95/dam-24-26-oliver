package com.example.myapplication

import android.app.Application
import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.io.File

class AddSongsViewModel(application: Application) : AndroidViewModel(application) {

    private val _songs = MutableLiveData<List<InfoCancion>>()
    val songs: LiveData<List<InfoCancion>> = _songs

    fun loadSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            val songList = mutableListOf<InfoCancion>()
            val context = getApplication<Application>().applicationContext

            HiddenSongsManager.load(context)
            val projection = arrayOf(
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DURATION
            )
            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} >= 60000"

            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null
            )?.use { cursor ->
                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                    val songUriString = contentUri.toString()

                    if (!HiddenSongsManager.isHidden(songUriString)) {
                        val artist = cursor.getString(artistCol) ?: "Desconocido"
                        val title = cursor.getString(titleCol)

                        try {
                            context.contentResolver.takePersistableUriPermission(contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        } catch (_: SecurityException) {
                            // Ignored
                        }
                        songList.add(InfoCancion(title, artist, null, songUriString, R.drawable.nota_musical, null))
                    }
                }
            }

            val internalSongsDir = context.filesDir
            val songsDir = File(internalSongsDir, "songs")
            if (songsDir.exists()) {
                songsDir.listFiles()?.forEach { file ->
                    val songUriString = Uri.fromFile(file).toString()
                    if (!HiddenSongsManager.isHidden(songUriString) && songList.none { it.audioUriString == songUriString }) {
                        try {
                            val retriever = android.media.MediaMetadataRetriever()
                            retriever.setDataSource(file.absolutePath)
                            val durationStr = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)
                            val artist = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Desconocido"
                            val title = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_TITLE) ?: file.nameWithoutExtension
                            retriever.release()
                            val duration = durationStr?.toLongOrNull() ?: 0
                            if (duration >= 60000) {
                                songList.add(InfoCancion(title, artist, null, songUriString, R.drawable.nota_musical, null))
                            }
                        } catch (_: Exception) {
                            // Ignored
                        }
                    }
                }
            }
            _songs.postValue(songList)

            val updatedSongs = songList.map { song ->
                async {
                    val coverUrl = fetchAlbumArt(song.artista, song.titulo)
                    song.copy(customImageUriString = coverUrl)
                }
            }.awaitAll()
            _songs.postValue(updatedSongs)
        }
    }

    private suspend fun fetchAlbumArt(artist: String, track: String): String? {
        return try {
            RetrofitClient.instance.searchTrack(artist, track).track?.firstOrNull()?.strTrackThumb
        } catch (_: Exception) {
            null
        }
    }
}
