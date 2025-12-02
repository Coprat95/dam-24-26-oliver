package com.example.myapplication

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PlaylistManager {
    var playlists: MutableMap<String, ArrayList<InfoCancion>> = linkedMapOf()
    private const val PREFS_NAME = "PlaylistPrefs"
    private const val KEY_PLAYLISTS = "playlists"

    fun cargar(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_PLAYLISTS, null)
        if (json != null) {
            val type = object : TypeToken<LinkedHashMap<String, ArrayList<InfoCancion>>>() {}.type
            playlists = Gson().fromJson(json, type)

            // Filtrar canciones ocultas
            val iterator = playlists.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                entry.value.removeAll { song ->
                    song.audioUriString != null && HiddenSongsManager.isHidden(song.audioUriString)
                }
            }
            guardar(context) // Guardar los cambios
        }
    }

    fun guardar(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(playlists)
        editor.putString(KEY_PLAYLISTS, json)
        editor.apply()
    }

    fun obtenerNombres(): List<String> {
        return playlists.keys.toList()
    }

    fun obtenerCanciones(nombreLista: String): ArrayList<InfoCancion> {
        return playlists.getOrDefault(nombreLista, ArrayList())
    }

    fun crearPlaylist(context: Context, nombreLista: String) {
        if (!playlists.containsKey(nombreLista)) {
            playlists[nombreLista] = ArrayList()
            guardar(context)
        }
    }

    fun agregarCancion(context: Context, nombreLista: String, cancion: InfoCancion) {
        cargar(context)
        if (!playlists.containsKey(nombreLista)) {
            playlists[nombreLista] = ArrayList()
        }

        val uri = cancion.audioUriString
        val tituloPersistente = if (uri != null) SongMetadataManager.getTitulo(uri) else null
        val coverPersistente  = if (uri != null) SongMetadataManager.getCaratula(uri) else null

        val cancionAGuardar = cancion.copy(
            titulo = tituloPersistente ?: cancion.titulo,
            customImageUriString = coverPersistente ?: cancion.customImageUriString
        )

        playlists[nombreLista]!!.add(cancionAGuardar)
        guardar(context)
    }

    fun agregarCanciones(context: Context, nombreLista: String, canciones: List<InfoCancion>) {
        cargar(context)
        if (!playlists.containsKey(nombreLista)) {
            playlists[nombreLista] = ArrayList()
        }
        playlists[nombreLista]?.addAll(canciones)
        guardar(context)
    }

    fun actualizarPlaylist(context: Context, nombreLista: String, canciones: ArrayList<InfoCancion>) {
        playlists[nombreLista] = canciones
        guardar(context)
    }
}