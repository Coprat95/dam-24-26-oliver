package com.example.myapplication.network

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

    // Interceptor para añadir User-Agent a MusicBrainz
    private val musicBrainzInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .header("User-Agent", "Neonbeat/1.0 ( oliver.gomez.dam@gmail.com )")
            .build()
        chain.proceed(request)
    }

    // Interceptor de registro para ver las peticiones y respuestas de la API
    private val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            android.util.Log.d("OkHttp", message)
        }
    }).apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Gson "lenient" (tolerante) para el JSON malformado
    private val lenientGson = GsonBuilder()
        .setLenient()
        .create()

    // Función genérica para crear un cliente OkHttp
    private fun createClient(vararg interceptors: Interceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()
        interceptors.forEach { builder.addInterceptor(it) }
        builder.addInterceptor(loggingInterceptor)
        return builder.build()
    }

    val musicBrainzApiService: MusicBrainzApiService by lazy {
        Retrofit.Builder()
            .baseUrl(MUSICBRAINZ_BASE_URL)
            .client(createClient(musicBrainzInterceptor))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MusicBrainzApiService::class.java)
    }

    val lastFmApiService: LastFmApiService by lazy {
        Retrofit.Builder()
            .baseUrl(LASTFM_BASE_URL)
            .client(createClient())
            .addConverterFactory(GsonConverterFactory.create(lenientGson))
            .build()
            .create(LastFmApiService::class.java)
    }

    val discogsApiService: DiscogsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(DISCOGS_BASE_URL)
            .client(createClient())
            .addConverterFactory(GsonConverterFactory.create(lenientGson)) // USAMOS EL PARSER TOLERANTE TAMBIÉN AQUÍ
            .build()
            .create(DiscogsApiService::class.java)
    }
}
