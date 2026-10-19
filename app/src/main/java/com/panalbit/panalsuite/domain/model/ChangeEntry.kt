package com.panalbit.panalsuite.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ChangeEntry(
    val id: String,
    val ticketId: String,
    val field: String,
    val oldValue: String,
    val newValue: String,
    val changedBy: String,
    val changedAt: String
)
