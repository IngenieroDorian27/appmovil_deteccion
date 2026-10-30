package com.example.visionia.network

data class LoginRequest(
    val usuario: String,
    val contrasena: String
)

data class LoginResponse(
    val mensaje: String,
    val id_usuario: Int,
    val usuario: String,
    val nombre_operario: String,
    val rol_usuario: String
)

