package com.panalbit.panalsuite.domain.repository

import com.panalbit.panalsuite.domain.model.*
import kotlinx.coroutines.flow.Flow

interface TicketRepository {
    fun getTickets(filter: TicketFilter): Flow<List<Ticket>>
    suspend fun getTicketById(id: String): Result<Ticket>
    suspend fun updateTicketStatus(id: String, newStatus: TicketStatus, comment: String?): Result<Unit>
    suspend fun addComment(ticketId: String, comment: String): Result<Comment>
    suspend fun addEvidence(ticketId: String, evidence: Evidence): Result<Unit>
    suspend fun reassignTicket(ticketId: String, newAssigneeId: String): Result<Unit>
}
