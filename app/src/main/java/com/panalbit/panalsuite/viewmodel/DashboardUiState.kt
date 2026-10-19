package com.panalbit.panalsuite.viewmodel

import com.panalbit.panalsuite.model.DashboardStats

/**
 * Estado del dashboard de indicadores.
 */
data class DashboardUiState(
    val stats: DashboardStats = DashboardStats(),
    val currentUserName: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {

    val hasData: Boolean
        get() = stats.totalTickets > 0

    val averageResolutionText: String
        get() = if (stats.averageResolutionHours <= 0) "-" else "%.1f h".format(stats.averageResolutionHours)

    /** Los equipos con mas carga primero, para dibujar las barras. */
    val workloadSorted: List<Pair<String, Int>>
        get() = stats.workloadByTeam.entries
            .sortedByDescending { it.value }
            .map { it.key to it.value }

    val maxWorkload: Int
        get() = workloadSorted.maxOfOrNull { it.second } ?: 0
}
