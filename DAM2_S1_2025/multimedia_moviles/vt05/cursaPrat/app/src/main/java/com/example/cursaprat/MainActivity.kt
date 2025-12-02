package com.example.cursaprat

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cursaprat.bbdd.FirebaseManager
import com.example.cursaprat.bbdd.FirebaseSeeder
import com.example.cursaprat.ui.TiempoAdapter
import com.example.cursaprat.ui.TiempoItem

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseSeeder: FirebaseSeeder
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var recyclerTiempos: RecyclerView
    private lateinit var tiempoAdapter: TiempoAdapter
    private var tiempoList = mutableListOf<TiempoItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            firebaseSeeder = FirebaseSeeder()
            firebaseManager = FirebaseManager()

            val btnSeedData = findViewById<Button>(R.id.btnSeedData)
            recyclerTiempos = findViewById(R.id.recyclerTiempos)
            recyclerTiempos.layoutManager = LinearLayoutManager(this)

            tiempoAdapter = TiempoAdapter(tiempoList)
            recyclerTiempos.adapter = tiempoAdapter

            loadTiempos()

            btnSeedData.setOnClickListener {
                firebaseSeeder.seedData(
                    onSuccess = {
                        Toast.makeText(this, "Datos de prueba agregados", Toast.LENGTH_SHORT).show()
                        Log.d("MainActivity", "✅ Datos guardados, ahora cargamos los tiempos")
                        loadTiempos()
                    },
                    onFailure = { exception ->
                        Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                        Log.e("MainActivity", "❌ Error en seedData: ${exception.message}")
                    }
                )
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error en onCreate: ${e.message}")
        }
    }

    private fun loadTiempos() {
        tiempoList.clear()
        val puntos = listOf("punto1", "punto2", "meta")

        puntos.forEach { punto ->
            firebaseManager.getTiempos(
                punto,
                onDataReceived = { tiempos ->
                    tiempos.forEach { (idCorredor, tiempo) ->
                        val tiempoItem = TiempoItem(idCorredor, punto, tiempo)
                        if (!tiempoList.contains(tiempoItem)) {
                            tiempoList.add(tiempoItem)
                        }
                    }
                    runOnUiThread {
                        tiempoAdapter.notifyDataSetChanged()
                    }
                },
                onFailure = { exception ->
                    runOnUiThread {
                        Toast.makeText(this, "Error al cargar tiempos: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}