package com.panalbit.panalsuite.data.remote.api

import com.panalbit.panalsuite.data.remote.dto.*
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): ApiResponse<LoginResponseDto>

    @POST("auth/logout")
    suspend fun logout(): ApiResponse<Unit>

    @POST("auth/refresh")
    suspend fun refreshToken(): ApiResponse<LoginResponseDto>
}
