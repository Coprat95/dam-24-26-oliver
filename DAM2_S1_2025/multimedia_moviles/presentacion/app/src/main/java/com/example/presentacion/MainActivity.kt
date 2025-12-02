package com.example.presentacion

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

    val xboton = findViewById<Button>(R.id.botonX)
     xboton.setOnClickListener {
         Toast.makeText(this,"La app ha finalizado", Toast.LENGTH_LONG).show()
     }



    }
}
