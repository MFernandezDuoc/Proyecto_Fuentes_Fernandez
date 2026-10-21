package com.panalbit.panalsuite.viewmodel

/**
 * Estado de la pantalla de detalle de un ticket.
 *
 * Además del ticket guarda dos mini formularios: el de comentario y el de
 * evidencia. Cada uno tiene su propio error calculado con get().
 */

import com.panalbit.panalsuite.model.Ticket
import com.panalbit.panalsuite.model.TicketStatus
import com.panalbit.panalsuite.model.UserRole

data class TicketDetailUiState(
    val ticket: Ticket? = null,
    val currentUserName: String = "",
    val currentUserRole: UserRole = UserRole.SOLICITANTE,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val feedbackMessage: String? = null,

    val commentText: String = "",
    val showCommentErrors: Boolean = false,

    val pendingStatus: TicketStatus? = null,

    val showEvidenceDialog: Boolean = false,
    val evidenceDescription: String = "",
    val evidenceIncludeLocation: Boolean = true,
    val showEvidenceErrors: Boolean = false
) {

    val commentError: String?
        get() = if (commentText.trim().length < 4) {
            "Escribe un comentario de al menos 4 caracteres"
        } else {
            null
        }

    val evidenceError: String?
        get() = if (evidenceDescription.trim().length < 6) {
            "Describe la evidencia en al menos 6 caracteres"
        } else {
            null
        }

    val canChangeStatus: Boolean
        get() = ticket != null && !ticket.isClosed

    val availableStatuses: List<TicketStatus>
        get() = ticket?.allowedNextStatuses ?: emptyList()

    val canSendComment: Boolean
        get() = commentError == null && !isSaving

    val canSaveEvidence: Boolean
        get() = evidenceError == null && !isSaving

    val simulatedLocation: String
        get() = "Santiago, Chile (-33.4489, -70.6693)"

    val commentCountText: String
        get() = when (val count = ticket?.comments?.size ?: 0) {
            0 -> "Sin comentarios"
            1 -> "1 comentario"
            else -> "$count comentarios"
        }

    val canManage: Boolean
        get() = currentUserRole.canManageTickets
}
