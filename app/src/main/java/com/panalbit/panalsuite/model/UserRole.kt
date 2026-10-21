package com.panalbit.panalsuite.model

/**
 * Los roles del sistema y los permisos de cada uno.
 *
 * Definen qué pantallas y qué acciones ve cada usuario.
 */

enum class UserRole(val label: String) {
    SOLICITANTE("Solicitante"),
    RESOLUTOR("Resolutor"),
    SUPERVISOR("Supervisor"),
    GERENCIA("Gerencia"),
    ADMINISTRADOR("Administrador");

    val canManageTickets: Boolean
        get() = this == RESOLUTOR || this == SUPERVISOR || this == ADMINISTRADOR

    val canSeeDashboard: Boolean
        get() = this == SUPERVISOR || this == GERENCIA || this == ADMINISTRADOR

    val canManageUsers: Boolean
        get() = this == ADMINISTRADOR

    val canCreateTicket: Boolean
        get() = this == SOLICITANTE || this == ADMINISTRADOR

    companion object {
        fun fromName(value: String?): UserRole =
            entries.firstOrNull { it.name == value } ?: SOLICITANTE
    }
}
