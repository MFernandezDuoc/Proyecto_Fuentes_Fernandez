package com.panalbit.panalsuite.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val fullName: String,
    val email: String,
    val role: UserRole,
    val isActive: Boolean = true
)
