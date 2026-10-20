package com.panalbit.panalsuite.ui.navigation

/**
 * Rutas de la aplicacion.
 *
 * Se declaran como constantes para no escribir textos sueltos por el codigo:
 * un error de tipeo en "bandeja" compilaria y fallaria recien al navegar.
 */
object Routes {
    const val LOGIN = "login"
    const val INBOX = "inbox"
    const val NEW_TICKET = "newTicket"
    const val TICKET_DETAIL = "ticket/{ticketId}"
    const val DASHBOARD = "dashboard"
    const val ADMIN = "admin"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"

    /** Arma la ruta del detalle con el identificador del ticket ya puesto. */
    fun ticketDetail(ticketId: String) = "ticket/$ticketId"
}
