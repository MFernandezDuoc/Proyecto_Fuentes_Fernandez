package com.panalbit.panalsuite.repository

import com.panalbit.panalsuite.model.ChangeEntry
import com.panalbit.panalsuite.model.Comment
import com.panalbit.panalsuite.model.Evidence
import com.panalbit.panalsuite.model.Ticket
import com.panalbit.panalsuite.model.TicketPriority
import com.panalbit.panalsuite.model.TicketStatus
import com.panalbit.panalsuite.model.User
import com.panalbit.panalsuite.model.UserRole
import org.json.JSONArray
import org.json.JSONObject

/**
 * Conversiones entre los modelos de Kotlin y el texto JSON.
 *
 * Se usan las clases org.json que ya vienen dentro de Android, asi no hay que
 * agregar ninguna libreria al proyecto.
 *
 * La regla es una sola: todo lo que entra desde JSON pasa por `optAlgo` con un
 * valor por defecto, para que un campo faltante no cierre la aplicacion.
 */

// ---------------- USUARIO ----------------

fun User.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("username", username)
    .put("fullName", fullName)
    .put("email", email)
    .put("role", role.name)
    .put("isActive", isActive)

fun userFromJson(json: JSONObject): User = User(
    id = json.optString("id"),
    username = json.optString("username"),
    fullName = json.optString("fullName"),
    email = json.optString("email"),
    role = UserRole.fromName(json.optString("role")),
    isActive = json.optBoolean("isActive", true)
)

// ---------------- COMENTARIO ----------------

fun Comment.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("authorName", authorName)
    .put("content", content)
    .put("createdAt", createdAt)

fun commentFromJson(json: JSONObject): Comment = Comment(
    id = json.optString("id"),
    authorName = json.optString("authorName"),
    content = json.optString("content"),
    createdAt = json.optLong("createdAt")
)

// ---------------- EVIDENCIA ----------------

fun Evidence.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("description", description)
    .put("location", location)
    .put("createdAt", createdAt)

fun evidenceFromJson(json: JSONObject): Evidence = Evidence(
    id = json.optString("id"),
    description = json.optString("description"),
    location = if (json.isNull("location")) null else json.optString("location"),
    createdAt = json.optLong("createdAt")
)

// ---------------- TRAZABILIDAD ----------------

fun ChangeEntry.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("field", field)
    .put("oldValue", oldValue)
    .put("newValue", newValue)
    .put("changedBy", changedBy)
    .put("changedAt", changedAt)

fun changeEntryFromJson(json: JSONObject): ChangeEntry = ChangeEntry(
    id = json.optString("id"),
    field = json.optString("field"),
    oldValue = json.optString("oldValue"),
    newValue = json.optString("newValue"),
    changedBy = json.optString("changedBy"),
    changedAt = json.optLong("changedAt")
)

// ---------------- TICKET ----------------

fun Ticket.toJson(): JSONObject = JSONObject()
    .put("id", id)
    .put("title", title)
    .put("type", type)
    .put("category", category)
    .put("priority", priority.name)
    .put("status", status.name)
    .put("branch", branch)
    .put("requesterName", requesterName)
    .put("resolverTeam", resolverTeam)
    .put("assignedTo", assignedTo)
    .put("description", description)
    .put("createdAt", createdAt)
    .put("updatedAt", updatedAt)
    .put("comments", JSONArray().apply { comments.forEach { put(it.toJson()) } })
    .put("evidences", JSONArray().apply { evidences.forEach { put(it.toJson()) } })
    .put("history", JSONArray().apply { history.forEach { put(it.toJson()) } })

fun ticketFromJson(json: JSONObject): Ticket = Ticket(
    id = json.optString("id"),
    title = json.optString("title"),
    type = json.optString("type"),
    category = json.optString("category"),
    priority = TicketPriority.fromName(json.optString("priority")),
    status = TicketStatus.fromName(json.optString("status")),
    branch = json.optString("branch"),
    requesterName = json.optString("requesterName"),
    resolverTeam = json.optString("resolverTeam"),
    assignedTo = if (json.isNull("assignedTo")) null else json.optString("assignedTo"),
    description = json.optString("description"),
    createdAt = json.optLong("createdAt"),
    updatedAt = json.optLong("updatedAt"),
    comments = json.optJSONArray("comments").toCommentList(),
    evidences = json.optJSONArray("evidences").toEvidenceList(),
    history = json.optJSONArray("history").toChangeList()
)

private fun JSONArray?.toCommentList(): List<Comment> {
    if (this == null) return emptyList()
    return (0 until length()).mapNotNull { index ->
        optJSONObject(index)?.let { commentFromJson(it) }
    }
}

private fun JSONArray?.toEvidenceList(): List<Evidence> {
    if (this == null) return emptyList()
    return (0 until length()).mapNotNull { index ->
        optJSONObject(index)?.let { evidenceFromJson(it) }
    }
}

private fun JSONArray?.toChangeList(): List<ChangeEntry> {
    if (this == null) return emptyList()
    return (0 until length()).mapNotNull { index ->
        optJSONObject(index)?.let { changeEntryFromJson(it) }
    }
}

// ---------------- LISTAS COMPLETAS ----------------

fun ticketsToJson(tickets: List<Ticket>): String {
    val array = JSONArray()
    tickets.forEach { array.put(it.toJson()) }
    return array.toString()
}

fun ticketsFromJson(text: String?): List<Ticket> {
    if (text.isNullOrBlank()) return emptyList()
    return try {
        val array = JSONArray(text)
        (0 until array.length()).mapNotNull { index ->
            array.optJSONObject(index)?.let { ticketFromJson(it) }
        }
    } catch (e: Exception) {
        // Si el texto guardado quedo corrupto o viene de otra version, se descarta
        // y la aplicacion vuelve a pedir la lista original.
        emptyList()
    }
}

fun usersToJson(users: List<User>): String {
    val array = JSONArray()
    users.forEach { array.put(it.toJson()) }
    return array.toString()
}

fun usersFromJson(text: String?): List<User> {
    if (text.isNullOrBlank()) return emptyList()
    return try {
        val array = JSONArray(text)
        (0 until array.length()).mapNotNull { index ->
            array.optJSONObject(index)?.let { userFromJson(it) }
        }
    } catch (e: Exception) {
        emptyList()
    }
}

// ---------------- RESPUESTA DE LA API ----------------

/**
 * Forma que tienen todas las respuestas de la API:
 * { "success": true, "message": "...", "data": { ... } }
 */
class ApiEnvelope(private val json: JSONObject) {

    val success: Boolean
        get() = json.optBoolean("success", false)

    val message: String?
        get() = if (json.has("message") && !json.isNull("message")) json.getString("message") else null

    val dataObject: JSONObject?
        get() = json.optJSONObject("data")

    val dataArray: JSONArray?
        get() = json.optJSONArray("data")
}

/** Lee el texto JSON de una respuesta de la API. Si viene malformado, devuelve una respuesta fallida. */
fun String.asApiEnvelope(): ApiEnvelope = try {
    ApiEnvelope(JSONObject(this))
} catch (e: Exception) {
    ApiEnvelope(JSONObject().put("success", false).put("message", "Respuesta invalida del servidor"))
}
