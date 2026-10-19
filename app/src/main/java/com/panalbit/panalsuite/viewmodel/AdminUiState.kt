package com.panalbit.panalsuite.viewmodel

import com.panalbit.panalsuite.model.User
import com.panalbit.panalsuite.model.UserRole

/**
 * Estado del modulo de administracion de usuarios de prueba.
 */
data class AdminUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val feedbackMessage: String? = null,

    // Formulario de usuario nuevo
    val showForm: Boolean = false,
    val fullName: String = "",
    val username: String = "",
    val email: String = "",
    val role: UserRole = UserRole.SOLICITANTE,
    val showErrors: Boolean = false
) {

    val fullNameError: String?
        get() = if (fullName.trim().length < 5) "Ingresa el nombre completo" else null

    val usernameError: String?
        get() = when {
            username.isBlank() -> "Ingresa un nombre de acceso"
            username.trim().length < 4 -> "Debe tener al menos 4 caracteres"
            username.trim().contains(" ") -> "No puede tener espacios"
            else -> null
        }

    val emailError: String?
        get() = if (!EMAIL_PATTERN.matches(email.trim())) "Correo no valido. Ejemplo: nombre@panalbit.cl" else null

    val isValid: Boolean
        get() = fullNameError == null && usernameError == null && emailError == null

    val canSubmit: Boolean
        get() = isValid && !isSaving

    val activeCount: Int
        get() = users.count { it.isActive }

    val inactiveCount: Int
        get() = users.count { !it.isActive }

    companion object {
        private val EMAIL_PATTERN = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
