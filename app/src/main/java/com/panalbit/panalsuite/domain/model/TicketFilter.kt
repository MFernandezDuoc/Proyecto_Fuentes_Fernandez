package com.panalbit.panalsuite.domain.model

data class TicketFilter(
    val status: TicketStatus? = null,
    val priority: TicketPriority? = null,
    val assignedTo: String? = null,
    val searchQuery: String? = null
)
