package com.panalbit.panalsuite.data.repository

import com.panalbit.panalsuite.domain.model.DashboardStats
import com.panalbit.panalsuite.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepositoryImpl @Inject constructor() : DashboardRepository {

    private val mockStats = MutableStateFlow(
        DashboardStats(
            totalOpen = 12,
            totalInProgress = 8,
            totalResolved = 25,
            avgResolutionTimeHours = 4.5,
            teamWorkload = mapOf(
                "Soporte TI" to 5,
                "Mantenimiento" to 3,
                "Operaciones" to 2
            )
        )
    )

    override fun getDashboardStats(): Flow<DashboardStats> = mockStats

    override suspend fun refreshStats(): Result<Unit> = runCatching {
    }
}
