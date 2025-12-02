package com.example.myapplication

import android.app.Application

class SongApplication : Application() {
    // Using by lazy so the database and repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { SongDatabase.getDatabase(this) }
    val repository by lazy { SongRepository(database.songDao(), this) }
}
