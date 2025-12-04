package com.example.myapplication.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface LastFmApiService {
    @GET("?method=album.search&format=json")
    suspend fun searchAlbum(
        @Query("album") album: String,
        @Query("artist") artist: String,
        @Query("api_key") apiKey: String
    ): LastFmAlbumResponse

    @GET("?method=track.search&format=json")
    suspend fun searchTrack(
        @Query("track") track: String,
        @Query("artist") artist: String,
        @Query("api_key") apiKey: String
    ): LastFmTrackResponse
}

// Clases de respuesta para Álbumes
data class LastFmAlbumResponse(
    val results: AlbumResults?
)

data class AlbumResults(
    @SerializedName("albummatches") val albumMatches: AlbumMatches?
)

data class AlbumMatches(
    val album: List<LastFmAlbum>?
)

data class LastFmAlbum(
    val name: String,
    val artist: String,
    @SerializedName("image") val images: List<LastFmImage>?
)

// Clases de respuesta para Canciones (Tracks)
data class LastFmTrackResponse(
    val results: TrackResults?
)

data class TrackResults(
    @SerializedName("trackmatches") val trackMatches: TrackMatches?
)

data class TrackMatches(
    val track: List<LastFmTrack>?
)

data class LastFmTrack(
    val name: String,
    val artist: String,
    @SerializedName("image") val image: List<LastFmImage>?
)

data class LastFmImage(
    @SerializedName("#text") val url: String?,
    val size: String?
)
