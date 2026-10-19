package com.panalbit.panalsuite.repository

import com.panalbit.panalsuite.model.ChangeEntry
import com.panalbit.panalsuite.model.Comment
import com.panalbit.panalsuite.model.Evidence
import com.panalbit.panalsuite.model.Ticket
import com.panalbit.panalsuite.model.TicketPriority
import com.panalbit.panalsuite.model.TicketStatus
import com.panalbit.panalsuite.model.User
import com.panalbit.panalsuite.model.UserRole
import kotlinx.coroutines.delay
import org.json.JSONObject

/**
 * API simulada de PanalSuite.
 *
 * Imita los endpoints de una API REST pero funciona entera dentro del telefono:
 * no hay internet, no hay servidor y no se usa ninguna credencial real.
 *
 * Cada funcion hace lo mismo que haria una llamada de red:
 *  1. espera un momento (delay) para que la pantalla pueda mostrar el estado de carga,
 *  2. responde un texto JSON con la forma { "success": ..., "message": ..., "data": ... }
 *
 * El repositorio es el unico que habla con esta clase, y la pantalla nunca la conoce.
 */
object FakePanalApi {

    /** Latencia simulada de la red, en milisegundos. */
    private const val NETWORK_DELAY_MS = 450L

    /** Contrasena unica de todos los usuarios de prueba. */
    const val DEMO_PASSWORD = "1234"

    private var ticketCounter = 1000
    private var commentCounter = 0
    private var evidenceCounter = 0
    private var userCounter = 10

    /** Lista de usuarios de prueba, uno por cada rol del sistema. */
    val demoUsers: List<User> = listOf(
        User("U-01", "solicitante", "Carolina Soto", "carolina.soto@panalbit.cl", UserRole.SOLICITANTE),
        User("U-02", "resolutor", "Diego Munoz", "diego.munoz@panalbit.cl", UserRole.RESOLUTOR),
        User("U-03", "supervisor", "Paula Rivas", "paula.rivas@panalbit.cl", UserRole.SUPERVISOR),
        User("U-04", "gerencia", "Andres Leon", "andres.leon@panalbit.cl", UserRole.GERENCIA),
        User("U-05", "admin", "Martin Fuentes", "martin.fuentes@panalbit.cl", UserRole.ADMINISTRADOR)
    )

    private val users = demoUsers.toMutableList()

    private val tickets = seedTickets().toMutableList()

    // ------------------------------------------------------------------
    // Autenticacion
    // ------------------------------------------------------------------

    suspend fun login(username: String, password: String): String {
        delay(NETWORK_DELAY_MS)

        val user = users.firstOrNull { it.username.equals(username.trim(), ignoreCase = true) }

        return when {
            user == null -> failure("No existe un usuario con ese nombre")
            password != DEMO_PASSWORD -> failure("La contrasena no es correcta")
            !user.isActive -> failure("El usuario esta desactivado, contacta al administrador")
            else -> success(
                user.toJson().put("token", "demo-token-${user.id}")
            )
        }
    }

    // ------------------------------------------------------------------
    // Tickets
    // ------------------------------------------------------------------

    suspend fun fetchTickets(): String {
        delay(NETWORK_DELAY_MS)
        return successArray(tickets.sortedByDescending { it.updatedAt }.map { it.toJson() })
    }

    suspend fun createTicket(
        requesterName: String,
        title: String,
        type: String,
        category: String,
        priority: TicketPriority,
        branch: String,
        description: String
    ): String {
        delay(NETWORK_DELAY_MS)

        val now = System.currentTimeMillis()
        ticketCounter += 1

        val ticket = Ticket(
            id = "C-$ticketCounter",
            title = title.trim(),
            type = type,
            category = category,
            priority = priority,
            status = TicketStatus.OPEN,
            branch = branch,
            requesterName = requesterName,
            resolverTeam = "Mesa de Ayuda",
            assignedTo = null,
            description = description.trim(),
            createdAt = now,
            updatedAt = now,
            history = listOf(
                ChangeEntry(
                    id = nextId("H"),
                    field = "Estado",
                    oldValue = "-",
                    newValue = TicketStatus.OPEN.label,
                    changedBy = requesterName,
                    changedAt = now
                )
            )
        )

        tickets.add(0, ticket)
        return success(ticket.toJson())
    }

