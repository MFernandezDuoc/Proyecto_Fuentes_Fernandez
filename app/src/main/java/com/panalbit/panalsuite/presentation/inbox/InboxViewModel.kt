package com.panalbit.panalsuite.presentation.inbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panalbit.panalsuite.domain.model.*
import com.panalbit.panalsuite.domain.usecase.GetTicketsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class InboxUiState(
    val tickets: List<Ticket> = emptyList(),
    val isLoading: Boolean = false,
    val selectedStatus: TicketStatus? = null,
    val searchQuery: String = ""
)

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val getTicketsUseCase: GetTicketsUseCase
) : ViewModel() {

    private val _filter = MutableStateFlow(TicketFilter())
    private val _uiState = MutableStateFlow(InboxUiState())
    val uiState: StateFlow<InboxUiState> = _uiState.asStateFlow()

    init {
        _filter
            .flatMapLatest { filter -> getTicketsUseCase(filter) }
            .onEach { tickets ->
                _uiState.update { it.copy(tickets = tickets, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun onStatusFilterSelect(status: TicketStatus?) {
        _uiState.update { it.copy(selectedStatus = status) }
        _filter.update { it.copy(status = status) }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        _filter.update { it.copy(searchQuery = query) }
    }
}
