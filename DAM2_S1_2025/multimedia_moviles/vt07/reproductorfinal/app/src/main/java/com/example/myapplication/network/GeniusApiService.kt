package com.example.myapplication.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GeniusApiService {
    @GET("search")
    suspend fun search(
        @Header("Authorization") token: String,
        @Query("q") query: String
    ): GeniusResponse
}
