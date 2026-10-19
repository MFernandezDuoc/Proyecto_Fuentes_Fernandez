package com.panalbit.panalsuite.data.remote.api

import com.panalbit.panalsuite.data.remote.dto.ApiResponse
import com.panalbit.panalsuite.data.remote.dto.DashboardStatsDto
import retrofit2.http.GET

interface DashboardApiService {
    @GET("dashboard/stats")
    suspend fun getStats(): ApiResponse<DashboardStatsDto>
}
