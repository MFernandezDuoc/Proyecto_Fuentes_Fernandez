package com.panalbit.panalsuite.model

/**
 * Lo que la aplicación recuerda del usuario entre una ejecución y otra.
 *
 * Son los valores por defecto de una app recién instalada: sin datos de
 * contacto, con la prioridad media y siguiendo el modo del teléfono.
 */

data class UserPreferences(
    val contactName: String = "",
    val contactEmail: String = "",
    val lastUsername: String = "",
    val defaultPriority: TicketPriority = TicketPriority.MEDIUM,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
) {
    val hasContactData: Boolean
        get() = contactName.isNotBlank() || contactEmail.isNotBlank()
}
