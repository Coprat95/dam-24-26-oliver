package com.example.plantilla_crud

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Clase para conectarnos a la BBDD SQLite y realizar las operaciones CRUD.
 *
 * Vista de la tabla de la BBDD:
 * ----------------------------------------
 *              CLIENTES
 * ----------------------------------------
 *  id INTEGER PRIMARY KEY AUTOINCREMENT
 *  nombre TEXT NOT NULL
 *  numTelefono TEXT NOT NULL
 * ----------------------------------------
 */
class ClientesDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Clientes.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE clientes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                numTelefono TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS clientes")
        onCreate(db)
    }

    // ───────────────────────────────────────────────────────────────
    // CRUD: Crear, Leer, Actualizar, Borrar
    // ───────────────────────────────────────────────────────────────

    /** Inserta un cliente usando un objeto Cliente */
    fun insert(nuevoCliente: Cliente): Long {
        return insert(nuevoCliente.nombre, nuevoCliente.numTelefono)
    }

    /** Inserta un cliente con datos individuales */
    private fun insert(nombre: String, numTelefono: Long): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("numTelefono", numTelefono)
        }
        val newRowId = db.insert("clientes", null, values)
        db.close()
        return newRowId
    }

    /** Actualiza un cliente usando un objeto Cliente */
    fun update(idCliente: Int, cliente: Cliente): Int {
        return update(idCliente, cliente.nombre, cliente.numTelefono)
    }

    /** Actualiza un cliente con datos individuales */
    private fun update(idCliente: Int, nombre: String, numTelefono: Long): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("numTelefono", numTelefono)
        }
        val affectedRows = db.update("clientes", values, "id = ?", arrayOf(idCliente.toString()))
        db.close()
        return affectedRows
    }

    /** Elimina un cliente por su ID */
    fun delete(idCliente: Int): Int {
        val db = writableDatabase
        val affectedRows = db.delete("clientes", "id = ?", arrayOf(idCliente.toString()))
        db.close()
        return affectedRows
    }

    /** Devuelve el número total de clientes en la BBDD */
    fun getNumeroClientes(): Int {
        val db = readableDatabase
        val selectQuery = "SELECT count(*) as numClientes FROM clientes"
        val cursor = db.rawQuery(selectQuery, null)
        cursor.use {
            if (it.moveToFirst()) {
                return it.getInt(it.getColumnIndexOrThrow("numClientes"))
            }
        }
        return -1
    }

    /** Devuelve una lista con todos los clientes */
    @SuppressLint("Range")
    fun getListadoClientes(): List<Cliente> {
        val clientesList = mutableListOf<Cliente>()
        val db = readableDatabase
        val selectQuery = "SELECT * FROM clientes"
        // Crea objeto clase Cursor ( que tiene sus propios metodos )
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(cursor.getColumnIndex("nombre"))
                val numTelefono = cursor.getLong(cursor.getColumnIndex("numTelefono"))
                val cliente = Cliente(nombre, numTelefono)
                clientesList.add(cliente)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return clientesList
    }
}