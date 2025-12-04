package com.example.myapplication.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DiscogsApiService {
    @GET("database/search")
    suspend fun searchRelease(
        @Header("Authorization") token: String,
        @Query("artist") artist: String,
        @Query("release_title") releaseTitle: String,
        @Query("type") type: String = "release"
    ): DiscogsSearchResponse
}

data class DiscogsSearchResponse(
    val results: List<DiscogsRelease>?
)

data class DiscogsRelease(
    @SerializedName("cover_image")
    val coverImage: String?
)
