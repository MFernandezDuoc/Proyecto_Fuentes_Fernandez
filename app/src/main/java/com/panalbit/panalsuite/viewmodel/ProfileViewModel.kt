package com.panalbit.panalsuite.viewmodel

import androidx.lifecycle.ViewModel
import com.panalbit.panalsuite.repository.PanalSuiteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Datos de la sesion activa y de lo que hay guardado en el telefono.
 *
 * Todo sale del almacenamiento local, por eso no es una funcion suspend:
 * leer SharedPreferences es inmediato.
 */
class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun load() {
        _uiState.value = ProfileUiState(
            session = PanalSuiteRepository.currentSession(),
            savedTickets = PanalSuiteRepository.savedTicketCount(),
            savedUsers = PanalSuiteRepository.savedUserCount(),
            savedComments = PanalSuiteRepository.savedCommentCount()
        )
    }

    fun logout() {
        PanalSuiteRepository.logout()
        _uiState.value = ProfileUiState()
    }
}
