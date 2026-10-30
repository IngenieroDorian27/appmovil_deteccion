
package com.example.visionia.network

data class CantidadPiscinasResponse(
    val cantidad_piscinas: Int
)

data class TotalMonitoreosResponse(
    val total_monitoreos: Int
)

data class NotificacionesPendientesResponse(
    val mensaje: String,
    val contador_actualizado: Boolean,
    val notificaciones_pendientes: Int
)

