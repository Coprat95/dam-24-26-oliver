package com.example.cursaprat.bbdd

import android.util.Log
import com.google.firebase.database.FirebaseDatabase

class FirebaseSeeder {

    private val db = FirebaseDatabase.getInstance()

    fun seedData(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val tiempos = mapOf(
            "punto1" to mapOf(
                "101" to "00:05:12",
                "102" to "00:05:45"
            ),
            "punto2" to mapOf(
                "101" to "00:10:30",
                "102" to "00:11:00"
            ),
            "meta" to mapOf(
                "101" to "00:15:20",
                "102" to "00:16:10"
            )
        )

        try {
            tiempos.forEach { punto, corredores ->
                val ref = db.getReference(punto)
                ref.setValue(corredores)
            }
            Log.d("FirebaseSeeder", "✅ Datos de prueba insertados correctamente")
            onSuccess()
        } catch (e: Exception) {
            Log.e("FirebaseSeeder", "❌ Error al insertar datos: ${e.message}")
            onFailure(e)
        }
    }
}