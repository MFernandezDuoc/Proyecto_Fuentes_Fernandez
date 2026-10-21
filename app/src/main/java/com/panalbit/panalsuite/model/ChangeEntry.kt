package com.panalbit.panalsuite.model

/**
 * Una línea de la trazabilidad: qué cambió, de qué valor a qué valor
 * y quién lo hizo.
 */

data class ChangeEntry(
    val id: String,
    val field: String,
    val oldValue: String,
    val newValue: String,
    val changedBy: String,
    val changedAt: Long
)
