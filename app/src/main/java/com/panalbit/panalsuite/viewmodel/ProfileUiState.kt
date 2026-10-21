package com.panalbit.panalsuite.viewmodel

/**
 * Estado de la pantalla de perfil: quién está conectado y cuánto hay guardado
 * en el almacenamiento local del teléfono.
 */

import com.panalbit.panalsuite.model.AuthSession

data class ProfileUiState(
    val session: AuthSession? = null,
    val savedTickets: Int = 0,
    val savedUsers: Int = 0,
    val savedComments: Int = 0
) {

    val fullName: String
        get() = session?.fullName ?: "Sin sesión iniciada"

    val roleLabel: String
        get() = session?.role?.label ?: "-"

    val username: String
        get() = session?.username ?: "-"

    val hasSession: Boolean
        get() = session != null

    val storageSummary: String
        get() = "$savedTickets ticket(s), $savedComments comentario(s) y $savedUsers usuario(s)"
}
