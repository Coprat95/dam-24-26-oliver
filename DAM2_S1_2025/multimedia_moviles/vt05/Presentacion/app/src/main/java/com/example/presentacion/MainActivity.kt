package com.example.presentacion

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import com.example.presentacion.ui.TiempoAdapter
import com.example.presentacion.bbdd.*
import com.example.presentacion.ui.TiempoItem

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseSeeder: FirebaseSeeder
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var recyclerTiempos: RecyclerView
    private lateinit var tiempoAdapter: TiempoAdapter
    private var tiempoList = mutableListOf<TiempoItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseSeeder = FirebaseSeeder()
        firebaseManager = FirebaseManager()

        val btnSeedData = findViewById<Button>(R.id.btnSeedData)
        recyclerTiempos = findViewById(R.id.recyclerTiempos)
        recyclerTiempos.layoutManager = LinearLayoutManager(this)

        // 🔹 Inicializar el adaptador con la lista vacía
        tiempoAdapter = TiempoAdapter(tiempoList)
        recyclerTiempos.adapter = tiempoAdapter

        // 🔥 Cargar datos desde Firebase al iniciar la app
        loadTiempos()

        // 🔥 Insertar datos de prueba y recargar
        btnSeedData.setOnClickListener {
            firebaseSeeder.seedData(
                onSuccess = {
                    Toast.makeText(this, "Datos de prueba agregados", Toast.LENGTH_SHORT).show()
                    Log.d("MainActivity", "✅ Datos guardados, ahora cargamos los tiempos")
                    loadTiempos() // 🔥 Llamar a loadTiempos() SOLO cuando los datos ya estén en Firebase
                },
                onFailure = { exception ->
                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                    Log.e("MainActivity", "❌ Error en seedData: ${exception.message}")
                }
            )
        }
    }

    private fun loadTiempos() {
        tiempoList.clear() // Limpiar lista antes de cargar nuevos datos
        val puntos = listOf("punto1", "punto2", "meta")

        puntos.forEach { punto ->
            firebaseManager.getTiempos(
                punto,
                onDataReceived = { tiempos ->
                    tiempos.forEach { (idCorredor, tiempo) ->
                        val tiempoItem = TiempoItem(idCorredor, punto, tiempo)
                        if (!tiempoList.contains(tiempoItem)) { // Evita duplicados
                            tiempoList.add(tiempoItem)
                        }
                    }
                    runOnUiThread {
                        tiempoAdapter.notifyDataSetChanged() // Actualizar RecyclerView
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