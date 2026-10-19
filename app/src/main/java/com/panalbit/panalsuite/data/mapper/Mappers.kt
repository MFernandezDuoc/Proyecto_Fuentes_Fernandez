package com.panalbit.panalsuite.data.mapper

import com.panalbit.panalsuite.data.local.entity.*
import com.panalbit.panalsuite.data.remote.dto.*
import com.panalbit.panalsuite.domain.model.*

fun String.toTicketStatus(): TicketStatus =
    runCatching { TicketStatus.valueOf(this) }.getOrDefault(TicketStatus.OPEN)

fun String.toTicketPriority(): TicketPriority =
    runCatching { TicketPriority.valueOf(this) }.getOrDefault(TicketPriority.MEDIUM)

fun String.toUserRole(): UserRole =
    runCatching { UserRole.valueOf(this) }.getOrDefault(TicketRole.SOLICITANTE)

private typealias TicketRole = UserRole

fun TicketDto.toDomain(): Ticket = Ticket(
    id = id,
    type = type,
    category = category,
    priority = priority.toTicketPriority(),
    status = status.toTicketStatus(),
    createdAt = createdAt,
    updatedAt = updatedAt,
    branch = branch,
    requesterId = requesterId,
    resolverTeam = resolverTeam,
    assignedTo = assignedTo,
    comments = comments.map { it.toDomain() },
    evidences = evidences.map { it.toDomain() },
    changeLog = changeLog.map { it.toDomain() }
)

fun TicketEntity.toDomain(): Ticket = Ticket(
    id = id,
    type = type,
    category = category,
    priority = priority.toTicketPriority(),
    status = status.toTicketStatus(),
    createdAt = createdAt,
    updatedAt = updatedAt,
    branch = branch,
    requesterId = requesterId,
    resolverTeam = resolverTeam,
    assignedTo = assignedTo
)

fun Ticket.toEntity(): TicketEntity = TicketEntity(
    id = id,
    type = type,
    category = category,
    priority = priority.name,
    status = status.name,
    createdAt = createdAt,
    updatedAt = updatedAt,
    branch = branch,
    requesterId = requesterId,
    resolverTeam = resolverTeam,
    assignedTo = assignedTo
)

fun CommentDto.toDomain(): Comment = Comment(
    id = id,
    ticketId = ticketId,
    authorId = authorId,
    authorName = authorName,
    content = content,
    createdAt = createdAt
)

fun CommentEntity.toDomain(): Comment = Comment(
    id = id,
    ticketId = ticketId,
    authorId = authorId,
    authorName = authorName,
    content = content,
    createdAt = createdAt
)

fun EvidenceDto.toDomain(): Evidence = Evidence(
    id = id,
    ticketId = ticketId,
    uri = uri,
    type = type,
    description = description,
    uploadedAt = uploadedAt,
    isLocal = isLocal,
    lat = lat,
    lng = lng
)

fun EvidenceEntity.toDomain(): Evidence = Evidence(
    id = id,
    ticketId = ticketId,
    uri = uri,
    type = type,
    description = description,
    uploadedAt = uploadedAt,
    isLocal = isLocal,
    lat = lat,
    lng = lng
)

fun ChangeEntryDto.toDomain(): ChangeEntry = ChangeEntry(
    id = id,
    ticketId = ticketId,
    field = field,
    oldValue = oldValue,
    newValue = newValue,
    changedBy = changedBy,
    changedAt = changedAt
)

fun UserDto.toDomain(): User = User(
    id = id,
    username = username,
    fullName = fullName,
    email = email,
    role = role.toUserRole(),
    isActive = isActive
)

fun UserEntity.toDomain(): User = User(
    id = id,
    username = username,
    fullName = fullName,
    email = email,
    role = role.toUserRole(),
    isActive = isActive
)

fun LoginResponseDto.toDomain(): AuthSession = AuthSession(
    userId = userId,
    username = username,
    role = role.toUserRole(),
    token = token,
    refreshToken = refreshToken,
    expiresAt = expiresAt
)
