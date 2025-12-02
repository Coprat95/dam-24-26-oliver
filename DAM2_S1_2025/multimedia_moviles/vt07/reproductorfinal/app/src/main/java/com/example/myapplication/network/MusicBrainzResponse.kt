package com.example.myapplication.network

import com.google.gson.annotations.SerializedName

data class MusicBrainzResponse(
    val releases: List<Release>?
)

data class Release(
    val id: String,
    val title: String,
    @SerializedName("artist-credit") val artistCredit: List<ArtistCredit>?
)

data class ArtistCredit(
    val name: String,
    val artist: Artist
)

data class Artist(
    val id: String,
    val name: String
)
