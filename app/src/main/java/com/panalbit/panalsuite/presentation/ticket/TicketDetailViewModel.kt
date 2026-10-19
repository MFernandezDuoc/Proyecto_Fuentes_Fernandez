package com.panalbit.panalsuite.presentation.ticket

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panalbit.panalsuite.domain.model.Comment
import com.panalbit.panalsuite.domain.model.Ticket
import com.panalbit.panalsuite.domain.model.TicketStatus
import com.panalbit.panalsuite.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TicketDetailUiState(
    val ticket: Ticket? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val newCommentText: String = ""
)

@HiltViewModel
class TicketDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTicketDetailUseCase: GetTicketDetailUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val updateTicketStatusUseCase: UpdateTicketStatusUseCase
) : ViewModel() {

    val ticketId: String = savedStateHandle["ticketId"] ?: ""

    private val _uiState = MutableStateFlow(TicketDetailUiState())
    val uiState: StateFlow<TicketDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    fun loadDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val res = getTicketDetailUseCase(ticketId)
            res.onSuccess { ticket ->
                _uiState.update { it.copy(ticket = ticket, isLoading = false) }
            }.onFailure { err ->
                _uiState.update { it.copy(error = err.message, isLoading = false) }
            }
        }
    }

    fun onCommentChange(text: String) {
        _uiState.update { it.copy(newCommentText = text) }
    }

    fun sendComment() {
        val text = _uiState.value.newCommentText
        if (text.isBlank()) return

        viewModelScope.launch {
            val res = addCommentUseCase(ticketId, text)
            res.onSuccess {
                _uiState.update { it.copy(newCommentText = "") }
                loadDetail()
            }
        }
    }

    fun changeStatus(newStatus: TicketStatus) {
        viewModelScope.launch {
            updateTicketStatusUseCase(ticketId, newStatus, "Cambio de estado")
            loadDetail()
        }
    }
}
