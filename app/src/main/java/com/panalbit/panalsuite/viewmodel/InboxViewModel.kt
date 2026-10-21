package com.panalbit.panalsuite.viewmodel

/**
 * Estado de la bandeja: la lista de tickets, el filtro y si está cargando.
 */

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panalbit.panalsuite.model.TicketPriority
import com.panalbit.panalsuite.model.TicketStatus
import com.panalbit.panalsuite.model.UserRole
import com.panalbit.panalsuite.repository.PanalSuiteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InboxViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(InboxUiState())
    val uiState: StateFlow<InboxUiState> = _uiState.asStateFlow()

    fun load(forceRefresh: Boolean = false) {
        val session = PanalSuiteRepository.currentSession()
        _uiState.value = _uiState.value.copy(
            isLoading = _uiState.value.tickets.isEmpty(),
            errorMessage = null,
            currentUserName = session?.fullName ?: "",
            currentUserRole = session?.role ?: UserRole.SOLICITANTE
        )

        viewModelScope.launch {
            val result = PanalSuiteRepository.loadTickets(forceRefresh)

            _uiState.value = result.fold(
                onSuccess = { tickets ->
                    _uiState.value.copy(tickets = tickets, isLoading = false)
                },
                onFailure = { error ->
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "No se pudo cargar la bandeja"
                    )
                }
            )
        }
    }

    fun refresh() {
        load(forceRefresh = true)
    }

    fun onSearchChange(query: String) {
        _uiState.value = _uiState.value.copy(filter = _uiState.value.filter.copy(searchQuery = query))
    }

    fun toggleStatusFilter(status: TicketStatus) {
        val current = _uiState.value.filter.status
        onStatusFilterChange(if (current == status) null else status)
    }

    fun onStatusFilterChange(status: TicketStatus?) {
        _uiState.value = _uiState.value.copy(filter = _uiState.value.filter.copy(status = status))
    }

    fun togglePriorityFilter(priority: TicketPriority) {
        val current = _uiState.value.filter.priority
        onPriorityFilterChange(if (current == priority) null else priority)
    }

    fun onPriorityFilterChange(priority: TicketPriority?) {
        _uiState.value = _uiState.value.copy(filter = _uiState.value.filter.copy(priority = priority))
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(filter = com.panalbit.panalsuite.model.TicketFilter())
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
