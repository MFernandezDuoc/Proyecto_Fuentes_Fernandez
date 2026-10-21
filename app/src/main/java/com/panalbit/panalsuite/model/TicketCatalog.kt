package com.panalbit.panalsuite.model

/**
 * Los valores que se ofrecen en los formularios.
 *
 * Están en un solo lugar para que el formulario de nueva solicitud y los filtros
 * de la bandeja usen exactamente las mismas opciones.
 */

object TicketCatalog {

    val types = listOf(
        "Infraestructura",
        "Software",
        "Acceso",
        "Requerimiento",
        "Administrativo",
        "Capacitación"
    )

    val categories = listOf(
        "Redes",
        "Servidores",
        "Punto de venta",
        "Rendimiento",
        "Usuarios y permisos",
        "Facturación",
        "Movilidad",
        "Producto",
        "Usuarios"
    )

    val branches = listOf(
        "Sucursal Alameda",
        "Sucursal Providencia",
        "Sucursal Nunoa",
        "Casa matriz",
        "Terreno - Zona Sur",
        "Cliente Farmacias Cruz"
    )
}
