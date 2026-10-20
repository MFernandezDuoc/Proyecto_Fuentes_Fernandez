package com.panalbit.panalsuite.viewmodel

import com.panalbit.panalsuite.model.AuthSession

/**
 * Estado de la pantalla de perfil: quien esta conectado y cuanto hay guardado
 * en el almacenamiento local del telefono.
 */
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

    /** Resumen de lo que hay persistido, para mostrar que el guardado funciona. */
    val storageSummary: String
        get() = "$savedTickets ticket(s), $savedComments comentario(s) y $savedUsers usuario(s)"
}