    suspend fun updateTicketStatus(
        ticketId: String,
        newStatus: TicketStatus,
        changedBy: String
    ): String {
        delay(NETWORK_DELAY_MS)

        val index = tickets.indexOfFirst { it.id == ticketId }
        if (index == -1) return failure("El ticket $ticketId ya no existe")

        val current = tickets[index]
        val now = System.currentTimeMillis()

        val updated = current.copy(
            status = newStatus,
            updatedAt = now,
            history = current.history + ChangeEntry(
                id = nextId("H"),
                field = "Estado",
                oldValue = current.status.label,
                newValue = newStatus.label,
                changedBy = changedBy,
                changedAt = now
            )
        )

        tickets[index] = updated
        return success(updated.toJson())
    }

    suspend fun addComment(ticketId: String, authorName: String, content: String): String {
        delay(NETWORK_DELAY_MS)

        val index = tickets.indexOfFirst { it.id == ticketId }
        if (index == -1) return failure("El ticket $ticketId ya no existe")

        val now = System.currentTimeMillis()
        val comment = Comment(
            id = nextId("K"),
            authorName = authorName,
            content = content.trim(),
            createdAt = now
        )

        val current = tickets[index]
        tickets[index] = current.copy(
            comments = current.comments + comment,
            updatedAt = now
        )

        return success(comment.toJson())
    }

    suspend fun addEvidence(
        ticketId: String,
        description: String,
        location: String?
    ): String {
        delay(NETWORK_DELAY_MS)

        val index = tickets.indexOfFirst { it.id == ticketId }
        if (index == -1) return failure("El ticket $ticketId ya no existe")

        val now = System.currentTimeMillis()
        val evidence = Evidence(
            id = nextId("E"),
            description = description.trim(),
            location = location,
            createdAt = now
        )

        val current = tickets[index]
        tickets[index] = current.copy(
            evidences = current.evidences + evidence,
            updatedAt = now
        )

        return success(evidence.toJson())
    }

    // ------------------------------------------------------------------
    // Usuarios
    // ------------------------------------------------------------------

    suspend fun fetchUsers(): String {
        delay(NETWORK_DELAY_MS)
        return successArray(users.map { it.toJson() })
    }

    suspend fun createUser(
        fullName: String,
        username: String,
        email: String,
        role: UserRole
    ): String {
        delay(NETWORK_DELAY_MS)

        val exists = users.any { it.username.equals(username.trim(), ignoreCase = true) }
        if (exists) return failure("Ya existe un usuario con ese nombre de acceso")

        userCounter += 1
        val user = User(
            id = "U-$userCounter",
            username = username.trim().lowercase(),
            fullName = fullName.trim(),
            email = email.trim().lowercase(),
            role = role,
            isActive = true
        )

        users.add(user)
        return success(user.toJson())
    }

    suspend fun setUserActive(userId: String, isActive: Boolean): String {
        delay(NETWORK_DELAY_MS)

        val index = users.indexOfFirst { it.id == userId }
        if (index == -1) return failure("El usuario ya no existe")

        val updated = users[index].copy(isActive = isActive)
        users[index] = updated
        return success(updated.toJson())
    }

    // ------------------------------------------------------------------
    // Respuestas
    // ------------------------------------------------------------------

    private fun success(data: JSONObject): String =
        JSONObject().put("success", true).put("data", data).toString()

    private fun successArray(data: List<JSONObject>): String {
        val array = org.json.JSONArray()
        data.forEach { array.put(it) }
        return JSONObject().put("success", true).put("data", array).toString()
    }

    private fun failure(message: String): String =
        JSONObject().put("success", false).put("message", message).toString()

    private fun nextId(prefix: String): String {
        return when (prefix) {
            "K" -> "K-${++commentCounter}"
            "E" -> "E-${++evidenceCounter}"
            else -> "H-${++commentCounter}${evidenceCounter}"
        }
    }

