package com.panalbit.panalsuite.model

/**
 * Los estados posibles de un ticket.
 *
 * Los nombres van en inglés y el label es lo que ve el usuario.
 */

enum class TicketStatus(val label: String) {
    OPEN("Abierto"),
    IN_PROGRESS("En progreso"),
    PENDING("Pendiente"),
    RESOLVED("Resuelto"),
    CLOSED("Cerrado"),
    CANCELLED("Cancelado");

    companion object {
        fun fromName(value: String?): TicketStatus =
            entries.firstOrNull { it.name == value } ?: OPEN
    }
}
