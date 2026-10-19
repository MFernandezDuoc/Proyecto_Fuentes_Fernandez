package com.panalbit.panalsuite.model

/**
 * Roles del sistema. Definen que pantallas y acciones ve cada usuario.
 */
enum class UserRole(val label: String) {
    SOLICITANTE("Solicitante"),
    RESOLUTOR("Resolutor"),
    SUPERVISOR("Supervisor"),
    GERENCIA("Gerencia"),
    ADMINISTRADOR("Administrador");

    /** Puede cambiar el estado de un ticket que no es suyo. */
    val canManageTickets: Boolean
        get() = this == RESOLUTOR || this == SUPERVISOR || this == ADMINISTRADOR

    /** Puede ver el dashboard de indicadores. */
    val canSeeDashboard: Boolean
        get() = this == SUPERVISOR || this == GERENCIA || this == ADMINISTRADOR

    /** Puede administrar usuarios de prueba. */
    val canManageUsers: Boolean
        get() = this == ADMINISTRADOR

    /** Solo el solicitante crea solicitudes nuevas. */
    val canCreateTicket: Boolean
        get() = this == SOLICITANTE || this == ADMINISTRADOR

    companion object {
        fun fromName(value: String?): UserRole =
            entries.firstOrNull { it.name == value } ?: SOLICITANTE
    }
}
