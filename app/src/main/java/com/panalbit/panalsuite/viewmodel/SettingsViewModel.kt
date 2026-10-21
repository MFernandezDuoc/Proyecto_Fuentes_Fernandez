package com.panalbit.panalsuite.viewmodel

/**
 * Estado de la pantalla de Ajustes.
 *
 * Es un AndroidViewModel porque el repositorio necesita un Context para
 * encontrar el archivo en el teléfono, y un ViewModel común no lo tiene.
 */

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

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserPreferencesRepository(application)

    val preferences: StateFlow<UserPreferences> = repository.userPreferences
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferences())

    fun onThemeModeChange(mode: ThemeMode) {
        viewModelScope.launch { repository.setThemeMode(mode) }
    }

    fun onDefaultPriorityChange(priority: TicketPriority) {
        viewModelScope.launch { repository.setDefaultPriority(priority) }
    }

    fun onContactDataChange(name: String, email: String) {
        viewModelScope.launch { repository.saveContactData(name, email) }
    }

    fun clearSavedData() {
        viewModelScope.launch { repository.clearSavedData() }
    }
}
