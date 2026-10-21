package com.panalbit.panalsuite.viewmodel

/**
 * Estado del dashboard de indicadores.
 */

import com.panalbit.panalsuite.model.DashboardStats

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

    val workloadSorted: List<Pair<String, Int>>
        get() = stats.workloadByTeam.entries
            .sortedByDescending { it.value }
            .map { it.key to it.value }

    val maxWorkload: Int
        get() = workloadSorted.maxOfOrNull { it.second } ?: 0
}
