package com.panalbit.panalsuite.domain.usecase

import com.panalbit.panalsuite.domain.model.*
import com.panalbit.panalsuite.domain.repository.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(u: String, p: String): Result<AuthSession> {
        if (u.isBlank() || p.isBlank()) return Result.failure(IllegalArgumentException("Campos obligatorios"))
        return authRepository.login(u, p)
    }
}

class GetCurrentSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<AuthSession?> = authRepository.getCurrentSession()
}

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() = authRepository.logout()
}

class GetTicketsUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    operator fun invoke(filter: TicketFilter): Flow<List<Ticket>> = ticketRepository.getTickets(filter)
}

class GetTicketDetailUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    suspend operator fun invoke(id: String): Result<Ticket> = ticketRepository.getTicketById(id)
}

class UpdateTicketStatusUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    suspend operator fun invoke(id: String, status: TicketStatus, comment: String?): Result<Unit> =
        ticketRepository.updateTicketStatus(id, status, comment)
}

class AddCommentUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    suspend operator fun invoke(ticketId: String, comment: String): Result<Comment> {
        if (comment.isBlank()) return Result.failure(IllegalArgumentException("El comentario no puede estar vacío"))
        return ticketRepository.addComment(ticketId, comment)
    }
}

class AddEvidenceUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    suspend operator fun invoke(ticketId: String, evidence: Evidence): Result<Unit> =
        ticketRepository.addEvidence(ticketId, evidence)
}

class GetDashboardStatsUseCase @Inject constructor(
    private val dashboardRepository: DashboardRepository
) {
    operator fun invoke(): Flow<DashboardStats> = dashboardRepository.getDashboardStats()
}

class GetUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<List<User>> = userRepository.getUsers()
}
