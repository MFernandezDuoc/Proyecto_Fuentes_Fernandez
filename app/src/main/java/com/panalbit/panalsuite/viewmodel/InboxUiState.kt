package com.panalbit.panalsuite.viewmodel

/**
 * Estado de la bandeja de trabajo.
 *
 * visibleTickets es una propiedad calculada: aplica el filtro de la interfaz y el
 * filtro por rol. Como se recalcula sola, no hay forma de que la pantalla muestre
 * una lista que ya no corresponde al filtro elegido.
 */

import com.panalbit.panalsuite.model.Ticket
import com.panalbit.panalsuite.model.TicketFilter
import com.panalbit.panalsuite.model.TicketPriority
import com.panalbit.panalsuite.model.TicketStatus
import com.panalbit.panalsuite.model.UserRole

data class InboxUiState(
    val tickets: List<Ticket> = emptyList(),
    val filter: TicketFilter = TicketFilter(),
    val currentUserName: String = "",
    val currentUserRole: UserRole = UserRole.SOLICITANTE,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {

    private val ticketsForRole: List<Ticket>
        get() = when (currentUserRole) {
            UserRole.SOLICITANTE -> tickets.filter { it.requesterName == currentUserName }
            UserRole.RESOLUTOR -> tickets.filter { it.assignedTo == currentUserName || it.resolverTeam == "Mesa de Ayuda" }
            UserRole.SUPERVISOR, UserRole.GERENCIA, UserRole.ADMINISTRADOR -> tickets
        }

    val visibleTickets: List<Ticket>
        get() {
            val query = filter.searchQuery.trim().lowercase()
            return ticketsForRole
                .filter { ticket ->
                    (filter.status == null || ticket.status == filter.status) &&
                            (filter.priority == null || ticket.priority == filter.priority)
                }
                .filter { ticket ->
                    query.isEmpty() ||
                            ticket.title.lowercase().contains(query) ||
                            ticket.id.lowercase().contains(query) ||
                            ticket.branch.lowercase().contains(query) ||
                            ticket.requesterName.lowercase().contains(query)
                }
                .sortedByDescending { it.updatedAt }
        }

    val isListEmpty: Boolean
        get() = visibleTickets.isEmpty()

    val totalForRole: Int
        get() = ticketsForRole.size

    val openCount: Int
        get() = ticketsForRole.count { it.status == TicketStatus.OPEN }

    val inProgressCount: Int
        get() = ticketsForRole.count {
            it.status == TicketStatus.IN_PROGRESS || it.status == TicketStatus.PENDING
        }

    val emptyMessage: String
        get() = if (filter.activeCount > 0) {
            "Ningún ticket coincide con el filtro aplicado"
        } else {
            "No tienes tickets asignados por ahora"
        }

    companion object {
        val SEARCH_HINT = "Buscar por titulo, ID, sucursal o solicitante"
    }
}
