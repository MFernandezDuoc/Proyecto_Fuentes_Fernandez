package com.panalbit.panalsuite.viewmodel

/**
 * Indicadores de seguimiento para supervisor, gerencia y administrador.
 *
 * Los números no se guardan: se calculan con repository.dashboardStats() cada vez
 * que se cargan los tickets, así nunca quedan desfasados.
 */

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panalbit.panalsuite.repository.PanalSuiteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun load(forceRefresh: Boolean = false) {
        val session = PanalSuiteRepository.currentSession()
        _uiState.value = _uiState.value.copy(
            isLoading = !_uiState.value.hasData,
            errorMessage = null,
            currentUserName = session?.fullName ?: ""
        )

        viewModelScope.launch {
            val result = PanalSuiteRepository.loadTickets(forceRefresh)

            _uiState.value = result.fold(
                onSuccess = {
                    _uiState.value.copy(
                        stats = PanalSuiteRepository.dashboardStats(),
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "No se pudieron calcular los indicadores"
                    )
                }
            )
        }
    }

    fun refresh() {
        load(forceRefresh = true)
    }
}
