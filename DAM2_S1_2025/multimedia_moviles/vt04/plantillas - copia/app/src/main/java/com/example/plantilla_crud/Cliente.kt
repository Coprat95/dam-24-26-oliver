package com.example.plantilla_crud

/**
 * Clase de datos que representa un Cliente en la BBDD.
 *
 * Esta clase se usa para:
 * - Insertar nuevos clientes
 * - Actualizar clientes existentes
 * - Mostrar clientes en pantalla
 * - Convertir resultados de la BBDD en objetos Kotlin
 */
data class Cliente(
    val nombre: String,       // Nombre del cliente
    val numTelefono: Long     // Número de teléfono del cliente
)