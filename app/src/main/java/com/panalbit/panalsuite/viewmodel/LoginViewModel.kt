package com.panalbit.panalsuite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panalbit.panalsuite.model.User
import com.panalbit.panalsuite.repository.FakePanalApi
import com.panalbit.panalsuite.repository.PanalSuiteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado del formulario de acceso.
 *
 * Es la unica clase que sabe si el usuario puede entrar: la pantalla solo dibuja
 * lo que este estado dice y avisa cuando el usuario escribe o toca el boton.
 */
class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) {
        _uiState.value = _uiState.value.copy(username = value, errorMessage = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = null)
    }

    /**
     * Intenta iniciar sesion.
     * Si el formulario tiene errores, activa su visualizacion y no llama a la API.
     * Si esta correcto, deja el resultado en el estado: la pantalla mira `session`
     * y navega cuando aparece.
     */
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

            _uiState.value = result.fold(
                onSuccess = { session ->
                    _uiState.value.copy(isLoading = false, session = session)
                },
                onFailure = { error ->
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "No se pudo iniciar sesion"
                    )
                }
            )
        }
    }

    /**
     * La pantalla avisa que ya navego con la sesion.
     * Sin esto, al volver a la pantalla de acceso la sesion vieja la sacaria de nuevo.
     */
    fun onSessionHandled() {
        _uiState.value = _uiState.value.copy(session = null)
    }

    /** Deja el formulario limpio, por ejemplo despues de cerrar sesion. */
    fun reset() {
        _uiState.value = LoginUiState()
    }

    /**
     * Cuentas de prueba que se muestran en la pantalla para poder entrar rapido.
     * Salen del mismo lugar que usa la API simulada, asi nunca se desincronizan.
     */
    fun demoAccounts(): List<User> = FakePanalApi.demoUsers

    fun demoPassword(): String = FakePanalApi.DEMO_PASSWORD

    /** Completa el formulario con una cuenta de prueba. */
    fun fillDemoAccount(user: User) {
        _uiState.value = _uiState.value.copy(
            username = user.username,
            password = FakePanalApi.DEMO_PASSWORD,
            showErrors = false,
            errorMessage = null
        )
    }
}
