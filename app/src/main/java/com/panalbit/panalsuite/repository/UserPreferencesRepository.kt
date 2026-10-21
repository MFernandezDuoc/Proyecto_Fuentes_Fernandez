package com.panalbit.panalsuite.repository

/**
 * El archivo de preferencias del teléfono.
 *
 * Se declara fuera de la clase a propósito: DataStore permite una sola instancia
 * por archivo, y así todas las pantallas comparten la misma.
 */

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.panalbit.panalsuite.model.ThemeMode
import com.panalbit.panalsuite.model.TicketPriority
import com.panalbit.panalsuite.model.UserPreferences
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.userPreferencesDataStore: DataStore<Preferences> by
        preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(context: Context) {

    private val appContext = context.applicationContext

    private object Keys {
        val CONTACT_NAME = stringPreferencesKey("contact_name")
        val CONTACT_EMAIL = stringPreferencesKey("contact_email")
        val LAST_USERNAME = stringPreferencesKey("last_username")
        val DEFAULT_PRIORITY = stringPreferencesKey("default_priority")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val userPreferences: Flow<UserPreferences> = appContext.userPreferencesDataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { preferences ->
            UserPreferences(
                contactName = preferences[Keys.CONTACT_NAME].orEmpty(),
                contactEmail = preferences[Keys.CONTACT_EMAIL].orEmpty(),
                lastUsername = preferences[Keys.LAST_USERNAME].orEmpty(),
                defaultPriority = TicketPriority.fromName(preferences[Keys.DEFAULT_PRIORITY]),
                themeMode = ThemeMode.fromName(preferences[Keys.THEME_MODE])
            )
        }

    suspend fun saveContactData(name: String, email: String) {
        appContext.userPreferencesDataStore.edit { preferences ->
            preferences[Keys.CONTACT_NAME] = name.trim()
            preferences[Keys.CONTACT_EMAIL] = email.trim()
        }
    }

    suspend fun saveLastUsername(username: String) {
        appContext.userPreferencesDataStore.edit { preferences ->
            preferences[Keys.LAST_USERNAME] = username.trim()
        }
    }

    suspend fun setDefaultPriority(priority: TicketPriority) {
        appContext.userPreferencesDataStore.edit { preferences ->
            preferences[Keys.DEFAULT_PRIORITY] = priority.name
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        appContext.userPreferencesDataStore.edit { preferences ->
            preferences[Keys.THEME_MODE] = mode.name
        }
    }

    suspend fun clearSavedData() {
        appContext.userPreferencesDataStore.edit { preferences ->
            preferences.remove(Keys.CONTACT_NAME)
            preferences.remove(Keys.CONTACT_EMAIL)
            preferences.remove(Keys.LAST_USERNAME)
        }
    }
}
