package com.panalbit.panalsuite.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.panalbit.panalsuite.model.TicketCatalog
import com.panalbit.panalsuite.model.TicketPriority
import com.panalbit.panalsuite.repository.PanalSuiteRepository
import com.panalbit.panalsuite.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Formulario para registrar una solicitud nueva.
 */
class NewTicketViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepository = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow(NewTicketUiState())
    val uiState: StateFlow<NewTicketUiState> = _uiState.asStateFlow()

    private val types = TicketCatalog.types
    private val categories = TicketCatalog.categories
    private val branches = TicketCatalog.branches

    /** La prioridad preferida, la que se eligio en Ajustes. */
    private var defaultPriority = TicketPriority.MEDIUM

    init {
        // La prioridad por defecto viene guardada: si el usuario la cambia en
        // Ajustes, el formulario parte con la nueva.
        viewModelScope.launch {
            preferencesRepository.userPreferences
                .map { it.defaultPriority }
                .distinctUntilChanged()
                .collect { priority ->
                    defaultPriority = priority
                    _uiState.value = _uiState.value.copy(priority = priority)
                }
        }
    }

    fun onTitleChange(value: String) {
        _uiState.value = _uiState.value.copy(title = value, errorMessage = null)
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(description = value, errorMessage = null)
    }

    fun onTypeChange(value: String) {
        _uiState.value = _uiState.value.copy(type = value)
    }

    fun onCategoryChange(value: String) {
        _uiState.value = _uiState.value.copy(category = value)
    }

    fun onPriorityChange(value: TicketPriority) {
        _uiState.value = _uiState.value.copy(priority = value)
    }

    fun onBranchChange(value: String) {
        _uiState.value = _uiState.value.copy(branch = value)
    }

    /** Valida y guarda. El resultado queda en `createdTicketId` y la pantalla navega. */
    fun submit() {
        val state = _uiState.value

        if (!state.isValid) {
            _uiState.value = state.copy(showErrors = true)
            return
        }

        if (state.isSaving) return

        val requesterName = PanalSuiteRepository.currentSession()?.fullName ?: "Solicitante"
        _uiState.value = state.copy(showErrors = true, isSaving = true, errorMessage = null)

        viewModelScope.launch {
            val result = PanalSuiteRepository.createTicket(
                requesterName = requesterName,
                title = state.title,
                type = state.type,
                category = state.category,
                priority = state.priority,
                branch = state.branch,
                description = state.description
            )

            _uiState.value = result.fold(
                onSuccess = { ticket ->
                    _uiState.value.copy(isSaving = false, createdTicketId = ticket.id)
                },
                onFailure = { error ->
                    _uiState.value.copy(
                        isSaving = false,
                        errorMessage = error.message ?: "No se pudo crear la solicitud"
                    )
                }
            )
        }
    }

    /** La pantalla avisa que ya navego, para no repetir la navegacion. */
    fun onCreatedHandled() {
        _uiState.value = _uiState.value.copy(createdTicketId = null)
    }

    /** Limpia el formulario, dejando la prioridad que eligio el usuario en Ajustes. */
    fun reset() {
        _uiState.value = NewTicketUiState(priority = defaultPriority)
    }

    fun types(): List<String> = types

    fun categories(): List<String> = categories

    fun branches(): List<String> = branches
}
