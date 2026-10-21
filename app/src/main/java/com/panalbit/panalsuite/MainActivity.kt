package com.panalbit.panalsuite

/**
 * Punto de entrada de la aplicación.
 *
 * Es la única Activity: en Compose todas las pantallas son funciones, no Activities.
 * Crea los ViewModel una sola vez, así el estado sobrevive al giro del teléfono, y
 * entrega el control a Compose. Agregar pantallas nuevas no toca este archivo:
 * se agregan nodos al grafo de navegación.
 */

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
            val preferences by settingsViewModel.preferences.collectAsState()

            PanalSuiteTheme(themeMode = preferences.themeMode) {

                var session by remember { mutableStateOf(PanalSuiteRepository.currentSession()) }

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
