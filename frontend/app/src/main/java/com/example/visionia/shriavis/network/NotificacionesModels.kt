package com.example.visionia.shriavis.network



data class NotificacionPendienteResponse(
    val id_alerta_superficie: Int,
    val notificacion: String,
    val fecha_alerta: String,
    val hora_alerta: String,
    val piscina: String,
    val imagen_capturada: String,
    val id_usuario: Int,
    val nombre_operario: String,
    val visto: Boolean,
    val latitud: Double?,
    val longitud: Double?
)

data class DetalleNotificacionResponse(
    val id_alerta_superficie: Int,
    val notificacion: String,
    val fecha_alerta: String,
    val hora_alerta: String,
    val piscina: String,
    val imagen_capturada: String,
    val id_usuario: Int,
    val nombre_operario: String,
    val visto: Boolean,
    val latitud: Double?,
    val longitud: Double?
)

data class HistorialGeneralResponse(
    val id_alerta: Int,
    val tipo: String,
    val notificacion: String,
    val fecha_alerta: String,
    val hora_alerta: String,
    val piscina: String,
    val imagen_capturada: String,
    val id_usuario: Int,
    val nombre_operario: String,
    val visto: Boolean,
    val latitud: Double?,
    val longitud: Double?
)

data class DetalleNotificacionAcumulacionResponse(
    val id_alerta_acumulacion: Int,
    val notificacion: String,
    val fecha_alerta: String,
    val hora_alerta: String,
    val piscina: String,
    val imagen_capturada: String,
    val id_usuario: Int,
    val nombre_operario: String,
    val visto: Boolean,
    val latitud: Double?,
    val longitud: Double?
)

data class DetalleNotificacionEspumaResponse(
    val id_alerta_espuma: Int,
    val notificacion: String,
    val fecha_alerta: String,
    val hora_alerta: String,
    val piscina: String,
    val imagen_capturada: String,
    val id_usuario: Int,
    val nombre_operario: String,
    val visto: Boolean,
    val latitud: Double?,
    val longitud: Double?
)
