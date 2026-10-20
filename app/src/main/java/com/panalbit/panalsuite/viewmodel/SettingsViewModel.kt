package com.panalbit.panalsuite.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.panalbit.panalsuite.model.ThemeMode
import com.panalbit.panalsuite.model.TicketPriority
import com.panalbit.panalsuite.model.UserPreferences
import com.panalbit.panalsuite.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Estado de la pantalla de Ajustes.
 *
 * Es un AndroidViewModel porque el repositorio necesita un Context para
 * encontrar el archivo en el telefono, y un ViewModel comun no lo tiene.
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserPreferencesRepository(application)

    /** Lo guardado, convertido en un StateFlow que la pantalla puede observar. */
    val preferences: StateFlow<UserPreferences> = repository.userPreferences
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferences())

    // Guardar es una operacion suspend: solo se puede llamar desde una corrutina.
    fun onThemeModeChange(mode: ThemeMode) {
        viewModelScope.launch { repository.setThemeMode(mode) }
    }

    fun onDefaultPriorityChange(priority: TicketPriority) {
        viewModelScope.launch { repository.setDefaultPriority(priority) }
    }

    fun onContactNameChange(name: String) {
        viewModelScope.launch { repository.saveContactData(name, currentEmail()) }
    }

    fun onContactEmailChange(email: String) {
        viewModelScope.launch { repository.saveContactData(currentName(), email) }
    }

    fun clearSavedData() {
        viewModelScope.launch { repository.clearSavedData() }
    }

    private fun currentName(): String = preferences.value.contactName

    private fun currentEmail(): String = preferences.value.contactEmail
}
