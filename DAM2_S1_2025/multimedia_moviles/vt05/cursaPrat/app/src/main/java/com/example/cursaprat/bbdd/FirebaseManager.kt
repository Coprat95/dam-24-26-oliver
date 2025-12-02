package com.example.cursaprat.bbdd

import android.util.Log
import com.google.firebase.database.*

class FirebaseManager {

    private val db = FirebaseDatabase.getInstance()

    fun getTiempos(
        punto: String,
        onDataReceived: (Map<String, String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val ref = db.getReference(punto)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tiempos = mutableMapOf<String, String>()
                for (child in snapshot.children) {
                    val idCorredor = child.key ?: continue
                    val tiempo = child.getValue(String::class.java) ?: continue
                    tiempos[idCorredor] = tiempo
                }
                Log.d("FirebaseManager", "✅ Tiempos recibidos desde $punto: $tiempos")
                onDataReceived(tiempos)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseManager", "❌ Error al leer tiempos: ${error.message}")
                onFailure(error.toException())
            }
        })
    }
}