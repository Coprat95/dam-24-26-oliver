package com.example.myapplication

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object HiddenSongsManager {
    private const val PREFS_NAME = "HiddenSongsPrefs"
    private const val KEY_HIDDEN_SONGS = "hidden_songs"
    private var hiddenSongs: MutableSet<String> = mutableSetOf()

    fun load(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_HIDDEN_SONGS, null)
        if (json != null) {
            val type = object : TypeToken<MutableSet<String>>() {}.type
            hiddenSongs = Gson().fromJson(json, type)
        }
    }

    fun save(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(hiddenSongs)
        editor.putString(KEY_HIDDEN_SONGS, json)
        editor.apply()
    }

    fun hideSong(context: Context, songUri: String) {
        hiddenSongs.add(songUri)
        save(context)
    }

    fun unhideSong(context: Context, songUri: String) {
        hiddenSongs.remove(songUri)
        save(context)
    }

    fun isHidden(songUri: String): Boolean {
        return hiddenSongs.contains(songUri)
    }
}