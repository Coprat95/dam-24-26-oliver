package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey val audioUriString: String,
    val titulo: String,
    val artista: String?,
    var customImageUriString: String?,
    var isHidden: Boolean = false
)
