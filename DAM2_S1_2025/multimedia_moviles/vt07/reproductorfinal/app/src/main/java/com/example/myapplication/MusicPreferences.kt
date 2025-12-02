package com.example.myapplication

import android.content.Context
import com.google.gson.Gson

object MusicPreferences {
    private const val PREFS_NAME = "MusicPrefs"
    private const val LAST_SONG_KEY = "last_song"

    fun saveLastSong(context: Context, song: InfoCancion) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(song)
        editor.putString(LAST_SONG_KEY, json)
        editor.apply()
    }

    fun getLastSong(context: Context): InfoCancion? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(LAST_SONG_KEY, null)
        return if (json != null) {
            Gson().fromJson(json, InfoCancion::class.java)
        } else {
            null
        }
    }

    fun clearLastSong(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(LAST_SONG_KEY).apply()
    }
}