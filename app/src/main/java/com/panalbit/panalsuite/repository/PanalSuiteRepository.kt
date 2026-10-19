package com.panalbit.panalsuite.repository

import com.panalbit.panalsuite.model.AuthSession
import com.panalbit.panalsuite.model.Comment
import com.panalbit.panalsuite.model.DashboardStats
import com.panalbit.panalsuite.model.Evidence
import com.panalbit.panalsuite.model.Ticket
import com.panalbit.panalsuite.model.TicketPriority
import com.panalbit.panalsuite.model.TicketStatus
import com.panalbit.panalsuite.model.User
import com.panalbit.panalsuite.model.UserRole

/**
 * Repositorio unico de PanalSuite.
 *
 * Es el intermediario entre las pantallas y los datos:
 *  - le pide la informacion a FakePanalApi (la API simulada), y
 *  - la guarda en LocalStore (el almacenamiento local del telefono).
 *
 * La estrategia es "primero lo local": si ya hay datos guardados se muestran de
 * inmediato, y solo se vuelve a consultar la API cuando se pide refrescar. Asi la
 * aplicacion funciona igual con o sin conexion.
 *
 * Los ViewModels son los unicos que usan esta clase; las pantallas nunca la ven.
 */
object PanalSuiteRepository {

    // ------------------------------------------------------------------
    // Sesion
    // ------------------------------------------------------------------

    suspend fun login(username: String, password: String): Result<AuthSession> {
        val envelope = FakePanalApi.login(username, password).asApiEnvelope()
        val data = envelope.dataObject

        if (!envelope.success || data == null) {
            return Result.failure(IllegalStateException(envelope.message ?: "No se pudo iniciar sesion"))
        }

        val session = AuthSession(
            userId = data.optString("id"),
            username = data.optString("username"),
            fullName = data.optString("fullName"),
            role = UserRole.fromName(data.optString("role")),
            token = data.optString("token")
        )

        LocalStore.saveSession(session)
        return Result.success(session)
    }

    fun currentSession(): AuthSession? = LocalStore.loadSession()

    /** Busca un ticket en lo que ya esta guardado. No llama a la API. */
    fun findTicket(ticketId: String): Ticket? =
        LocalStore.loadTickets().firstOrNull { it.id == ticketId }

    /** Cuantos tickets hay guardados en el telefono. */
    fun savedTicketCount(): Int = LocalStore.loadTickets().size

    /** Cuantos usuarios hay guardados en el telefono. */
    fun savedUserCount(): Int = LocalStore.loadUsers().size

    /** Cuantos comentarios hay guardados, sumando todos los tickets. */
    fun savedCommentCount(): Int = LocalStore.loadTickets().sumOf { it.comments.size }

    fun logout() {
        LocalStore.saveSession(null)
    }

    // ------------------------------------------------------------------
    // Tickets
    // ------------------------------------------------------------------

    /** Devuelve los tickets guardados. Con forceRefresh vuelve a consultar la API. */
    suspend fun loadTickets(forceRefresh: Boolean = false): Result<List<Ticket>> {
        if (!forceRefresh) {
            val cached = LocalStore.loadTickets()
            if (cached.isNotEmpty()) return Result.success(cached)
        }
        return fetchAndCacheTickets()
    }

    suspend fun createTicket(
        requesterName: String,
        title: String,
        type: String,
        category: String,
        priority: TicketPriority,
        branch: String,
        description: String
    ): Result<Ticket> {
        val envelope = FakePanalApi.createTicket(
            requesterName = requesterName,
            title = title,
            type = type,
            category = category,
            priority = priority,
            branch = branch,
            description = description
        ).asApiEnvelope()

        val data = envelope.dataObject
        if (!envelope.success || data == null) {
            return Result.failure(IllegalStateException(envelope.message ?: "No se pudo crear la solicitud"))
        }

        return refreshLocalTickets().map { tickets ->
            tickets.firstOrNull { it.id == data.optString("id") } ?: ticketFromJson(data)
        }
    }

    suspend fun changeStatus(
        ticketId: String,
        newStatus: TicketStatus,
        changedBy: String
    ): Result<Ticket> {
        val envelope = FakePanalApi.updateTicketStatus(ticketId, newStatus, changedBy).asApiEnvelope()
        if (!envelope.success) {
            return Result.failure(IllegalStateException(envelope.message ?: "No se pudo cambiar el estado"))
        }

        return refreshLocalTickets().map { tickets ->
            tickets.firstOrNull { it.id == ticketId }
                ?: throw IllegalStateException("El ticket ya no existe")
        }
    }

