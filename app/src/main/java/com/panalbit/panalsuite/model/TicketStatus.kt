package com.panalbit.panalsuite.model

/**
 * Estados posibles de un ticket. Los nombres van en ingles y el label es lo que ve el usuario.
 */
enum class TicketStatus(val label: String) {
    OPEN("Abierto"),
    IN_PROGRESS("En progreso"),
    PENDING("Pendiente"),
    RESOLVED("Resuelto"),
    CLOSED("Cerrado"),
    CANCELLED("Cancelado");

    companion object {
        /** Convierte el texto guardado en almacenamiento local de vuelta a enum, sin romper si no existe. */
        fun fromName(value: String?): TicketStatus =
            entries.firstOrNull { it.name == value } ?: OPEN
    }
}
