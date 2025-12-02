package com.example.myapplication

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SongDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(songs: List<Song>)

    @Query("SELECT * FROM songs WHERE NOT isHidden ORDER BY titulo ASC")
    fun pagingSource(): PagingSource<Int, Song>

    @Query("UPDATE songs SET isHidden = 1 WHERE audioUriString IN (:uris)")
    suspend fun hideSongs(uris: List<String>)

    @Query("UPDATE songs SET customImageUriString = :coverUri WHERE audioUriString = :songUri")
    suspend fun updateCoverArt(songUri: String, coverUri: String)

    @Query("SELECT * FROM songs WHERE audioUriString = :uri")
    suspend fun getSongByUri(uri: String): Song?

    @Query("DELETE FROM songs")
    suspend fun clearAll()
}
