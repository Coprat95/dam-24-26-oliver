package com.example.myapplication.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val MUSICBRAINZ_BASE_URL = "https://musicbrainz.org/"

    private val httpClient = OkHttpClient.Builder().addInterceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("User-Agent", "Neonbeat/1.0")
        val request = requestBuilder.build()
        chain.proceed(request)
    }.build()

    val musicBrainzApiService: MusicBrainzApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(MUSICBRAINZ_BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(MusicBrainzApiService::class.java)
    }
}
