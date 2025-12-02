package com.example.plantilla_crud

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast

/**
 * Actividad principal que muestra el número de clientes y su listado.
 * También ejecuta operaciones CRUD básicas como ejemplo reutilizable.
 */
class MainActivity : AppCompatActivity() {

    companion object {
        /**
         * Convierte una lista de clientes en un String legible para mostrar en pantalla.
         * @param listadoClientes lista de objetos Cliente
         * @return texto con cada cliente en una línea
         */
        fun listadoClientesToString(listadoClientes: List<Cliente>): String {
            var listado = ""
            listadoClientes.forEach { elemento ->
                listado += "Cliente: $elemento\n"
            }
            return listado
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bbddClientes = ClientesDatabaseHelper(this)

        // 🔹 INSERTAR un cliente de ejemplo
        val clienteInsertado = Cliente("Oliver", 68686868)
        val idInsertado = bbddClientes.insert(clienteInsertado)
        Toast.makeText(this, "Insertado con ID: $idInsertado", Toast.LENGTH_SHORT).show()

        // 🔄 ACTUALIZAR ese cliente
        val clienteActualizado = Cliente("Oliver Actualizado", 69999999)
        val filasModificadas = bbddClientes.update(idInsertado.toInt(), clienteActualizado)
        Toast.makeText(this, "Filas modificadas: $filasModificadas", Toast.LENGTH_SHORT).show()

        // ❌ ELIMINAR ese cliente
        val filasEliminadas = bbddClientes.delete(idInsertado.toInt())
        Toast.makeText(this, "Filas eliminadas: $filasEliminadas", Toast.LENGTH_SHORT).show()

        // 🔍 Mostrar número total de clientes
        val numClientes = bbddClientes.getNumeroClientes()
        val txtNumClientes: TextView = findViewById(R.id.txtNumClientes)
        txtNumClientes.text = "$numClientes clientes en la BBDD"

        // 📋 Mostrar listado de clientes
        val listadoClientes: List<Cliente> = bbddClientes.getListadoClientes()
        val txtListadoClientes: TextView = findViewById(R.id.txtListado_Clientes)
        txtListadoClientes.text = listadoClientesToString(listadoClientes)
    }
}