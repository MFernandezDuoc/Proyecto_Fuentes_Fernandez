package com.panalbit.panalsuite.domain.repository

import com.panalbit.panalsuite.domain.model.AuthSession
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<AuthSession>
    suspend fun logout()
    fun getCurrentSession(): Flow<AuthSession?>
    suspend fun refreshToken(): Result<AuthSession>
}
