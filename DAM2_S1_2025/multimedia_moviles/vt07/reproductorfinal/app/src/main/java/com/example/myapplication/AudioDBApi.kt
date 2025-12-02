package com.example.myapplication

import retrofit2.http.GET
import retrofit2.http.Query

interface AudioDbApi {
    @GET("searchtrack.php")
    suspend fun searchTrack(@Query("s") artist: String, @Query("t") track: String): TrackResponse
}

data class TrackResponse(
    val track: List<Track>?
)

data class Track(
    val strTrackThumb: String?
)
