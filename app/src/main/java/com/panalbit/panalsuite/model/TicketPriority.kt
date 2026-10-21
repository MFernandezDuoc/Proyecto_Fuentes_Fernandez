package com.panalbit.panalsuite.model

/**
 * Las cuatro prioridades de un ticket.
 */

enum class TicketPriority(val label: String) {
    LOW("Baja"),
    MEDIUM("Media"),
    HIGH("Alta"),
    CRITICAL("Critica");

    companion object {
        fun fromName(value: String?): TicketPriority =
            entries.firstOrNull { it.name == value } ?: MEDIUM
    }
}
