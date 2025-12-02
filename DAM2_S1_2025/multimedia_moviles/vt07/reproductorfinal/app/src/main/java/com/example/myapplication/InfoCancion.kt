package com.example.myapplication

import java.io.Serializable

data class InfoCancion(
    var titulo: String,
    val artista: String,
    val album: String?,
    val audioResId: Int?,
    val audioUriString: String?,
    val fotoResId: Int,
    var customImageUriString: String? = null // Campo para la carátula personalizada
) : Serializable