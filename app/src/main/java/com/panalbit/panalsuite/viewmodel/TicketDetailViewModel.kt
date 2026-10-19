package com.panalbit.panalsuite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panalbit.panalsuite.model.TicketStatus
import com.panalbit.panalsuite.model.UserRole
import com.panalbit.panalsuite.repository.PanalSuiteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Detalle de un ticket: sus datos, el cambio de estado, los comentarios y las evidencias.
 */
class TicketDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TicketDetailUiState())
    val uiState: StateFlow<TicketDetailUiState> = _uiState.asStateFlow()

    private var currentTicketId: String? = null

    /**
     * Abre un ticket. Se llama desde la navegacion, no desde el constructor,
     * porque el ViewModel se crea una sola vez junto con la Activity.
     */
    fun openTicket(ticketId: String) {
        currentTicketId = ticketId
        val session = PanalSuiteRepository.currentSession()
        _uiState.value = TicketDetailUiState(
            isLoading = true,
            currentUserName = session?.fullName ?: "",
            currentUserRole = session?.role ?: UserRole.SOLICITANTE
        )
        load()
    }

    fun load() {
        val ticketId = currentTicketId ?: return

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            // Primero se refresca la bandeja guardada y despues se busca el ticket.
            val result = PanalSuiteRepository.loadTickets()

            _uiState.value = result.fold(
                onSuccess = {
                    val ticket = PanalSuiteRepository.findTicket(ticketId)
                    if (ticket == null) {
                        _uiState.value.copy(isLoading = false, errorMessage = "El ticket $ticketId no existe")
                    } else {
                        _uiState.value.copy(ticket = ticket, isLoading = false)
                    }
                },
                onFailure = { error ->
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "No se pudo cargar el ticket"
                    )
                }
            )
        }
    }

    // ---------------- Comentario ----------------

    fun onCommentChange(value: String) {
        _uiState.value = _uiState.value.copy(commentText = value)
    }

    fun sendComment() {
        val state = _uiState.value
        val ticketId = currentTicketId ?: return

        if (state.commentError != null) {
            _uiState.value = state.copy(showCommentErrors = true)
            return
        }

        if (state.isSaving) return

        val author = PanalSuiteRepository.currentSession()?.fullName ?: "Usuario"
        _uiState.value = state.copy(isSaving = true, errorMessage = null)

        viewModelScope.launch {
            val result = PanalSuiteRepository.addComment(ticketId, author, state.commentText)

            _uiState.value = result.fold(
                onSuccess = {
                    val ticket = PanalSuiteRepository.findTicket(ticketId)
                    _uiState.value.copy(
                        ticket = ticket ?: _uiState.value.ticket,
                        isSaving = false,
                        commentText = "",
                        showCommentErrors = false,
                        feedbackMessage = "Comentario agregado"
                    )
                },
                onFailure = { error ->
                    _uiState.value.copy(isSaving = false, errorMessage = error.message ?: "No se pudo comentar")
                }
            )
        }
    }

    // ---------------- Cambio de estado ----------------

    fun onStatusSelected(status: TicketStatus) {
        _uiState.value = _uiState.value.copy(pendingStatus = status)
    }

    fun dismissStatusDialog() {
        _uiState.value = _uiState.value.copy(pendingStatus = null)
    }

    fun confirmStatusChange() {
        val state = _uiState.value
        val ticketId = currentTicketId ?: return
        val newStatus = state.pendingStatus ?: return

        val author = PanalSuiteRepository.currentSession()?.fullName ?: "Usuario"
        _uiState.value = state.copy(isSaving = true, pendingStatus = null, errorMessage = null)

        viewModelScope.launch {
            val result = PanalSuiteRepository.changeStatus(ticketId, newStatus, author)

            _uiState.value = result.fold(
                onSuccess = { ticket ->
                    _uiState.value.copy(
                        ticket = ticket,
                        isSaving = false,
                        feedbackMessage = "Estado actualizado a ${ticket.status.label}"
                    )
                },
                onFailure = { error ->
                    _uiState.value.copy(isSaving = false, errorMessage = error.message ?: "No se pudo cambiar el estado")
                }
            )
        }
    }

    // ---------------- Evidencia ----------------

    fun showEvidenceDialog() {
        _uiState.value = _uiState.value.copy(
            showEvidenceDialog = true,
            evidenceDescription = "",
            evidenceIncludeLocation = true,
            showEvidenceErrors = false
        )
    }

    fun dismissEvidenceDialog() {
        _uiState.value = _uiState.value.copy(showEvidenceDialog = false)
    }

    fun onEvidenceDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(evidenceDescription = value)
    }

    fun onEvidenceLocationToggle(include: Boolean) {
        _uiState.value = _uiState.value.copy(evidenceIncludeLocation = include)
    }

    fun saveEvidence() {
        val state = _uiState.value
        val ticketId = currentTicketId ?: return

        if (state.evidenceError != null) {
            _uiState.value = state.copy(showEvidenceErrors = true)
            return
        }

        val location = if (state.evidenceIncludeLocation) state.simulatedLocation else null
        _uiState.value = state.copy(isSaving = true, errorMessage = null)

        viewModelScope.launch {
            val result = PanalSuiteRepository.addEvidence(ticketId, state.evidenceDescription, location)

            _uiState.value = result.fold(
                onSuccess = {
                    val ticket = PanalSuiteRepository.findTicket(ticketId)
                    _uiState.value.copy(
                        ticket = ticket ?: _uiState.value.ticket,
                        isSaving = false,
                        showEvidenceDialog = false,
                        evidenceDescription = "",
                        feedbackMessage = "Evidencia registrada"
                    )
                },
                onFailure = { error ->
                    _uiState.value.copy(isSaving = false, errorMessage = error.message ?: "No se pudo guardar la evidencia")
                }
            )
        }
    }

    // ---------------- Mensajes ----------------

    fun onFeedbackShown() {
        _uiState.value = _uiState.value.copy(feedbackMessage = null)
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
