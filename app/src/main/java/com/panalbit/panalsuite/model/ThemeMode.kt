package com.panalbit.panalsuite.model

/**
 * Las tres maneras de ver la aplicación.
 *
 * SYSTEM sigue el modo del teléfono, LIGHT y DARK lo fuerzan.
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
