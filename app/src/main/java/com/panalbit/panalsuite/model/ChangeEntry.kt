package com.panalbit.panalsuite.model

/**
 * Una linea de la trazabilidad: que cambio, de que valor a que valor y quien lo hizo.
 */
data class ChangeEntry(
    val id: String,
    val field: String,
    val oldValue: String,
    val newValue: String,
    val changedBy: String,
    val changedAt: Long
)
