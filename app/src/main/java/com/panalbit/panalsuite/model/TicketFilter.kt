package com.panalbit.panalsuite.model

/**
 * Filtro de la bandeja. Todos los campos son opcionales: si son null o estan vacios, no filtran.
 */
data class TicketFilter(
    val status: TicketStatus? = null,
    val priority: TicketPriority? = null,
    val searchQuery: String = ""
) {
    /** Texto que se muestra en el chip de estado de la bandeja. */
    val statusLabel: String
        get() = status?.label ?: "Todos"

    /** Cuantos filtros estan activos, para mostrarlo en pantalla. */
    val activeCount: Int
        get() = (if (status != null) 1 else 0) +
                (if (priority != null) 1 else 0) +
                (if (searchQuery.isNotBlank()) 1 else 0)
}
