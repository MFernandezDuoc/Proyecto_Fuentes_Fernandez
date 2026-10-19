package com.panalbit.panalsuite.data.repository

import com.panalbit.panalsuite.data.local.dao.TicketDao
import com.panalbit.panalsuite.data.mapper.toDomain
import com.panalbit.panalsuite.data.mapper.toEntity
import com.panalbit.panalsuite.data.remote.api.TicketApiService
import com.panalbit.panalsuite.domain.model.*
import com.panalbit.panalsuite.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TicketRepositoryImpl @Inject constructor(
    private val ticketDao: TicketDao,
    private val apiService: TicketApiService
) : TicketRepository {

    private val mockTickets = mutableListOf(
        Ticket(
            id = "TICK-101",
            type = "Incidencia",
            category = "Sistemas",
            priority = TicketPriority.HIGH,
            status = TicketStatus.OPEN,
            createdAt = "2026-10-18 09:00",
            updatedAt = "2026-10-18 09:00",
            branch = "Sucursal Central",
            requesterId = "usr-1",
            resolverTeam = "TI Soporte",
            assignedTo = "resolutor",
            comments = listOf(
                Comment("c1", "TICK-101", "usr-1", "Juan Pérez", "Falla en el punto de venta.", "2026-10-18 09:05")
            )
        ),
        Ticket(
            id = "TICK-102",
            type = "Solicitud",
            category = "Mantenimiento",
            priority = TicketPriority.MEDIUM,
            status = TicketStatus.IN_PROGRESS,
            createdAt = "2026-10-17 14:30",
            updatedAt = "2026-10-18 10:15",
            branch = "Sucursal Norte",
            requesterId = "usr-2",
            resolverTeam = "Infraestructura",
            assignedTo = "resolutor"
        )
    )

    override fun getTickets(filter: TicketFilter): Flow<List<Ticket>> {
        return ticketDao.getAll().map { list ->
            val domainList = if (list.isEmpty()) mockTickets else list.map { it.toDomain() }
            domainList.filter { ticket ->
                (filter.status == null || ticket.status == filter.status) &&
                (filter.priority == null || ticket.priority == filter.priority) &&
                (filter.assignedTo == null || ticket.assignedTo == filter.assignedTo) &&
                (filter.searchQuery.isNull_or_blank() || 
                    ticket.id.contains(filter.searchQuery, ignoreCase = true) ||
                    ticket.category.contains(filter.searchQuery, ignoreCase = true))
            }
        }
    }

    override suspend fun getTicketById(id: String): Result<Ticket> = runCatching {
        val local = ticketDao.getById(id)?.toDomain()
        local ?: mockTickets.find { it.id == id } ?: throw NoSuchElementException("Ticket $id no encontrado")
    }

    override suspend fun updateTicketStatus(id: String, newStatus: TicketStatus, comment: String?): Result<Unit> = runCatching {
        val index = mockTickets.indexOfFirst { it.id == id }
        if (index != -1) {
            val updated = mockTickets[index].copy(status = newStatus, updatedAt = "2026-10-19 12:00")
            mockTickets[index] = updated
            ticketDao.upsert(updated.toEntity())
        }
    }

    override suspend fun addComment(ticketId: String, comment: String): Result<Comment> = runCatching {
        val newComment = Comment(
            id = "c-${System.currentTimeMillis()}",
            ticketId = ticketId,
            authorId = "usr-current",
            authorName = "Usuario Actual",
            content = comment,
            createdAt = "2026-10-19 12:00"
        )
        val index = mockTickets.indexOfFirst { it.id == ticketId }
        if (index != -1) {
            val t = mockTickets[index]
            mockTickets[index] = t.copy(comments = t.comments + newComment)
        }
        newComment
    }

    override suspend fun addEvidence(ticketId: String, evidence: Evidence): Result<Unit> = runCatching {
        val index = mockTickets.indexOfFirst { it.id == ticketId }
        if (index != -1) {
            val t = mockTickets[index]
            mockTickets[index] = t.copy(evidences = t.evidences + evidence)
        }
    }

    override suspend fun reassignTicket(ticketId: String, newAssigneeId: String): Result<Unit> = runCatching {
        val index = mockTickets.indexOfFirst { it.id == ticketId }
        if (index != -1) {
            val t = mockTickets[index]
            mockTickets[index] = t.copy(assignedTo = newAssigneeId)
        }
    }

    private fun String?.isNull_or_blank(): Boolean = this == null || this.trim().isEmpty()
}
