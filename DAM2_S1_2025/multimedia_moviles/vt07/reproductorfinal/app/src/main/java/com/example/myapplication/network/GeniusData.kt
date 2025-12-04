package com.example.myapplication.network

import com.google.gson.annotations.SerializedName

// Estructura principal de la respuesta de la API de Genius
data class GeniusResponse(
    val response: GeniusSearchResponse?
)

// Contenedor de los resultados de búsqueda
data class GeniusSearchResponse(
    val hits: List<GeniusHit>?
)

// Cada resultado individual
data class GeniusHit(
    val result: GeniusResult?
)

// Detalles de la canción, incluyendo la carátula
data class GeniusResult(
    @SerializedName("song_art_image_url")
    val songArtImageUrl: String?
)
