package com.panalbit.panalsuite.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TicketDto(
    @SerialName("id") val id: String,
    @SerialName("type") val type: String,
    @SerialName("category") val category: String,
    @SerialName("priority") val priority: String,
    @SerialName("status") val status: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("branch") val branch: String,
    @SerialName("requester_id") val requesterId: String,
    @SerialName("resolver_team") val resolverTeam: String,
    @SerialName("assigned_to") val assignedTo: String? = null,
    @SerialName("comments") val comments: List<CommentDto> = emptyList(),
    @SerialName("evidences") val evidences: List<EvidenceDto> = emptyList(),
    @SerialName("change_log") val changeLog: List<ChangeEntryDto> = emptyList()
)

@Serializable
data class CommentDto(
    @SerialName("id") val id: String,
    @SerialName("ticket_id") val ticketId: String,
    @SerialName("author_id") val authorId: String,
    @SerialName("author_name") val authorName: String,
    @SerialName("content") val content: String,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class EvidenceDto(
    @SerialName("id") val id: String,
    @SerialName("ticket_id") val ticketId: String,
    @SerialName("uri") val uri: String,
    @SerialName("type") val type: String = "IMAGE",
    @SerialName("description") val description: String,
    @SerialName("uploaded_at") val uploadedAt: String,
    @SerialName("is_local") val isLocal: Boolean = false,
    @SerialName("lat") val lat: Double? = null,
    @SerialName("lng") val lng: Double? = null
)

@Serializable
data class ChangeEntryDto(
    @SerialName("id") val id: String,
    @SerialName("ticket_id") val ticketId: String,
    @SerialName("field") val field: String,
    @SerialName("old_value") val oldValue: String,
    @SerialName("new_value") val newValue: String,
    @SerialName("changed_by") val changedBy: String,
    @SerialName("changed_at") val changedAt: String
)

@Serializable
data class UserDto(
    @SerialName("id") val id: String,
    @SerialName("username") val username: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("email") val email: String,
    @SerialName("role") val role: String,
    @SerialName("is_active") val isActive: Boolean = true
)

@Serializable
data class DashboardStatsDto(
    @SerialName("total_open") val totalOpen: Int,
    @SerialName("total_in_progress") val totalInProgress: Int,
    @SerialName("total_resolved") val totalResolved: Int,
    @SerialName("avg_resolution_time_hours") val avgResolutionTimeHours: Double,
    @SerialName("team_workload") val teamWorkload: Map<String, Int> = emptyMap()
)
