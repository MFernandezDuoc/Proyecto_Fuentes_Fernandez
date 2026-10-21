package com.panalbit.panalsuite.ui.navigation

/**
 * Las rutas de la aplicación.
 *
 * Se declaran como constantes para no escribir textos sueltos por el código: un
 * error de tipeo en "bandeja" compilaría y fallaría recién al navegar.
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

    fun ticketDetail(ticketId: String) = "ticket/$ticketId"
}
