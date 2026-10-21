package com.panalbit.panalsuite.model

/**
 * Indicadores del dashboard.
 *
 * Se calculan a partir de la lista de tickets, nunca se guardan a mano.
 */

data class DashboardStats(
    val totalOpen: Int = 0,
    val totalInProgress: Int = 0,
    val totalResolved: Int = 0,
    val totalCritical: Int = 0,
    val averageResolutionHours: Double = 0.0,
    val workloadByTeam: Map<String, Int> = emptyMap()
) {
    val totalTickets: Int
        get() = totalOpen + totalInProgress + totalResolved

    val resolutionRate: Int
        get() = if (totalTickets == 0) 0 else (totalResolved * 100) / totalTickets
}
