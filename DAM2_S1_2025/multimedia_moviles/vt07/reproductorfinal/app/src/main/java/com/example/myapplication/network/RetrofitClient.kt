package com.example.myapplication.network

import com.example.myapplication.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val MUSICBRAINZ_BASE_URL = "https://musicbrainz.org/ws/2/"
    private const val LASTFM_BASE_URL = "https://ws.audioscrobbler.com/2.0/"
    private const val DISCOGS_BASE_URL = "https://api.discogs.com/"
    private const val GENIUS_BASE_URL = "https://api.genius.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val lenientGson = GsonBuilder()
        .setLenient()
        .create()

    private val musicBrainzClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "Neonbeat/1.0 (oliver.gomez.dam@gmail.com)")
                .build()
            chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor)
        .build()

    private val discogsClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Authorization", "Discogs token=${BuildConfig.DISCOGS_API_TOKEN}")
                .build()
            chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor)
        .build()

    private val lastFmClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val geniusClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val musicBrainzApiService: MusicBrainzApiService by lazy {
        Retrofit.Builder()
            .baseUrl(MUSICBRAINZ_BASE_URL)
            .client(musicBrainzClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MusicBrainzApiService::class.java)
    }

    val lastFmApiService: LastFmApiService by lazy {
        Retrofit.Builder()
            .baseUrl(LASTFM_BASE_URL)
            .client(lastFmClient)
            .addConverterFactory(GsonConverterFactory.create(lenientGson))
            .build()
            .create(LastFmApiService::class.java)
    }

    val discogsApiService: DiscogsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(DISCOGS_BASE_URL)
            .client(discogsClient)
            .addConverterFactory(GsonConverterFactory.create(lenientGson))
            .build()
            .create(DiscogsApiService::class.java)
    }

    val geniusApiService: GeniusApiService by lazy {
        Retrofit.Builder()
            .baseUrl(GENIUS_BASE_URL)
            .client(geniusClient)
            .addConverterFactory(GsonConverterFactory.create(lenientGson))
            .build()
            .create(GeniusApiService::class.java)
    }
}
