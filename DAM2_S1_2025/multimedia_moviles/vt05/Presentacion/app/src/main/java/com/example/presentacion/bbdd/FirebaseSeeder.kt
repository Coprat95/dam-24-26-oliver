package com.example.presentacion.bbdd

import android.util.Log
import com.example.presentacion.modelos.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseSeeder {
    private val database: DatabaseReference = FirebaseDatabase.getInstance(
        "https://maraton2-7ae6e-default-rtdb.europe-west1.firebasedatabase.app"
    ).reference
    fun seedData(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val corredores = mapOf(
            "id1" to Corredor("id1", "Juan", "Pérez", "12345678A"),
            "id2" to Corredor("id2", "Ana", "García", "87654321B")
        )
        val tiempos = mapOf(
            "punto1" to mapOf(
                "id1" to "00:12:30",
                "id2" to "00:13:15"
            ),
            "punto2" to mapOf(
                "id1" to "00:25:45",
                "id2" to "00:27:10"
            ),
            "meta" to mapOf(
                "id1" to "01:45:23",
                "id2" to "01:52:30"
            )
        )
        // Agregar corredores
        database.child("corredores").setValue(corredores)
            .addOnSuccessListener {
                Log.d("FirebaseSeeder", "Corredores agregados correctamente")
                // Agregar tiempos solo si los corredores se insertan correctamente
                database.child("tiempos").setValue(tiempos)
                    .addOnSuccessListener {
                        Log.d("FirebaseSeeder", "Tiempos agregados correctamente")
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        Log.e(
                            "FirebaseSeeder", "Error agregando tiempos:$ { exception.message }"
                        )
                        onFailure(exception)
                    }
            }
            .addOnFailureListener { exception ->
                Log.e(
                    "FirebaseSeeder", "Error agregando corredores:$ { exception.message }"
                )
                onFailure(exception)
            }
    }
}