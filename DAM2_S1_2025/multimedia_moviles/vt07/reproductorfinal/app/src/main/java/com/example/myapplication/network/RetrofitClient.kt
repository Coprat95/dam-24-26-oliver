package com.example.myapplication.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val MUSICBRAINZ_BASE_URL = "https://musicbrainz.org/"

    val musicBrainzApiService: MusicBrainzApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(MUSICBRAINZ_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(MusicBrainzApiService::class.java)
    }
}
