package com.panalbit.panalsuite.model

/**
 * Evidencia adjunta a un ticket. En este MVP solo se describe: no se sube nada real.
 */
data class Evidence(
    val id: String,
    val description: String,
    val location: String?,
    val createdAt: Long
)
