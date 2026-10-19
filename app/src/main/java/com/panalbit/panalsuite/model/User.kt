package com.panalbit.panalsuite.model

/**
 * Usuario de prueba del sistema.
 */
data class User(
    val id: String,
    val username: String,
    val fullName: String,
    val email: String,
    val role: UserRole,
    val isActive: Boolean = true
)
