package com.panalbit.panalsuite.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.panalbit.panalsuite.model.AuthSession
import com.panalbit.panalsuite.model.UserRole
import com.panalbit.panalsuite.ui.AdminScreen
import com.panalbit.panalsuite.ui.DashboardScreen
import com.panalbit.panalsuite.ui.InboxScreen
import com.panalbit.panalsuite.ui.LoginScreen
import com.panalbit.panalsuite.ui.NewTicketScreen
import com.panalbit.panalsuite.ui.ProfileScreen
import com.panalbit.panalsuite.ui.SettingsScreen
import com.panalbit.panalsuite.ui.TicketDetailScreen
import com.panalbit.panalsuite.viewmodel.AdminViewModel
import com.panalbit.panalsuite.viewmodel.DashboardViewModel
import com.panalbit.panalsuite.viewmodel.InboxViewModel
import com.panalbit.panalsuite.viewmodel.LoginViewModel
import com.panalbit.panalsuite.viewmodel.NewTicketViewModel
import com.panalbit.panalsuite.viewmodel.ProfileViewModel
import com.panalbit.panalsuite.viewmodel.SettingsViewModel
import com.panalbit.panalsuite.viewmodel.TicketDetailViewModel

/** Una opcion de la barra inferior. */
private data class BottomItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

/**
 * Arma la barra inferior segun el rol.
 *
 * Esta es la parte visible del control de acceso: el solicitante no ve
 * indicadores ni administracion, y el resolutor no ve ninguna de las dos.
 */
private fun bottomItemsFor(role: UserRole?): List<BottomItem> {
    if (role == null) return emptyList()

    val items = mutableListOf(
        BottomItem(Routes.INBOX, "Bandeja", Icons.Default.Home)
    )

    if (role.canCreateTicket) {
        items += BottomItem(Routes.NEW_TICKET, "Nueva", Icons.Default.Add)
    }

    if (role.canSeeDashboard) {
        items += BottomItem(Routes.DASHBOARD, "Indicadores", Icons.Default.Assessment)
    }

    if (role.canManageUsers) {
        items += BottomItem(Routes.ADMIN, "Usuarios", Icons.Default.Group)
    }

    items += BottomItem(Routes.PROFILE, "Perfil", Icons.Default.Person)

    return items
}

/**
 * Grafo de navegacion de la aplicacion.
 *
 * Es el unico archivo que sabe que pantallas existen y como se conectan.
 * Las pantallas no reciben el NavController: reciben funciones.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    session: AuthSession?,
    startDestination: String,
    loginViewModel: LoginViewModel,
    inboxViewModel: InboxViewModel,
    newTicketViewModel: NewTicketViewModel,
    ticketDetailViewModel: TicketDetailViewModel,
    dashboardViewModel: DashboardViewModel,
    adminViewModel: AdminViewModel,
    profileViewModel: ProfileViewModel,
    settingsViewModel: SettingsViewModel,
    onLoginSuccess: (AuthSession) -> Unit,
    onLogout: () -> Unit
) {

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val bottomItems = bottomItemsFor(session?.role)
    val showBottomBar = bottomItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = { navController.navigateSingleTopTo(item.route) },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {

            // NODO 1: acceso
            composable(Routes.LOGIN) {
                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = onLoginSuccess
                )
            }

            // NODO 2: bandeja de trabajo
            composable(Routes.INBOX) {
                InboxScreen(
                    viewModel = inboxViewModel,
                    onTicketClick = { ticketId ->
                        ticketDetailViewModel.openTicket(ticketId)
                        navController.navigate(Routes.ticketDetail(ticketId))
                    },
                    onNewTicketClick = { navController.navigate(Routes.NEW_TICKET) }
                )
            }

            // NODO 3: detalle del ticket, con argumento en la ruta
            composable(
                route = Routes.TICKET_DETAIL,
                arguments = listOf(navArgument("ticketId") { type = NavType.StringType })
            ) {
                TicketDetailScreen(
                    viewModel = ticketDetailViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // NODO 4: formulario de nueva solicitud
            composable(Routes.NEW_TICKET) {
                NewTicketScreen(
                    viewModel = newTicketViewModel,
                    onBack = { navController.popBackStack() },
                    onCreated = { ticketId ->
                        newTicketViewModel.reset()
                        ticketDetailViewModel.openTicket(ticketId)
                        navController.navigate(Routes.ticketDetail(ticketId)) {
                            // Saca el formulario de la pila: Atras no vuelve a el.
                            popUpTo(Routes.INBOX)
                        }
                    }
                )
            }

            // NODO 5: indicadores
            composable(Routes.DASHBOARD) {
                DashboardScreen(viewModel = dashboardViewModel)
            }

            // NODO 6: administracion de usuarios
            composable(Routes.ADMIN) {
                AdminScreen(viewModel = adminViewModel)
            }

            // NODO 7: perfil
            composable(Routes.PROFILE) {
                val preferences by settingsViewModel.preferences.collectAsState()
                ProfileScreen(
                    viewModel = profileViewModel,
                    preferences = preferences,
                    onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                    onLoggedOut = onLogout
                )
            }

            // NODO 8: ajustes
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

/**
 * Navega sin apilar copias de la misma pantalla.
 * Se usa solo para la barra inferior, que es un cambio de seccion, no un avance.
 */
private fun NavHostController.navigateSingleTopTo(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
