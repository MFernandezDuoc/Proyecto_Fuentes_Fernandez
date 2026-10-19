package com.panalbit.panalsuite.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Ticket(
    val id: String,
    val type: String,
    val category: String,
    val priority: TicketPriority,
    val status: TicketStatus,
    val createdAt: String,
    val updatedAt: String,
    val branch: String,
    val requesterId: String,
    val resolverTeam: String,
    val assignedTo: String?,
    val comments: List<Comment> = emptyList(),
    val evidences: List<Evidence> = emptyList(),
    val changeLog: List<ChangeEntry> = emptyList()
)
