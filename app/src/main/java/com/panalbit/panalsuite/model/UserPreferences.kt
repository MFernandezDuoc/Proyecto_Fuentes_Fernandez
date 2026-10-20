package com.panalbit.panalsuite.model

/**
 * Lo que la aplicacion recuerda del usuario entre una ejecucion y otra.
 *
 * Son los valores por defecto de una app recien instalada: sin datos de
 * contacto, con la prioridad media y siguiendo el modo del telefono.
 */
data class UserPreferences(
    val contactName: String = "",
    val contactEmail: String = "",
    val lastUsername: String = "",
    val defaultPriority: TicketPriority = TicketPriority.MEDIUM,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
) {
    /** Hay datos de contacto guardados o todavia no. */
    val hasContactData: Boolean
        get() = contactName.isNotBlank() || contactEmail.isNotBlank()
}
