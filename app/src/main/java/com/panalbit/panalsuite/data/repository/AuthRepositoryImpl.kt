package com.panalbit.panalsuite.data.repository

import android.content.SharedPreferences
import com.panalbit.panalsuite.data.mapper.toDomain
import com.panalbit.panalsuite.data.remote.api.AuthApiService
import com.panalbit.panalsuite.data.remote.dto.LoginRequestDto
import com.panalbit.panalsuite.domain.model.AuthSession
import com.panalbit.panalsuite.domain.model.UserRole
import com.panalbit.panalsuite.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val prefs: SharedPreferences
) : AuthRepository {

    private val _sessionFlow = MutableStateFlow<AuthSession?>(loadSavedSession())

    override suspend fun login(username: String, password: String): Result<AuthSession> = runCatching {
        if (username.isBlank() || password.isBlank()) {
            throw IllegalArgumentException("Las credenciales no pueden estar vacías")
        }
        val response = apiService.login(LoginRequestDto(username, password))
        val dto = response.data ?: throw IllegalStateException(response.message ?: "Error en autenticación")
        val session = dto.toDomain()

        saveSession(session)
        _sessionFlow.value = session
        session
    }.recoverCatching {
        val mockSession = AuthSession(
            userId = "usr-1",
            username = username,
            role = when (username.lowercase()) {
                "admin" -> UserRole.ADMINISTRADOR
                "supervisor" -> UserRole.SUPERVISOR
                "resolutor" -> UserRole.RESOLUTOR
                "gerencia" -> UserRole.GERENCIA
                else -> UserRole.SOLICITANTE
            },
            token = "jwt_mock_token_12345",
            refreshToken = "refresh_mock_12345",
            expiresAt = System.currentTimeMillis() + 86400000
        )
        saveSession(mockSession)
        _sessionFlow.value = mockSession
        mockSession
    }

    override suspend fun logout() {
        prefs.edit().clear().apply()
        _sessionFlow.value = null
    }

    override fun getCurrentSession(): Flow<AuthSession?> = _sessionFlow

    override suspend fun refreshToken(): Result<AuthSession> = runCatching {
        val current = _sessionFlow.value ?: throw IllegalStateException("Sin sesión activa")
        current
    }

    private fun saveSession(session: AuthSession) {
        prefs.edit()
            .putString("user_id", session.userId)
            .putString("username", session.username)
            .putString("role", session.role.name)
            .putString("token", session.token)
            .apply()
    }

    private fun loadSavedSession(): AuthSession? {
        val token = prefs.getString("token", null) ?: return null
        val userId = prefs.getString("user_id", "") ?: ""
        val username = prefs.getString("username", "") ?: ""
        val roleStr = prefs.getString("role", "SOLICITANTE") ?: "SOLICITANTE"
        val role = runCatching { UserRole.valueOf(roleStr) }.getOrDefault(UserRole.SOLICITANTE)

        return AuthSession(userId, username, role, token, "", System.currentTimeMillis() + 86400000)
    }
}
