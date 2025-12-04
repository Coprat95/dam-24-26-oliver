package com.example.myapplication.network

import android.util.Log
import com.example.myapplication.BuildConfig
import java.text.Normalizer

class CoverArtService {

    private val TAG = "CoverArtService"

    // 1. Caché en memoria para las carátulas
    private val coverArtCache = mutableMapOf<String, String>()

    private val STOP_WORDS = setOf(
        "official", "video", "lyric", "audio", "remix", "edit", "videoclip",
        "official video", "video oficial", "visualizer", "letra", "prod", "performance"
    )

    private fun getCacheKey(artist: String, title: String, album: String?): String {
        return "${normalize(artist)}|${normalize(title)}|${normalize(album ?: "")}"
    }

    fun normalize(input: String): String {
        var normalized = Normalizer.normalize(input.lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        normalized = normalized.replace(Regex("\\.mp3|\\.flac|\\.wav|\\.m4a$"), "").trim()
        normalized = normalized.split('_')[0].trim()
        normalized = normalized.replace(Regex("\\(.*?\\)|\\[.*?\\]"), "").trim()
        
        normalized = STOP_WORDS.fold(normalized) { acc, word ->
            acc.replace(Regex("\\b$word\\b", RegexOption.IGNORE_CASE), "")
        }
        
        normalized = normalized.replace(Regex("[^a-z0-9,\\- ]"), " ").trim()
        return normalized.replace(Regex("\\s+"), " ")
    }

    fun parseInfoFromName(filename: String): Pair<String?, String?> {
        Log.d(TAG, "Parseando nombre de archivo original: $filename")
        val cleanedName = normalize(filename)
        val parts = cleanedName.split(Regex(" - ")).map { it.trim() }.filter { it.isNotEmpty() }

        if (parts.size >= 2) {
            val partA = parts[0]
            val partB = parts.subList(1, parts.size).joinToString(" - ")

            val artistKeywords = listOf("ft", "feat", " x ", ",", "&")
            val knownArtists = listOf("quevedo", "rels b", "rosalia", "mora", "c. tangana", "aitana", "yahritza y su esencia")

            val aHasKeywords = artistKeywords.any { partA.contains(it) } || knownArtists.any { partA.contains(it) }
            val bHasKeywords = artistKeywords.any { partB.contains(it) } || knownArtists.any { partB.contains(it) }

            val finalArtist: String
            val finalTitle: String

            if (bHasKeywords && !aHasKeywords) {
                finalArtist = partB
                finalTitle = partA
            } else {
                finalArtist = partA
                finalTitle = partB
            }
            
            val result = Pair(finalArtist.capitalizeAndFormat(), finalTitle.capitalizeAndFormat())
            Log.i(TAG, "Deducción final -> Artista='${result.first}', Título='${result.second}'")
            return result
        }

        Log.w(TAG, "No se pudo deducir 'Artista - Título' de '$cleanedName'.")
        return Pair(null, cleanedName.capitalizeAndFormat())
    }

    private fun String.capitalizeAndFormat(): String {
        return this.split(' ').joinToString(" ") { it.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }.trim()
    }

    suspend fun getCoverArtUrl(artist: String, title: String, album: String?): String? {
        Log.d(TAG, "Iniciando búsqueda de carátula para Artista='$artist', Título='$title'")
        if (artist.isBlank() || artist == "<unknown>" || artist == "Artista Desconocido" || title.isBlank()) {
            Log.w(TAG, "Búsqueda cancelada: artista o título inválido.")
            return null
        }

        // 2. Antes de lanzar la búsqueda, se consulte la caché.
        val originalKey = getCacheKey(artist, title, album)
        coverArtCache[originalKey]?.let {
            Log.d(TAG, "Carátula encontrada en caché para '$artist - $title'")
            return it
        }
    
        Log.d(TAG, "Probando Variante original -> Artista='$artist', Título='$title'")
        doSearch(artist, title, album)?.let {
            Log.i(TAG, "¡ÉXITO con la variante original! Guardando en caché.")
            coverArtCache[originalKey] = it // 4. Guardado en caché
            return it
        }

        val invertedKey = getCacheKey(title, artist, album)
        coverArtCache[invertedKey]?.let {
            Log.d(TAG, "Carátula encontrada en caché para '$title - $artist' (variante invertida)")
            return it
        }
    
        Log.d(TAG, "Probando Variante invertida -> Artista='$title', Título='$artist'")
        doSearch(title, artist, album)?.let {
            Log.i(TAG, "¡ÉXITO con la variante invertida! Guardando en caché.")
            coverArtCache[invertedKey] = it // 4. Guardado en caché
            return it
        }
        
        Log.w(TAG, "Búsqueda finalizada SIN ÉXITO para '$artist' - '$title' (ambas variantes probadas).")
        return null
    }

    private suspend fun doSearch(artist: String, title: String, album: String?): String? {
        Log.d(TAG, "doSearch (antes de normalizar): Artista='$artist', Título='$title'")
        val normalizedFullArtist = normalize(artist)
        val normTitle = normalize(title)
        Log.d(TAG, "doSearch (después de normalizar): Artista='$normalizedFullArtist', Título='$normTitle'")

        val normAlbum = album?.let { normalize(it) }
    
        val artistSeparators = Regex(",|\\bft\\b|\\bfeat\\b|&|\\bx\\b|\\band\\b|/")
        val individualArtists = normalizedFullArtist.split(artistSeparators).map { it.trim() }.filter { it.isNotEmpty() }
    
        val artistsToSearch = mutableListOf<String>()
        if (individualArtists.size > 1) {
            artistsToSearch.addAll(individualArtists)
            artistsToSearch.add(normalizedFullArtist.replace(artistSeparators, " ").replace(Regex("\\s+"), " ").trim())
        } else {
            artistsToSearch.add(normalizedFullArtist)
        }
    
        artistsToSearch.distinct().forEachIndexed { index, artistVariant ->
            val formattedArtist = artistVariant.capitalizeAndFormat()
            val formattedTitle = normTitle.capitalizeAndFormat()
            Log.d(TAG, "Buscando con sub-variante ${index + 1}: Artista='$formattedArtist', Título='$formattedTitle'")
    
            val searchAlbumTitle = normAlbum?.takeIf { it.isNotBlank() && it != "music" && it != "download" }
            if (searchAlbumTitle != null) {
                Log.d(TAG, "Fase 1 - Búsqueda por álbum: '$searchAlbumTitle'")
                searchMusicBrainz(artistVariant, searchAlbumTitle)?.let { return it }
                searchLastFm(artistVariant, searchAlbumTitle)?.let { return it }
                searchDiscogs(artistVariant, searchAlbumTitle)?.let { return it }
            }
    
            if (normTitle.isNotBlank()) {
                Log.d(TAG, "Fase 2 - Búsqueda por título: '$normTitle'")
                searchMusicBrainz(artistVariant, normTitle)?.let { return it }
                searchLastFm(artistVariant, normTitle, isTrack = true)?.let { return it }
                searchDiscogs(artistVariant, normTitle)?.let { return it }
                
                Log.d(TAG, "Fallback Genius -> Artista='$formattedArtist', Título='$formattedTitle'")
                searchGenius(artistVariant, normTitle)?.let { return it }
            }
        }
        
        return null
    }

    private suspend fun searchMusicBrainz(artist: String, release: String): String? {
        if (release.isBlank()) return null
        val query = "artist:\"$artist\" AND release:\"$release\""
        return try {
            val response = RetrofitClient.musicBrainzApiService.searchRelease(query)
            response.releases?.firstOrNull()?.id?.let {
                Log.i(TAG, "--> ¡ÉXITO! MusicBrainz encontró URL para $it")
                "https://coverartarchive.org/release/$it/front"
            }
        } catch (e: Exception) {
            Log.e(TAG, "--> MusicBrainz: Error en la API: ${e.message}")
            null
        }
    }

    private suspend fun searchLastFm(artist: String, searchItem: String, isTrack: Boolean = false): String? {
        if (BuildConfig.LASTFM_API_KEY.isBlank()) return null
        if (searchItem.isBlank()) return null
        return try {
            val response: Any = if (isTrack) {
                RetrofitClient.lastFmApiService.searchTrack(track = searchItem, artist = artist, apiKey = BuildConfig.LASTFM_API_KEY)
            } else {
                RetrofitClient.lastFmApiService.searchAlbum(album = searchItem, artist = artist, apiKey = BuildConfig.LASTFM_API_KEY)
            }
            val imageUrl = (response as? LastFmAlbumResponse)?.results?.albumMatches?.album?.firstOrNull()?.images?.find { it.size == "extralarge" }?.url
                ?: (response as? LastFmTrackResponse)?.results?.trackMatches?.track?.firstOrNull()?.image?.find { it.size == "extralarge" }?.url
            
            imageUrl?.takeIf { it.isNotBlank() }?.also { Log.i(TAG, "--> ¡ÉXITO! Last.fm encontró URL: $it") }
        } catch (e: Exception) {
            Log.e(TAG, "--> Last.fm: Error en la API: ${e.javaClass.simpleName}: ${e.message}")
            null
        }
    }

    private suspend fun searchDiscogs(artist: String, releaseTitle: String): String? {
        if (BuildConfig.DISCOGS_API_TOKEN.isBlank()) return null
        if (releaseTitle.isBlank()) return null
        return try {
            val token = "Discogs token=${BuildConfig.DISCOGS_API_TOKEN}"
            val response = RetrofitClient.discogsApiService.searchRelease(
                token = token,
                artist = artist,
                releaseTitle = releaseTitle
            )
            response.results?.firstOrNull()?.coverImage?.also { Log.i(TAG, "--> ¡ÉXITO! Discogs encontró URL: $it") }
        } catch (e: Exception) {
            Log.e(TAG, "--> Discogs: Error en la API: ${e.message}")
            null
        }
    }

    private suspend fun searchGenius(artist: String, title: String): String? {
        // TODO: Implementar la llamada a la API de Genius.
        // 1. Añadir el token de acceso de Genius a tu archivo local.properties y BuildConfig.
        // 2. Crear las data classes para la respuesta de la API de Genius.
        // 3. Añadir una nueva función a tu interfaz de Retrofit para la API de Genius.
        // 4. Actualizar tu RetrofitClient para incluir el servicio de la API de Genius.
        /*
        if (BuildConfig.GENIUS_API_TOKEN.isBlank()) return null
        return try {
            // Suponiendo que tienes un endpoint de búsqueda en tu geniusApiService
            val response = RetrofitClient.geniusApiService.search("$artist $title") 
            response.response.hits?.firstOrNull()?.result?.song_art_image_url?.also {
                Log.i(TAG, "--> ¡ÉXITO! Genius encontró URL: $it")
            }
        } catch (e: Exception) {
            Log.e(TAG, "--> Genius: Error en la API: ${e.message}")
            null
        }
        */
        return null
    }
}
