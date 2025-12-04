package com.example.myapplication

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object SongMetadataManager {
    private val coversMap = HashMap<String, String>()
    private val tituloMap = HashMap<String, String>()

    fun guardarTitulo(context: Context, uri: String, nuevoTitulo: String) {
        tituloMap[uri] = nuevoTitulo
        guardar(context)
    }

    fun getTitulo(uri: String): String? {
        return tituloMap[uri]
    }

    fun guardarCaratula(context: Context, uri: String, coverPath: String) {
        coversMap[uri] = coverPath
        guardar(context)
    }

    fun getCaratula(context: Context, uri: String): String? {
        coversMap[uri]?.let { coverPath ->
            if (File(coverPath).exists()) {
                return coverPath
            }
        }

        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, Uri.parse(uri))
            val embeddedPicture = retriever.embeddedPicture
            if (embeddedPicture != null) {
                saveCoverToFile(context, uri, embeddedPicture)?.let { coverPath ->
                    guardarCaratula(context, uri, coverPath)
                    return coverPath
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            retriever.release()
        }
        return null
    }

    private fun saveCoverToFile(context: Context, uri: String, imageData: ByteArray): String? {
        val filename = "cover_${uri.hashCode()}.png"
        val file = File(context.cacheDir, filename)
        return try {
            FileOutputStream(file).use { out ->
                out.write(imageData)
            }
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    // Persistencia en SharedPreferences
    fun guardar(context: Context) {
        val prefs = context.getSharedPreferences("song_metadata", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // Guardar covers
        for ((uri, cover) in coversMap) {
            editor.putString("cover_$uri", cover)
        }

        // Guardar títulos
        for ((uri, titulo) in tituloMap) {
            editor.putString("titulo_$uri", titulo)
        }

        editor.apply()
    }

    fun cargar(context: Context) {
        val prefs = context.getSharedPreferences("song_metadata", Context.MODE_PRIVATE)

        coversMap.clear()
        tituloMap.clear()

        prefs.all.forEach { (key, value) ->
            if (value is String) {
                if (key.startsWith("cover_")) {
                    val uri = key.removePrefix("cover_")
                    coversMap[uri] = value
                }
                if (key.startsWith("titulo_")) {
                    val uri = key.removePrefix("titulo_")
                    tituloMap[uri] = value
                }
            }
        }
    }
}
