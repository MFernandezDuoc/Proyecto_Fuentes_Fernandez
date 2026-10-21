package com.panalbit.panalsuite

/**
 * Punto de entrada de la aplicación.
 *
 * Su única tarea es preparar el almacenamiento local antes de que se cree
 * cualquier pantalla. Android llama a onCreate() una sola vez, al arrancar la app.
 */

import android.app.Application
import com.panalbit.panalsuite.repository.LocalStore

class PanalSuiteApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        LocalStore.init(this)
    }
}
