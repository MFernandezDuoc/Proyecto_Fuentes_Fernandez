package com.panalbit.panalsuite.domain.repository

import com.panalbit.panalsuite.domain.model.DashboardStats
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun getDashboardStats(): Flow<DashboardStats>
    suspend fun refreshStats(): Result<Unit>
}
