package com.panalbit.panalsuite.model

/**
 * Sesión activa.
 *
 * Se guarda en el almacenamiento local para no tener que volver a entrar cada vez.
 */

data class AuthSession(
    val userId: String,
    val username: String,
    val fullName: String,
    val role: UserRole,
    val token: String
)
