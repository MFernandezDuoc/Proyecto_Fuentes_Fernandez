package com.panalbit.panalsuite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.panalbit.panalsuite.repository.PanalSuiteRepository
import com.panalbit.panalsuite.ui.navigation.AppNavHost
import com.panalbit.panalsuite.ui.navigation.Routes
import com.panalbit.panalsuite.ui.theme.PanalSuiteTheme
import com.panalbit.panalsuite.viewmodel.AdminViewModel
import com.panalbit.panalsuite.viewmodel.DashboardViewModel
import com.panalbit.panalsuite.viewmodel.InboxViewModel
import com.panalbit.panalsuite.viewmodel.LoginViewModel
import com.panalbit.panalsuite.viewmodel.NewTicketViewModel
import com.panalbit.panalsuite.viewmodel.ProfileViewModel
import com.panalbit.panalsuite.viewmodel.SettingsViewModel
import com.panalbit.panalsuite.viewmodel.TicketDetailViewModel

/**
 * Unica Activity de la aplicacion.
 *
 * Crea los ViewModels una sola vez, asi el estado sobrevive al giro del telefono,
 * y entrega el control a Compose. Agregar pantallas nuevas no toca este archivo:
 * se agregan nodos al grafo de navegacion.
 */
class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private val inboxViewModel: InboxViewModel by viewModels()
    private val newTicketViewModel: NewTicketViewModel by viewModels()
    private val ticketDetailViewModel: TicketDetailViewModel by viewModels()
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val adminViewModel: AdminViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // El tema depende de lo guardado: si el usuario cambia el modo de
            // color en Ajustes, toda la app cambia al instante.
            val preferences by settingsViewModel.preferences.collectAsState()

            PanalSuiteTheme(themeMode = preferences.themeMode) {

                // La sesion vive en el almacenamiento local, por eso al rotar el
                // telefono se recupera sola y no hay que volver a iniciar sesion.
                var session by remember { mutableStateOf(PanalSuiteRepository.currentSession()) }

                // Se decide una sola vez por composicion, para que el grafo no se
                // reinicie cada vez que cambia la sesion.
                val startDestination = remember {
                    if (PanalSuiteRepository.currentSession() == null) Routes.LOGIN else Routes.INBOX
                }

                val navController = rememberNavController()

                AppNavHost(
                    navController = navController,
                    session = session,
                    startDestination = startDestination,
                    loginViewModel = loginViewModel,
                    inboxViewModel = inboxViewModel,
                    newTicketViewModel = newTicketViewModel,
                    ticketDetailViewModel = ticketDetailViewModel,
                    dashboardViewModel = dashboardViewModel,
                    adminViewModel = adminViewModel,
                    profileViewModel = profileViewModel,
                    settingsViewModel = settingsViewModel,
                    onLoginSuccess = { newSession ->
                        session = newSession
                        navController.navigate(Routes.INBOX) {
                            // El acceso sale de la pila: Atras no devuelve al login.
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onLogout = {
                        loginViewModel.reset()
                        session = null
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(navController.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
