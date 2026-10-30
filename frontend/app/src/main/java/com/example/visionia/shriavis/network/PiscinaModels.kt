package com.example.visionia.shriavis.network

data class PiscinaResponse(
    val id_piscina: Int,
    val nombre_piscina: String,
    val latitud: Double?,
    val longitud: Double?,
    val ubicacion_piscina: String
)

data class RegistrarPiscinaRequest(
    val nombre_piscina: String
)

data class RegistrarPiscinaResponse(
    val mensaje: String,
    val id_piscina: Int,
    val nombre_piscina: String,
    val latitud: Double?,
    val longitud: Double?,
    val ubicacion_piscina: String
)

data class ActualizarUbicacionRequest(
    val latitud: Double,
    val longitud: Double
)