    // ------------------------------------------------------------------
    // Datos de prueba (ficticios)
    // ------------------------------------------------------------------

    private fun hoursAgo(hours: Long): Long =
        System.currentTimeMillis() - hours * 60L * 60L * 1000L

    private fun seedTickets(): List<Ticket> = listOf(
        Ticket(
            id = "C-1001",
            title = "Falla de conexion en caja 3",
            type = "Infraestructura",
            category = "Redes",
            priority = TicketPriority.CRITICAL,
            status = TicketStatus.OPEN,
            branch = "Sucursal Alameda",
            requesterName = "Carolina Soto",
            resolverTeam = "Infraestructura",
            assignedTo = "Diego Munoz",
            description = "La caja 3 pierde la conexion con el servidor cada 10 minutos y no permite cerrar ventas.",
            createdAt = hoursAgo(3),
            updatedAt = hoursAgo(3),
            history = listOf(
                ChangeEntry("H-1", "Estado", "-", "Abierto", "Carolina Soto", hoursAgo(3))
            )
        ),
        Ticket(
            id = "C-1002",
            title = "Error al imprimir boleta",
            type = "Software",
            category = "Punto de venta",
            priority = TicketPriority.HIGH,
            status = TicketStatus.IN_PROGRESS,
            branch = "Sucursal Providencia",
            requesterName = "Carolina Soto",
            resolverTeam = "Soporte Aplicaciones",
            assignedTo = "Diego Munoz",
            description = "Al cerrar la venta la impresora responde con error de formato y la boleta queda en cola.",
            createdAt = hoursAgo(8),
            updatedAt = hoursAgo(2),
            comments = listOf(
                Comment("K-1", "Diego Munoz", "Se solicito al proveedor la configuracion de la impresora.", hoursAgo(4))
            ),
            evidences = listOf(
                Evidence("E-1", "Pantalla con el error de impresion", null, hoursAgo(3))
            ),
            history = listOf(
                ChangeEntry("H-2", "Estado", "Abierto", "En progreso", "Diego Munoz", hoursAgo(2))
            )
        ),
        Ticket(
            id = "C-1003",
            title = "Solicitud de usuario nuevo en Retail",
            type = "Acceso",
            category = "Usuarios y permisos",
            priority = TicketPriority.MEDIUM,
            status = TicketStatus.PENDING,
            branch = "Casa matriz",
            requesterName = "Marcela Diaz",
            resolverTeam = "Mesa de Ayuda",
            assignedTo = null,
            description = "Se requiere crear acceso de solo lectura para el nuevo analista de Retail.",
            createdAt = hoursAgo(26),
            updatedAt = hoursAgo(20),
            history = listOf(
                ChangeEntry("H-3", "Estado", "Abierto", "Pendiente", "Paula Rivas", hoursAgo(20))
            )
        ),
        Ticket(
            id = "C-1004",
            title = "Lentitud en modulo de cobranzas",
            type = "Software",
            category = "Rendimiento",
            priority = TicketPriority.HIGH,
            status = TicketStatus.IN_PROGRESS,
            branch = "Sucursal Nunoa",
            requesterName = "Andres Leon",
            resolverTeam = "Soporte Aplicaciones",
            assignedTo = "Diego Munoz",
            description = "El listado de cobranzas demora mas de 30 segundos en cargar durante la manana.",
            createdAt = hoursAgo(30),
            updatedAt = hoursAgo(6),
            comments = listOf(
                Comment("K-2", "Diego Munoz", "Se agrego un indice a la consulta del listado.", hoursAgo(6))
            ),
            history = listOf(
                ChangeEntry("H-4", "Estado", "Abierto", "En progreso", "Diego Munoz", hoursAgo(24))
            )
        ),
        Ticket(
            id = "C-1005",
            title = "Cambio de direccion de facturacion",
            type = "Administrativo",
            category = "Facturacion",
            priority = TicketPriority.LOW,
            status = TicketStatus.RESOLVED,
            branch = "Casa matriz",
            requesterName = "Marcela Diaz",
            resolverTeam = "Administracion",
            assignedTo = "Paula Rivas",
            description = "Actualizar la direccion de facturacion del cliente Legal Andes.",
            createdAt = hoursAgo(50),
            updatedAt = hoursAgo(28),
            history = listOf(
                ChangeEntry("H-5", "Estado", "En progreso", "Resuelto", "Paula Rivas", hoursAgo(28))
            )
        ),
        Ticket(
            id = "C-1006",
            title = "Tablet de terreno sin sincronizar",
            type = "Infraestructura",
            category = "Movilidad",
            priority = TicketPriority.HIGH,
            status = TicketStatus.PENDING,
            branch = "Terreno - Zona Sur",
            requesterName = "Diego Munoz",
            resolverTeam = "Infraestructura",
            assignedTo = null,
            description = "Una tablet de terreno lleva tres dias sin sincronizar las atenciones del dia.",
            createdAt = hoursAgo(54),
            updatedAt = hoursAgo(48),
            history = listOf(
                ChangeEntry("H-6", "Estado", "Abierto", "Pendiente", "Paula Rivas", hoursAgo(48))
            )
        ),
        Ticket(
            id = "C-1007",
            title = "Farmacia requiere modulo de recetas",
            type = "Requerimiento",
            category = "Producto",
            priority = TicketPriority.MEDIUM,
            status = TicketStatus.OPEN,
            branch = "Cliente Farmacias Cruz",
            requesterName = "Paula Rivas",
            resolverTeam = "Producto",
            assignedTo = null,
            description = "El cliente pide evaluar un modulo de recetas con trazabilidad de entrega.",
            createdAt = hoursAgo(72),
            updatedAt = hoursAgo(72),
            history = listOf(
                ChangeEntry("H-7", "Estado", "-", "Abierto", "Paula Rivas", hoursAgo(72))
            )
        ),
        Ticket(
            id = "C-1008",
            title = "Filtro de tickets por sucursal",
            type = "Requerimiento",
            category = "Producto",
            priority = TicketPriority.LOW,
            status = TicketStatus.IN_PROGRESS,
            branch = "Casa matriz",
            requesterName = "Andres Leon",
            resolverTeam = "Producto",
            assignedTo = "Paula Rivas",
            description = "Permitir filtrar la bandeja por sucursal para revisar la carga por local.",
            createdAt = hoursAgo(96),
            updatedAt = hoursAgo(40),
            history = listOf(
                ChangeEntry("H-8", "Estado", "Abierto", "En progreso", "Paula Rivas", hoursAgo(40))
            )
        ),
        Ticket(
            id = "C-1009",
            title = "Caida de servicio de correo",
            type = "Infraestructura",
            category = "Servidores",
            priority = TicketPriority.CRITICAL,
            status = TicketStatus.RESOLVED,
            branch = "Casa matriz",
            requesterName = "Carolina Soto",
            resolverTeam = "Infraestructura",
            assignedTo = "Diego Munoz",
            description = "El servidor de correo dejo de responder y afecto las notificaciones de tickets.",
            createdAt = hoursAgo(120),
            updatedAt = hoursAgo(110),
            evidences = listOf(
                Evidence("E-2", "Grafico de disponibilidad del servicio", "Santiago, Chile", hoursAgo(112))
            ),
            history = listOf(
                ChangeEntry("H-9", "Estado", "En progreso", "Resuelto", "Diego Munoz", hoursAgo(110))
            )
        ),
        Ticket(
            id = "C-1010",
            title = "Curso de uso de la app para proveedores",
            type = "Capacitacion",
            category = "Usuarios",
            priority = TicketPriority.LOW,
            status = TicketStatus.CLOSED,
            branch = "Sucursal Alameda",
            requesterName = "Marcela Diaz",
            resolverTeam = "Mesa de Ayuda",
            assignedTo = "Paula Rivas",
            description = "Capacitacion de una hora para que los proveedores registren sus solicitudes.",
            createdAt = hoursAgo(200),
            updatedAt = hoursAgo(150),
            history = listOf(
                ChangeEntry("H-10", "Estado", "Resuelto", "Cerrado", "Paula Rivas", hoursAgo(150))
            )
        )
    )
}
