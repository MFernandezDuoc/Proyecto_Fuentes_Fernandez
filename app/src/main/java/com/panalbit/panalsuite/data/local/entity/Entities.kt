package com.panalbit.panalsuite.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey val id: String,
    val type: String,
    val category: String,
    val priority: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
    val branch: String,
    val requesterId: String,
    val resolverTeam: String,
    val assignedTo: String?
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val ticketId: String,
    val authorId: String,
    val authorName: String,
    val content: String,
    val createdAt: String
)

@Entity(tableName = "evidences")
data class EvidenceEntity(
    @PrimaryKey val id: String,
    val ticketId: String,
    val uri: String,
    val type: String,
    val description: String,
    val uploadedAt: String,
    val isLocal: Boolean,
    val lat: Double?,
    val lng: Double?
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val fullName: String,
    val email: String,
    val role: String,
    val isActive: Boolean
)

@Entity(tableName = "dashboard_stats")
data class DashboardStatsEntity(
    @PrimaryKey val id: Int = 1,
    val totalOpen: Int,
    val totalInProgress: Int,
    val totalResolved: Int,
    val avgResolutionTimeHours: Double,
    val teamWorkloadJson: String
)

@Entity(tableName = "pending_operations")
data class PendingOperationEntity(
    @PrimaryKey val id: String,
    val type: String,
    val ticketId: String,
    val payloadJson: String,
    val createdAt: Long,
    val retryCount: Int = 0,
    val status: String = "PENDING"
)
