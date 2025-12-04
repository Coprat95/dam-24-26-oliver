package com.example.myapplication.network

import android.util.Log
import com.example.myapplication.BuildConfig
import java.text.Normalizer

class CoverArtService {

    private val TAG = "CoverArtService"

    private fun normalize(input: String): String {
        var normalized = Normalizer.normalize(input.lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        normalized = normalized.replace(Regex("\\(.*?\\)|\\[.*?\\]"), "").trim()
        normalized = normalized.replace(Regex("official|video|lyric|audio|feat|ft|remix|edit"), "").trim()
        return normalized.replace(Regex("[^a-z0-9 ]"), " ").trim().replace(Regex("\\s+"), " ")
    }

    suspend fun getCoverArtUrl(artist: String, title: String, album: String?): String? {
        Log.d(TAG, "Buscando carátula para: Artista='$artist', Título='$title', Álbum='$album'")
        if (artist.isBlank() || artist == "<unknown>") {
            Log.w(TAG, "Búsqueda cancelada: el artista está vacío o es '<unknown>'.")
            return null
        }

        val normArtist = normalize(artist)
        val normTitle = normalize(title)
        val normAlbum = album?.let { normalize(it) }

        Log.d(TAG, "Búsqueda Normalizada -> Artista: '$normArtist', Título: '$normTitle', Álbum: '$normAlbum'")

        if (!normAlbum.isNullOrBlank()) {
            Log.d(TAG, "Fase 1: Búsqueda por álbum: '$normAlbum'")
            searchMusicBrainz(normArtist, normAlbum)?.let { return it }
            searchLastFm(normArtist, normAlbum)?.let { return it }
            searchDiscogs(normArtist, normAlbum)?.let { return it }
        }

        if (normTitle.isNotBlank()) {
            Log.d(TAG, "Fase 2: Búsqueda por título: '$normTitle'")
            searchMusicBrainz(normArtist, normTitle)?.let { return it }
            searchLastFm(normArtist, normTitle, isTrack = true)?.let { return it }
            searchDiscogs(normArtist, normTitle)?.let { return it }
        }

        Log.i(TAG, "Búsqueda finalizada sin resultados para '$artist' - '$title'.")
        return null
    }

    private suspend fun searchMusicBrainz(artist: String, release: String): String? {
        if (release.isBlank()) return null
        val query = "artist:\"$artist\" AND release:\"$release\""
        return try {
            Log.d(TAG, "--> MusicBrainz: Buscando con query='$query'")
            val response = RetrofitClient.musicBrainzApiService.searchRelease(query)
            val releaseId = response.releases?.firstOrNull()?.id
            if (releaseId != null) {
                val url = "https://coverartarchive.org/release/$releaseId/front"
                Log.i(TAG, "--> ¡ÉXITO! MusicBrainz encontró URL: $url")
                url
            } else {
                Log.d(TAG, "--> MusicBrainz: Sin resultados.")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "--> MusicBrainz: Error en la API: ${e.message}")
            null
        }
    }

    private suspend fun searchLastFm(artist: String, searchItem: String, isTrack: Boolean = false): String? {
        if (BuildConfig.LASTFM_API_KEY.isBlank()) {
            Log.w(TAG, "--> Last.fm: Clave de API no configurada en local.properties. Saltando búsqueda.")
            return null
        }
        if (searchItem.isBlank()) return null
        return try {
            val imageUrl: String?
            if (isTrack) {
                Log.d(TAG, "--> Last.fm: Buscando track '$searchItem' de '$artist'")
                val response = RetrofitClient.lastFmApiService.searchTrack(track = searchItem, artist = artist, apiKey = BuildConfig.LASTFM_API_KEY)
                imageUrl = response.results?.trackMatches?.track?.firstOrNull()?.image?.find { img -> img.size == "extralarge" }?.url
            } else {
                Log.d(TAG, "--> Last.fm: Buscando álbum '$searchItem' de '$artist'")
                val response = RetrofitClient.lastFmApiService.searchAlbum(album = searchItem, artist = artist, apiKey = BuildConfig.LASTFM_API_KEY)
                imageUrl = response.results?.albumMatches?.album?.firstOrNull()?.images?.find { img -> img.size == "extralarge" }?.url
            }

            if (!imageUrl.isNullOrBlank()) {
                Log.i(TAG, "--> ¡ÉXITO! Last.fm encontró URL: $imageUrl")
            } else {
                Log.d(TAG, "--> Last.fm: Sin resultados.")
            }
            imageUrl
        } catch (e: Exception) {
            Log.e(TAG, "--> Last.fm: Error en la API: ${e.javaClass.simpleName}: ${e.message}")
            null
        }
    }

    private suspend fun searchDiscogs(artist: String, releaseTitle: String): String? {
        if (BuildConfig.DISCOGS_API_TOKEN.isBlank()) {
            Log.w(TAG, "--> Discogs: Token de API no configurado en local.properties. Saltando búsqueda.")
            return null
        }
        if (releaseTitle.isBlank()) return null
        return try {
            Log.d(TAG, "--> Discogs: Buscando release '$releaseTitle' de '$artist'")
            val token = "Discogs token=${BuildConfig.DISCOGS_API_TOKEN}"
            val response = RetrofitClient.discogsApiService.searchRelease(
                token = token,
                artist = artist,
                releaseTitle = releaseTitle
            )
            val coverUrl = response.results?.firstOrNull()?.coverImage
            if (coverUrl != null) {
                Log.i(TAG, "--> ¡ÉXITO! Discogs encontró URL: $coverUrl")
            } else {
                Log.d(TAG, "--> Discogs: Sin resultados.")
            }
            coverUrl
        } catch (e: Exception) {
            Log.e(TAG, "--> Discogs: Error en la API: ${e.message}")
            null
        }
    }
}
