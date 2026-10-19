package com.panalbit.panalsuite.model

/**
 * Valores que se ofrecen en los formularios.
 *
 * Estan en un solo lugar para que el formulario de nueva solicitud y el filtro
 * de la bandeja usen exactamente las mismas opciones.
 */
object TicketCatalog {

    val types = listOf(
        "Infraestructura",
        "Software",
        "Acceso",
        "Requerimiento",
        "Administrativo",
        "Capacitacion"
    )

    val categories = listOf(
        "Redes",
        "Servidores",
        "Punto de venta",
        "Rendimiento",
        "Usuarios y permisos",
        "Facturacion",
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
