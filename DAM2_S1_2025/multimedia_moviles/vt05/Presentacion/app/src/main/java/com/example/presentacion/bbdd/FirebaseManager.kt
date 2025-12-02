package com.example.presentacion.bbdd

import com.google.firebase.database.*
import com.example.presentacion.modelos.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseManager {
    private val database: DatabaseReference = FirebaseDatabase.getInstance(
        "https://maraton2-7ae6e-default-rtdb.europe-west1.firebasedatabase.app"

    ).reference

    // 🔹 Agregar un corredor a la base de datos
    fun addCorredor(corredor: Corredor, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val corredorRef = database.child("corredores").child(corredor.id)
        corredorRef.setValue(corredor)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    // 🔹 Obtener la lista de corredores
    fun getCorredores(onDataReceived: (List<Corredor>) -> Unit, onFailure: (Exception) -> Unit) {
        database.child("corredores").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val corredores = mutableListOf<Corredor>()
                for (child in snapshot.children) {
                    val corredor = child.getValue(Corredor::class.java)
                    corredor?.let { corredores.add(it) }
                }
                onDataReceived(corredores)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.toException())
            }
        })
    }

    // 🔹 Actualizar un corredor
    fun updateCorredor(corredor: Corredor, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val corredorRef = database.child("corredores").child(corredor.id)
        corredorRef.setValue(corredor)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    // 🔹 Eliminar un corredor
    fun deleteCorredor(idCorredor: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        database.child("corredores").child(idCorredor).removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    // 🔹 Agregar un tiempo en un punto de control
    fun addTiempo(
        punto: String,
        tiempo: Tiempo,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val tiempoRef = database.child("tiempos").child(punto).child(tiempo.idCorredor)
        tiempoRef.setValue(tiempo.tiempo)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    // 🔹 Obtener tiempos por punto de control
    fun getTiempos(
        punto: String,
        onDataReceived: (Map<String, String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        database.child("tiempos").child(punto).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tiempos = mutableMapOf<String, String>()
                for (child in snapshot.children) {
                    tiempos[child.key!!] = child.value.toString()
                }
                onDataReceived(tiempos)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.toException())
            }
        })
    }

    // 🔹 Eliminar un tiempo registrado en un punto de control
    fun deleteTiempo(
        punto: String,
        idCorredor: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        database.child("tiempos").child(punto).child(idCorredor).removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
}