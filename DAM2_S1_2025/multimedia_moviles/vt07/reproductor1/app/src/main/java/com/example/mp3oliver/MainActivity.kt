package com.example.mp3oliver

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.media.AudioManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView // Para mostrar el título de la canción
import android.util.Log // Importación para la depuración
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

// Clase de Datos para mantener la información legible de cada canción
data class InfoCancion(
    val titulo: String,
    val artista: String,
    val audioId: Int, // ID del recurso de audio (R.raw.nombre_archivo)
    val fotoId: Int   // ID del recurso de la carátula (R.drawable.nombre_foto)
)

class MainActivity : AppCompatActivity() {
    private var reproducirPausar: Button? = null
    private var imagenCancion: ImageView? = null
    private var textoTitulo: TextView? = null

    // Lista maestra de información (legible)
    private lateinit var listaCanciones: Array<InfoCancion>

    // Array de MediaPlayers (Instancias funcionales). Tamaño 4
    private var canciones: Array<MediaPlayer?> = arrayOfNulls(4)
    private var posicion: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Forzamos el control de volumen al flujo de música
        volumeControlStream = AudioManager.STREAM_MUSIC

        // 1. Vinculamos variables de UI
        imagenCancion = findViewById(R.id.imagenCancion)
        reproducirPausar = findViewById(R.id.buttonPlay)
        textoTitulo = findViewById(R.id.textoTitulo)

        // 2. Definición del catálogo de canciones (SINCRONIZACIÓN FINAL CON TUS ARCHIVOS)
        listaCanciones = arrayOf(
            // ATENCIÓN: Estos nombres deben ser EXACTOS a tus archivos en minúsculas y guiones bajos
            InfoCancion("La Perla", "ROSALÍA", R.raw.rosalia_la_perla, R.drawable.foto_rosalia_laperla),
            InfoCancion("Session #2", "DY & BIZA", R.raw.dy_biza_session, R.drawable.foto_dy_biza_session),
            InfoCancion("Superestrella", "AITANA", R.raw.aitana_superestrella, R.drawable.foto_aitana_superestrella),
            InfoCancion("Tú Vas Sin", "RELS B", R.raw.rels_b_tu_vas_sin, R.drawable.foto_relsb_tuvassin)
        )

        // 3. Crear instancias de MediaPlayer y establecer la UI inicial
        recargarCanciones()
        actualizarUI()
    }

    // --- MÉTODOS PÚBLICOS ASIGNADOS AL XML (onclick) ---

    fun playPause(view: View?) {
        if (canciones[posicion]?.isPlaying() == true) {
            canciones[posicion]?.pause()
            Log.d("MP3_DEBUG", "Canción pausada: ${listaCanciones[posicion].titulo}")
        } else {
            canciones[posicion]?.start()
            Log.d("MP3_DEBUG", "Canción reproducida: ${listaCanciones[posicion].titulo}")
        }
        actualizarUI() // Actualiza el icono de Play/Pause
    }

    fun stop(view: View?) {
        canciones[posicion]?.stop()
        recargarCanciones() // Regenera las instancias
        posicion = 0
        actualizarUI() // Muestra la primera carátula y el icono Play
        Log.d("MP3_DEBUG", "Reproducción detenida y reseteada a posición 0.")
    }

    fun anterior(view: View?) {
        canciones[posicion]?.stop()
        recargarCanciones() // Regenera las instancias

        if (posicion == 0) {
            posicion = canciones.size - 1
        } else {
            posicion--
        }

        canciones[posicion]?.start()
        actualizarUI()
    }

    fun siguiente(view: View?) {
        canciones[posicion]?.stop()
        recargarCanciones() // Regenera las instancias

        if (posicion < canciones.size - 1) {
            posicion++
        } else {
            posicion = 0
        }

        canciones[posicion]?.start()
        actualizarUI()
    }

    // --- MÉTODOS PRIVADOS AUXILIARES ---

    private fun recargarCanciones(){
        // Libera los recursos de las instancias viejas
        for (i in canciones.indices) {
            canciones[i]?.release()
        }

        // Crea las nuevas instancias (con depuración)
        for (i in listaCanciones.indices) {
            canciones[i] = MediaPlayer.create(this, listaCanciones[i].audioId)

            if (canciones[i] == null) {
                // Si falla aquí, el nombre no existe o el MP3 está corrupto
                Log.e("MP3_ERROR", "No se pudo crear MediaPlayer para: ${listaCanciones[i].titulo}. Revise el nombre en res/raw.")
            } else {
                Log.d("MP3_DEBUG", "MediaPlayer CREADO correctamente para: ${listaCanciones[i].titulo}")
            }
        }
    }

    private fun actualizarUI(){
        val cancionActual = listaCanciones[posicion]

        textoTitulo?.text = "${cancionActual.titulo} - ${cancionActual.artista}"
        imagenCancion?.setImageResource(cancionActual.fotoId)

        if (canciones[posicion]?.isPlaying() == true){
            reproducirPausar?.setBackgroundResource(R.drawable.pause)
        } else {
            reproducirPausar?.setBackgroundResource(R.drawable.play)
        }
    }

    // --- GESTIÓN DEL CICLO DE VIDA (Obligatorio) ---
    override fun onDestroy() {
        super.onDestroy()
        for (i in canciones.indices) {
            canciones[i]?.release()
        }
    }
}