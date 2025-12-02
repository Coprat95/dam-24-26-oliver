package com.example.myapplication.network

import android.util.Log

class CoverArtService {

    suspend fun getCoverArtUrl(artist: String, releaseTitle: String): String? {
        val query = "artist:\"$artist\" AND release:\"$releaseTitle\""
        return try {
            val response = RetrofitClient.musicBrainzApiService.searchRelease(query)
            val releaseId = response.releases?.firstOrNull()?.id
            if (releaseId != null) {
                "https://coverartarchive.org/release/$releaseId/front"
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("CoverArtService", "Error fetching cover art: ${e.message}")
            null
        }
    }
}
