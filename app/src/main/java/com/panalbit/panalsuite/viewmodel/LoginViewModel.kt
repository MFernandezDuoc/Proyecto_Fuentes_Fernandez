package com.panalbit.panalsuite.viewmodel

/**
 * Estado del formulario de acceso.
 *
 * Es la única clase que sabe si el usuario puede entrar: la pantalla solo dibuja
 * lo que este estado dice y avisa cuando el usuario escribe o toca el botón.
 */

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.panalbit.panalsuite.model.User
import com.panalbit.panalsuite.repository.PanalApiService
import com.panalbit.panalsuite.repository.PanalSuiteRepository
import com.panalbit.panalsuite.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepository = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.userPreferences
                .map { it.lastUsername }
                .distinctUntilChanged()
                .collect { saved ->
                    if (saved.isNotBlank() && _uiState.value.username.isBlank()) {
                        _uiState.value = _uiState.value.copy(username = saved)
                    }
                }
        }
    }

    fun onUsernameChange(value: String) {
        _uiState.value = _uiState.value.copy(username = value, errorMessage = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = null)
    }

    fun submit() {
        val state = _uiState.value

        if (!state.isValid) {
            _uiState.value = state.copy(showErrors = true)
            return
        }

        if (state.isLoading) return

        _uiState.value = state.copy(showErrors = true, isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = PanalSuiteRepository.login(state.username, state.password)

            result.fold(
                onSuccess = { session ->
                    preferencesRepository.saveLastUsername(state.username)
                    _uiState.value = _uiState.value.copy(isLoading = false, session = session)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "No se pudo iniciar sesión"
                    )
                }
            )
        }
    }

    fun onSessionHandled() {
        _uiState.value = _uiState.value.copy(session = null)
    }

    fun reset() {
        _uiState.value = LoginUiState(username = _uiState.value.username)
    }

    fun demoAccounts(): List<User> = PanalApiService.demoUsers

    fun demoPassword(): String = PanalApiService.DEMO_PASSWORD

    fun fillDemoAccount(user: User) {
        _uiState.value = _uiState.value.copy(
            username = user.username,
            password = PanalApiService.DEMO_PASSWORD,
            showErrors = false,
            errorMessage = null
        )
    }
}
