package com.panalbit.panalsuite.viewmodel

import com.panalbit.panalsuite.model.AuthSession

/**
 * Foto completa del formulario de acceso en un momento dado.
 *
 * Los errores no se guardan: se calculan con get() a partir de los valores
 * actuales. Asi el mensaje siempre corresponde a lo que el usuario escribio y
 * desaparece solo cuando lo corrige.
 */
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val showErrors: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val session: AuthSession? = null
) {

    val usernameError: String?
        get() = if (username.trim().length < 3) {
            "Ingresa tu nombre de acceso (mínimo 3 caracteres)"
        } else {
            null
        }

    val passwordError: String?
        get() = if (password.length < 4) {
            "La contraseña debe tener al menos 4 caracteres"
        } else {
            null
        }

    val isValid: Boolean
        get() = usernameError == null && passwordError == null

    /** El boton queda deshabilitado mientras se esta validando contra la API. */
    val canSubmit: Boolean
        get() = isValid && !isLoading
}
