package com.example.myapplication.network

import retrofit2.http.GET
import retrofit2.http.Query

interface MusicBrainzApiService {
    @GET("ws/2/release/")
    suspend fun searchRelease(
        @Query("query") query: String,
        @Query("fmt") format: String = "json"
    ): MusicBrainzResponse
}