    suspend fun addComment(ticketId: String, authorName: String, content: String): Result<Comment> {
        val envelope = FakePanalApi.addComment(ticketId, authorName, content).asApiEnvelope()
        val data = envelope.dataObject

        if (!envelope.success || data == null) {
            return Result.failure(IllegalStateException(envelope.message ?: "No se pudo guardar el comentario"))
        }

        refreshLocalTickets()
        return Result.success(commentFromJson(data))
    }

    suspend fun addEvidence(
        ticketId: String,
        description: String,
        location: String?
    ): Result<Evidence> {
        val envelope = FakePanalApi.addEvidence(ticketId, description, location).asApiEnvelope()
        val data = envelope.dataObject

        if (!envelope.success || data == null) {
            return Result.failure(IllegalStateException(envelope.message ?: "No se pudo guardar la evidencia"))
        }

        refreshLocalTickets()
        return Result.success(evidenceFromJson(data))
    }

    // ------------------------------------------------------------------
    // Usuarios
    // ------------------------------------------------------------------

    suspend fun loadUsers(forceRefresh: Boolean = false): Result<List<User>> {
        if (!forceRefresh) {
            val cached = LocalStore.loadUsers()
            if (cached.isNotEmpty()) return Result.success(cached)
        }

        val envelope = FakePanalApi.fetchUsers().asApiEnvelope()
        val array = envelope.dataArray
        if (!envelope.success || array == null) {
            return Result.failure(IllegalStateException(envelope.message ?: "No se pudo cargar la lista de usuarios"))
        }

        val users = (0 until array.length()).mapNotNull { index ->
            array.optJSONObject(index)?.let { userFromJson(it) }
        }
        LocalStore.saveUsers(users)
        return Result.success(users)
    }

    suspend fun createUser(
        fullName: String,
        username: String,
        email: String,
        role: UserRole
    ): Result<User> {
        val envelope = FakePanalApi.createUser(fullName, username, email, role).asApiEnvelope()
        val data = envelope.dataObject

        if (!envelope.success || data == null) {
            return Result.failure(IllegalStateException(envelope.message ?: "No se pudo crear el usuario"))
        }

        refreshLocalUsers()
        return Result.success(userFromJson(data))
    }

    suspend fun setUserActive(userId: String, isActive: Boolean): Result<User> {
        val envelope = FakePanalApi.setUserActive(userId, isActive).asApiEnvelope()
        val data = envelope.dataObject

        if (!envelope.success || data == null) {
            return Result.failure(IllegalStateException(envelope.message ?: "No se pudo actualizar el usuario"))
        }

        refreshLocalUsers()
        return Result.success(userFromJson(data))
    }

    // ------------------------------------------------------------------
    // Indicadores
    // ------------------------------------------------------------------

    /**
     * Calcula los indicadores del dashboard a partir de los tickets guardados.
     * Es un calculo puro: no llama a la API ni dibuja nada.
     */
    fun dashboardStats(): DashboardStats {
        val tickets = LocalStore.loadTickets()
        val resolved = tickets.filter { it.status == TicketStatus.RESOLVED || it.status == TicketStatus.CLOSED }

        val averageHours = if (resolved.isEmpty()) {
            0.0
        } else {
            resolved.map { (it.updatedAt - it.createdAt) / 3_600_000.0 }.average()
        }

        return DashboardStats(
            totalOpen = tickets.count { it.status == TicketStatus.OPEN },
            totalInProgress = tickets.count {
                it.status == TicketStatus.IN_PROGRESS || it.status == TicketStatus.PENDING
            },
            totalResolved = resolved.size,
            totalCritical = tickets.count { it.priority == TicketPriority.CRITICAL && !it.isClosed },
            averageResolutionHours = averageHours,
            workloadByTeam = tickets
                .filter { !it.isClosed }
                .groupingBy { it.resolverTeam }
                .eachCount()
        )
    }

    // ------------------------------------------------------------------
    // Internos
    // ------------------------------------------------------------------

    private suspend fun fetchAndCacheTickets(): Result<List<Ticket>> {
        val envelope = FakePanalApi.fetchTickets().asApiEnvelope()
        val array = envelope.dataArray

        if (!envelope.success || array == null) {
            return Result.failure(IllegalStateException(envelope.message ?: "No se pudo cargar la bandeja"))
        }

        val tickets = (0 until array.length()).mapNotNull { index ->
            array.optJSONObject(index)?.let { ticketFromJson(it) }
        }
        LocalStore.saveTickets(tickets)
        return Result.success(tickets)
    }

    /** Baja de nuevo la bandeja desde la API y la deja guardada. Se usa despues de cada cambio. */
    private suspend fun refreshLocalTickets(): Result<List<Ticket>> = fetchAndCacheTickets()

    private suspend fun refreshLocalUsers() {
        loadUsers(forceRefresh = true)
    }
}
