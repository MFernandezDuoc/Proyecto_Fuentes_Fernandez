package com.panalbit.panalsuite.model

/**
 * Sesion activa. Se guarda en almacenamiento local para no tener que volver a entrar.
 */
data class AuthSession(
    val userId: String,
    val username: String,
    val fullName: String,
    val role: UserRole,
    val token: String
)
