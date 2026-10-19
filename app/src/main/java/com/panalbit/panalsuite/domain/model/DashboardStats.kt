package com.panalbit.panalsuite.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DashboardStats(
    val totalOpen: Int,
    val totalInProgress: Int,
    val totalResolved: Int,
    val avgResolutionTimeHours: Double,
    val teamWorkload: Map<String, Int> = emptyMap()
)
