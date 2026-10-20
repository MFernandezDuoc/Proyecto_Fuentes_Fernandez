package com.panalbit.panalsuite.model

/**
 * Como se ve la aplicacion.
 *
 * SYSTEM sigue el modo del telefono, LIGHT y DARK lo fuerzan.
 */
enum class ThemeMode(val label: String) {
    SYSTEM("Sistema"),
    LIGHT("Claro"),
    DARK("Oscuro");

    companion object {
        fun fromName(value: String?): ThemeMode =
            entries.firstOrNull { it.name == value } ?: SYSTEM
    }
}
