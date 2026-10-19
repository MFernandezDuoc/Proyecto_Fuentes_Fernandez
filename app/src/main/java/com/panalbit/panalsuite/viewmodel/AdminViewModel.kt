package com.panalbit.panalsuite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panalbit.panalsuite.model.User
import com.panalbit.panalsuite.model.UserRole
import com.panalbit.panalsuite.repository.PanalSuiteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Administracion de los usuarios de prueba: listar, crear y activar o desactivar.
 * Esta seccion es solo para el rol Administrador.
 */
class AdminViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    fun load(forceRefresh: Boolean = false) {
        _uiState.value = _uiState.value.copy(
            isLoading = _uiState.value.users.isEmpty(),
            errorMessage = null
        )

        viewModelScope.launch {
            val result = PanalSuiteRepository.loadUsers(forceRefresh)

            _uiState.value = result.fold(
                onSuccess = { users -> _uiState.value.copy(users = users, isLoading = false) },
                onFailure = { error ->
                    _uiState.value.copy(isLoading = false, errorMessage = error.message ?: "No se pudo cargar los usuarios")
                }
            )
        }
    }

    fun refresh() {
        load(forceRefresh = true)
    }

    // ---------------- Formulario ----------------

    fun showForm() {
        _uiState.value = _uiState.value.copy(
            showForm = true,
            fullName = "",
            username = "",
            email = "",
            role = UserRole.SOLICITANTE,
            showErrors = false
        )
    }

    fun hideForm() {
        _uiState.value = _uiState.value.copy(showForm = false)
    }

    fun onFullNameChange(value: String) {
        _uiState.value = _uiState.value.copy(fullName = value)
    }

    fun onUsernameChange(value: String) {
        // El nombre de acceso no lleva espacios: se limpian al escribir.
        _uiState.value = _uiState.value.copy(username = value.filter { !it.isWhitespace() })
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value.trim())
    }

    fun onRoleChange(value: UserRole) {
        _uiState.value = _uiState.value.copy(role = value)
    }

    fun submit() {
        val state = _uiState.value

        if (!state.isValid) {
            _uiState.value = state.copy(showErrors = true)
            return
        }

        if (state.isSaving) return

        _uiState.value = state.copy(showErrors = true, isSaving = true, errorMessage = null)

        viewModelScope.launch {
            val result = PanalSuiteRepository.createUser(
                fullName = state.fullName,
                username = state.username,
                email = state.email,
                role = state.role
            )

            if (result.isSuccess) {
                val created = result.getOrThrow()
                val users = refreshUsers()
                _uiState.value = _uiState.value.copy(
                    users = users,
                    isSaving = false,
                    showForm = false,
                    feedbackMessage = "Usuario ${created.username} creado"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "No se pudo crear el usuario"
                )
            }
        }
    }

    fun toggleUserActive(user: User) {
        _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)

        viewModelScope.launch {
            val result = PanalSuiteRepository.setUserActive(user.id, !user.isActive)

            _uiState.value = result.fold(
                onSuccess = { updated ->
                    _uiState.value.copy(
                        users = _uiState.value.users.map { if (it.id == updated.id) updated else it },
                        isSaving = false,
                        feedbackMessage = if (updated.isActive) {
                            "${updated.fullName} activado"
                        } else {
                            "${updated.fullName} desactivado"
                        }
                    )
                },
                onFailure = { error ->
                    _uiState.value.copy(isSaving = false, errorMessage = error.message ?: "No se pudo actualizar el usuario")
                }
            )
        }
    }

    fun onFeedbackShown() {
        _uiState.value = _uiState.value.copy(feedbackMessage = null)
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private suspend fun refreshUsers(): List<User> =
        PanalSuiteRepository.loadUsers(forceRefresh = true)
            .getOrElse { _uiState.value.users }
}
