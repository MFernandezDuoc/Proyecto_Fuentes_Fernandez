package com.panalbit.panalsuite.viewmodel

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

/**
 * Estado del formulario de acceso.
 *
 * Es la unica clase que sabe si el usuario puede entrar: la pantalla solo dibuja
 * lo que este estado dice y avisa cuando el usuario escribe o toca el boton.
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepository = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        // El ultimo nombre de acceso queda escrito, para no escribirlo cada vez.
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

            result.fold(
                onSuccess = { session ->
                    // Se recuerda el nombre de acceso para la proxima vez.
                    preferencesRepository.saveLastUsername(state.username)
                    _uiState.value = _uiState.value.copy(isLoading = false, session = session)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
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

    /**
     * Deja el formulario limpio, por ejemplo despues de cerrar sesion.
     * El nombre de acceso se conserva, porque esta guardado en el telefono.
     */
    fun reset() {
        _uiState.value = LoginUiState(username = _uiState.value.username)
    }

    /**
     * Cuentas de prueba que se muestran en la pantalla para poder entrar rapido.
     * Salen del mismo lugar que usa la API, asi nunca se desincronizan.
     */
    fun demoAccounts(): List<User> = PanalApiService.demoUsers

    fun demoPassword(): String = PanalApiService.DEMO_PASSWORD

    /** Completa el formulario con una cuenta de prueba. */
    fun fillDemoAccount(user: User) {
        _uiState.value = _uiState.value.copy(
            username = user.username,
            password = PanalApiService.DEMO_PASSWORD,
            showErrors = false,
            errorMessage = null
        )
    }
}
