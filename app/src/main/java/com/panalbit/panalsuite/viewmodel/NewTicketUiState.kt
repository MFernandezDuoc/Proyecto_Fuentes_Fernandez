package com.panalbit.panalsuite.viewmodel

/**
 * Estado del formulario de nueva solicitud.
 *
 * Todos los errores se calculan con get(): el código del problema se valida
 * contra la lista de tipos y el texto libre tiene un largo mínimo.
 */

import com.panalbit.panalsuite.model.TicketCatalog
import com.panalbit.panalsuite.model.TicketPriority

data class NewTicketUiState(
    val title: String = "",
    val description: String = "",
    val type: String = TicketCatalog.types.first(),
    val category: String = TicketCatalog.categories.first(),
    val priority: TicketPriority = TicketPriority.MEDIUM,
    val branch: String = TicketCatalog.branches.first(),
    val showErrors: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val createdTicketId: String? = null
) {

    val titleError: String?
        get() = if (title.trim().length < 6) {
            "Resume el problema en al menos 6 caracteres"
        } else {
            null
        }

    val descriptionError: String?
        get() = if (description.trim().length < 15) {
            "Agrega un detalle de al menos 15 caracteres"
        } else {
            null
        }

    val isValid: Boolean
        get() = titleError == null && descriptionError == null

    val canSubmit: Boolean
        get() = isValid && !isSaving

    val descriptionCounter: String
        get() = "${description.trim().length} de 15 caracteres minimos"
}
