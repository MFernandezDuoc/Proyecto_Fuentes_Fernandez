package com.panalbit.panalsuite.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthSession(
    val userId: String,
    val username: String,
    val role: UserRole,
    val token: String,
    val refreshToken: String,
    val expiresAt: Long
)
