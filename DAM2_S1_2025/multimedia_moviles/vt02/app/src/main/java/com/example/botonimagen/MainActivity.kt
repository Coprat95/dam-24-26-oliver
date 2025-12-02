package com.example.botonimagen

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val b1 = findViewById<ImageButton>(R.id.boton1)
        val b2 = findViewById<ImageButton>(R.id.boton2)
        val b3 = findViewById<ImageButton>(R.id.boton3)
        val b4 = findViewById<ImageButton>(R.id.boton4)
        val b5 = findViewById<ImageButton>(R.id.boton5)

        b1.setOnClickListener {
            seleccion(b1)
        }
        b2.setOnClickListener {
            seleccion(b2)
        }
        b3.setOnClickListener {
            seleccion(b3)
        }
        b4.setOnClickListener {
            seleccion(b4)
        }
        b5.setOnClickListener {
            seleccion(b5)
        }


    }


    // crear funcion en Kotlin
    fun seleccion(view: View) {

        // Sintaxis del switch ( when en Kotlin)
        when (view.getId()) {
            R.id.boton1 -> {
                Toast.makeText(this, "Esta es la imagen 1", Toast.LENGTH_SHORT).show()
            }

            R.id.boton2 -> {
                Toast.makeText(this, "Esta es la imagen 2", Toast.LENGTH_SHORT).show()
            }

            R.id.boton3 -> {
                Toast.makeText(this, "Esta es la imagen 3", Toast.LENGTH_SHORT).show()
            }

            R.id.boton4 -> {
                Toast.makeText(this, "Esta es la imagen 4", Toast.LENGTH_SHORT).show()
            }

            R.id.boton5 -> {
                Toast.makeText(this, "Esta es la imagen 5", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
