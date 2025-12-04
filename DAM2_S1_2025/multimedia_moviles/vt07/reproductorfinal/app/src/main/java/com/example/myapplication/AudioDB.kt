package com.example.myapplication

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.util.Log

object AudioDB {

    private const val TAG = "Neonbeat-Debug"

    fun getAllSongs(context: Context): List<InfoCancion> {
        Log.d(TAG, "AudioDB: Iniciando getAllSongs()...")
        val allSongsList = mutableListOf<InfoCancion>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} >= 60000"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        try {
            Log.d(TAG, "AudioDB: Realizando consulta a MediaStore...")
            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            )?.use { cursor ->
                Log.d(TAG, "AudioDB: La consulta a MediaStore devolvió un cursor. Columnas: ${cursor.columnNames.joinToString()}")

                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)

                Log.d(TAG, "AudioDB: Recorriendo el cursor. Encontradas ${cursor.count} canciones.")
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                    val title = cursor.getString(titleCol)
                    val artist = cursor.getString(artistCol)
                    val album = cursor.getString(albumCol)

                    // PRUEBA DEFINITIVA: Mostramos los datos en bruto leídos del sistema
                    Log.d(TAG, "AudioDB - LECTURA EN BRUTO -> Título: '$title', Artista: '$artist', Álbum: '$album'")

                    allSongsList.add(
                        InfoCancion(
                            titulo = title ?: "Título Desconocido",
                            artista = if (artist.isNullOrBlank() || artist == "<unknown>") "Artista Desconocido" else artist,
                            album = if (album.isNullOrBlank()) "Álbum Desconocido" else album,
                            audioResId = null,
                            audioUriString = uri.toString(),
                            fotoResId = R.drawable.nota_musical,
                            customImageUriString = null
                        )
                    )
                }
                Log.d(TAG, "AudioDB: Finalizado el recorrido del cursor.")
            } ?: Log.w(TAG, "AudioDB: La consulta a MediaStore devolvió un cursor nulo.")

        } catch (e: Exception) {
            Log.e(TAG, "AudioDB: Error CRÍTICO al consultar MediaStore. ${e.message}", e)
        }

        Log.d(TAG, "AudioDB: getAllSongs() finalizado. Se devolverán ${allSongsList.size} canciones.")
        return allSongsList
    }
}
