package com.example.visionia.network


data class RegistroCapturaResponse(
    val mensaje: String,
    val id_alerta_superficie: Int,
    val notificacion: String,
    val fecha_alerta: String,
    val hora_alerta: String,
    val piscina: String,
    val imagen_capturada: String,
    val id_usuario: Int,
    val visto: Boolean,
    val latitud: Double?,
    val longitud: Double?
)

data class RegistroCapturaAcumulacionResponse(
    val mensaje: String,
    val id_alerta_acumulacion: Int,
    val notificacion: String,
    val fecha_alerta: String,
    val hora_alerta: String,
    val piscina: String,
    val imagen_capturada: String,
    val id_usuario: Int,
    val visto: Boolean,
    val latitud: Double?,
    val longitud: Double?
)

data class RegistroCapturaEspumaResponse(
    val mensaje: String,
    val id_alerta_espuma: Int,
    val notificacion: String,
    val fecha_alerta: String,
    val hora_alerta: String,
    val piscina: String,
    val imagen_capturada: String,
    val id_usuario: Int,
    val visto: Boolean,
    val latitud: Double?,
    val longitud: Double?
)

data class MarcarVistaResponse(
    val mensaje: String,
    val contador_actualizado: Boolean
)