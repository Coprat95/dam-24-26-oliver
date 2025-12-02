package com.example.oraculo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Variables para guardar la fecha seleccionada
    private var dia = 1
    private var mes = 1
    private var anio = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referencias de la UI
        val btnEnviar: Button = findViewById(R.id.botonSiguiente)
        val txtNombre: EditText = findViewById(R.id.editTextNombre)
        val calendario: CalendarView = findViewById(R.id.calendarView)

        // Listener para capturar la fecha seleccionada
        calendario.setOnDateChangeListener { _, year, month, dayOfMonth ->
            dia = dayOfMonth
            mes = month + 1 // Los meses empiezan en 0
            anio = year
        }

        // Programamos el botón siguiente
        btnEnviar.setOnClickListener {
            val nombre = txtNombre.text.toString().trim()

            // Validación básica
            if (nombre.isBlank()) {
                Toast.makeText(this, "Por favor, introduce tu nombre", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Mostrar la fecha seleccionada
            Toast.makeText(
                this,
                "$nombre Fecha seleccionada: $dia/$mes/$anio",
                Toast.LENGTH_SHORT
            ).show()

            // Enviar datos a la siguiente Activity
            val intentFuturo = Intent(this, VerFuturo::class.java)
            intentFuturo.putExtra("nombre", nombre)
            intentFuturo.putExtra("dia", dia)
            intentFuturo.putExtra("mes", mes)
            startActivity(intentFuturo)
        }
    }
}