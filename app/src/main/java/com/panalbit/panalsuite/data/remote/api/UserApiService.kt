package com.panalbit.panalsuite.data.remote.api

import com.panalbit.panalsuite.data.remote.dto.ApiResponse
import com.panalbit.panalsuite.data.remote.dto.UserDto
import retrofit2.http.*

interface UserApiService {
    @GET("users")
    suspend fun getUsers(): ApiResponse<List<UserDto>>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): ApiResponse<UserDto>

    @POST("users")
    suspend fun createUser(@Body user: UserDto): ApiResponse<UserDto>

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: UserDto): ApiResponse<UserDto>

    @DELETE("users/{id}")
    suspend fun deactivateUser(@Path("id") id: String): ApiResponse<Unit>
}
