package com.panalbit.panalsuite.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Evidence(
    val id: String,
    val ticketId: String,
    val uri: String,
    val type: String = "IMAGE",
    val description: String,
    val uploadedAt: String,
    val isLocal: Boolean = false,
    val lat: Double? = null,
    val lng: Double? = null
)
