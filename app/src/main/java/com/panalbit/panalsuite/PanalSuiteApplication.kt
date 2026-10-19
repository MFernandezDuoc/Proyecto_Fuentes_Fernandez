package com.panalbit.panalsuite

import android.app.Application
import com.panalbit.panalsuite.repository.LocalStore

/**
 * Punto de entrada de la aplicacion.
 *
 * Su unica tarea es preparar el almacenamiento local antes de que se cree
 * cualquier pantalla. Android llama a onCreate() una sola vez, al arrancar la app.
 */
class PanalSuiteApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        LocalStore.init(this)
    }
}
