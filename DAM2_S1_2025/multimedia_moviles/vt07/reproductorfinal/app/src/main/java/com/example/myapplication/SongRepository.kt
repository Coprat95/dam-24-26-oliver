package com.example.myapplication

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SongRepository(private val songDao: SongDao, private val context: Context) {

    fun getSongs(): Flow<PagingData<Song>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { songDao.pagingSource() }
        ).flow
    }

    suspend fun getAllSongs(): List<Song> {
        return songDao.getAllSongs()
    }

    suspend fun hideSongs(uris: List<String>) {
        songDao.hideSongs(uris)
    }

    suspend fun updateCoverArt(songUri: String, coverUri: String) {
        songDao.updateCoverArt(songUri, coverUri)
    }

    suspend fun syncSongs() {
        withContext(Dispatchers.IO) {
            val deviceSongs = loadSongsFromDevice()
            songDao.insertAll(deviceSongs)
        }
    }

    private fun loadSongsFromDevice(): List<Song> {
        val songs = mutableListOf<Song>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection, 
            null, 
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                
                songs.add(Song(uri.toString(), title, artist, null))
            }
        }
        return songs
    }
}
