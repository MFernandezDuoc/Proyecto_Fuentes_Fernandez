package com.panalbit.panalsuite.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    @SerialName("user_id") val userId: String,
    @SerialName("username") val username: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("role") val role: String,
    @SerialName("token") val token: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_at") val expiresAt: Long
)
