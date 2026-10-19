package com.panalbit.panalsuite.repository

import android.content.Context
import android.content.SharedPreferences
import com.panalbit.panalsuite.model.AuthSession
import com.panalbit.panalsuite.model.Ticket
import com.panalbit.panalsuite.model.User
import com.panalbit.panalsuite.model.UserRole
import org.json.JSONObject

/**
 * Almacenamiento local del telefono.
 *
 * Usa SharedPreferences, que ya viene dentro de Android: no hay que instalar
 * nada ni agregar librerias. Guarda la sesion, los tickets y los usuarios como
 * texto JSON, asi los datos siguen ahi cuando se cierra y se vuelve a abrir la app.
 *
 * Se inicializa una sola vez desde PanalSuiteApplication, antes de que se cree
 * cualquier pantalla.
 */
object LocalStore {

    private const val PREFS_NAME = "panalsuite_local"
    private const val KEY_SESSION = "session"
    private const val KEY_TICKETS = "tickets"
    private const val KEY_USERS = "users"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // ---------------- Sesion ----------------

    fun saveSession(session: AuthSession?) {
        val editor = prefs.edit()
        if (session == null) {
            editor.remove(KEY_SESSION)
        } else {
            editor.putString(KEY_SESSION, sessionToJson(session).toString())
        }
        editor.apply()
    }

    fun loadSession(): AuthSession? {
        val text = prefs.getString(KEY_SESSION, null) ?: return null
        return try {
            val json = JSONObject(text)
            AuthSession(
                userId = json.optString("userId"),
                username = json.optString("username"),
                fullName = json.optString("fullName"),
                role = UserRole.fromName(json.optString("role")),
                token = json.optString("token")
            )
        } catch (e: Exception) {
            null
        }
    }

    // ---------------- Tickets ----------------

    fun saveTickets(tickets: List<Ticket>) {
        prefs.edit().putString(KEY_TICKETS, ticketsToJson(tickets)).apply()
    }

    fun loadTickets(): List<Ticket> = ticketsFromJson(prefs.getString(KEY_TICKETS, null))

    // ---------------- Usuarios ----------------

    fun saveUsers(users: List<User>) {
        prefs.edit().putString(KEY_USERS, usersToJson(users)).apply()
    }

    fun loadUsers(): List<User> = usersFromJson(prefs.getString(KEY_USERS, null))

    private fun sessionToJson(session: AuthSession): JSONObject = JSONObject()
        .put("userId", session.userId)
        .put("username", session.username)
        .put("fullName", session.fullName)
        .put("role", session.role.name)
        .put("token", session.token)
}
