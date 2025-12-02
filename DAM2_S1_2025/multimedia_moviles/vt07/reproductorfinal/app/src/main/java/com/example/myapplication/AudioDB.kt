package com.example.myapplication

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore

object AudioDB {

    fun getAllSongs(context: Context): List<InfoCancion> {
        val allSongsList = mutableListOf<InfoCancion>()
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DURATION
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} >= 60000"
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
