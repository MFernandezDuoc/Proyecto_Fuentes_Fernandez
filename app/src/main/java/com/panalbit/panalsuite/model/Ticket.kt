package com.panalbit.panalsuite.model

/**
 * Ticket o solicitud.
 *
 * Es el dato central de la aplicación: además de sus datos, lleva anidados los
 * comentarios, las evidencias y el historial de cambios.
 */

data class Ticket(
    val id: String,
    val title: String,
    val type: String,
    val category: String,
    val priority: TicketPriority,
    val status: TicketStatus,
    val branch: String,
    val requesterName: String,
    val resolverTeam: String,
    val assignedTo: String?,
    val description: String,
    val createdAt: Long,
    val updatedAt: Long,
    val comments: List<Comment> = emptyList(),
    val evidences: List<Evidence> = emptyList(),
    val history: List<ChangeEntry> = emptyList()
) {
    val isClosed: Boolean
        get() = status == TicketStatus.CLOSED || status == TicketStatus.CANCELLED

    val allowedNextStatuses: List<TicketStatus>
        get() = when (status) {
            TicketStatus.OPEN -> listOf(TicketStatus.IN_PROGRESS, TicketStatus.CANCELLED)
            TicketStatus.IN_PROGRESS -> listOf(TicketStatus.PENDING, TicketStatus.RESOLVED)
            TicketStatus.PENDING -> listOf(TicketStatus.IN_PROGRESS, TicketStatus.CANCELLED)
            TicketStatus.RESOLVED -> listOf(TicketStatus.CLOSED, TicketStatus.IN_PROGRESS)
            TicketStatus.CLOSED -> emptyList()
            TicketStatus.CANCELLED -> emptyList()
        }
}
