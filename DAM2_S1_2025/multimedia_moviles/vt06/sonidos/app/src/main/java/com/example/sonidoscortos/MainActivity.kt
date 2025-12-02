package com.example.sonidoscortos

import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var sonidoCorto: SoundPool? = null
    private lateinit var sonidoLargo: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Inicializa SoundPool para sonidos cortos
        sonidoCorto = SoundPool(1, AudioManager.STREAM_MUSIC, 1)
        sonidoCorto?.load(this, R.raw.sonido_boton, 1)

        // Inicializa MediaPlayer para sonidos largos
        sonidoLargo = MediaPlayer.create(this, R.raw.sonido_cancion)
    }

    // Metodo vinculado al botón "Sonido corto" desde XML
    fun audioCorto(view: View) {
        sonidoCorto?.play(1, 1f, 1f, 1, 0, 1f)
    }

    // Metodo vinculado al botón "Sonido largo" desde XML
    fun audioLargo(view: View) {
        // --- MODIFICADO: Buena práctica ---
        // Comprobamos si ya está sonando para no reiniciarlo
        if (!sonidoLargo.isPlaying) {
            sonidoLargo.start()
        }
    }

    // --- AÑADIDO: Gestión del Ciclo de Vida (Pausa) ---
    override fun onPause() {
        super.onPause()
        // Si el usuario sale de la app (ej. va al Home) y la música está sonando,
        // pausamos el MediaPlayer.
        if (sonidoLargo.isPlaying) {
            sonidoLargo.pause()
        }
    }

    // --- AÑADIDO: Gestión del Ciclo de Vida (Destrucción) ---
    override fun onDestroy() {
        super.onDestroy()

        // ¡Crucial! Liberamos TODOS los recursos multimedia
        // para evitar fugas de memoria (Memory Leaks) cuando la app se cierra.

        // 1. Liberar SoundPool
        sonidoCorto?.release() // Libera los recursos nativos de SoundPool
        sonidoCorto = null     // Ayuda al Garbage Collector a limpiar la referencia

        // 2. Liberar MediaPlayer
        sonidoLargo.stop()     // Para la reproducción por si acaso
        sonidoLargo.release()  // Libera los recursos nativos de MediaPlayer
    }
}