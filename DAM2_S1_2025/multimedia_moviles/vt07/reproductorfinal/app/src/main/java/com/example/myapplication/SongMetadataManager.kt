package com.example.myapplication

import android.content.Context

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

    fun getCaratula(uri: String): String? {
        return coversMap[uri]
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
            if (key.startsWith("cover_")) {
                val uri = key.removePrefix("cover_")
                coversMap[uri] = value as String
            }
            if (key.startsWith("titulo_")) {
                val uri = key.removePrefix("titulo_")
                tituloMap[uri] = value as String
            }
        }
    }
}