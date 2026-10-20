package com.panalbit.panalsuite.repository

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

/**
 * El archivo de preferencias del telefono.
 *
 * Se declara fuera de la clase a proposito: DataStore permite una sola
 * instancia por archivo, y asi todas las pantallas comparten la misma.
 */
private val Context.userPreferencesDataStore: DataStore<Preferences> by
        preferencesDataStore(name = "user_preferences")

/**
 * Donde se guardan las preferencias de la aplicacion.
 *
 * A diferencia del resto de los datos, aqui no hay una lista de tickets: son
 * valores sueltos y pequenos (un nombre, un correo, un modo de color), que es
 * justo para lo que sirve DataStore.
 */
class UserPreferencesRepository(context: Context) {

    private val appContext = context.applicationContext

    /** Cada dato se guarda bajo su propia clave, como la etiqueta de un cajon. */
    private object Keys {
        val CONTACT_NAME = stringPreferencesKey("contact_name")
        val CONTACT_EMAIL = stringPreferencesKey("contact_email")
        val LAST_USERNAME = stringPreferencesKey("last_username")
        val DEFAULT_PRIORITY = stringPreferencesKey("default_priority")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    /**
     * Lo guardado, entregado como un Flow: entrega lo que hay ahora y vuelve a
     * entregar cada vez que algo cambia. Quien lo recoge no tiene que preguntar.
     */
    val userPreferences: Flow<UserPreferences> = appContext.userPreferencesDataStore.data
        .catch { error ->
            // Si el archivo no se pudo leer, partimos como si estuviera vacio.
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

    /** Guarda los datos de contacto del usuario. */
    suspend fun saveContactData(name: String, email: String) {
        appContext.userPreferencesDataStore.edit { preferences ->
            preferences[Keys.CONTACT_NAME] = name.trim()
            preferences[Keys.CONTACT_EMAIL] = email.trim()
        }
    }

    /** Recuerda el ultimo nombre de acceso usado, para completar el formulario. */
    suspend fun saveLastUsername(username: String) {
        appContext.userPreferencesDataStore.edit { preferences ->
            preferences[Keys.LAST_USERNAME] = username.trim()
        }
    }

    /** La prioridad con la que partiran las solicitudes nuevas. */
    suspend fun setDefaultPriority(priority: TicketPriority) {
        appContext.userPreferencesDataStore.edit { preferences ->
            preferences[Keys.DEFAULT_PRIORITY] = priority.name
        }
    }

    /** El modo de color de la aplicacion. */
    suspend fun setThemeMode(mode: ThemeMode) {
        appContext.userPreferencesDataStore.edit { preferences ->
            preferences[Keys.THEME_MODE] = mode.name
        }
    }

    /**
     * Olvida los datos guardados del usuario.
     * La apariencia y la prioridad por defecto no se tocan.
     */
    suspend fun clearSavedData() {
        appContext.userPreferencesDataStore.edit { preferences ->
            preferences.remove(Keys.CONTACT_NAME)
            preferences.remove(Keys.CONTACT_EMAIL)
            preferences.remove(Keys.LAST_USERNAME)
        }
    }
}
