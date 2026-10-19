package com.panalbit.panalsuite.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.panalbit.panalsuite.presentation.admin.AdminScreen
import com.panalbit.panalsuite.presentation.auth.LoginScreen
import com.panalbit.panalsuite.presentation.dashboard.DashboardScreen
import com.panalbit.panalsuite.presentation.inbox.InboxScreen
import com.panalbit.panalsuite.presentation.ticket.TicketDetailScreen

object NavRoutes {
    const val LOGIN = "login"
    const val INBOX = "inbox"
    const val TICKET_DETAIL = "ticket/{ticketId}"
    const val DASHBOARD = "dashboard"
    const val ADMIN = "admin"

    fun ticketDetail(id: String) = "ticket/$id"
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        NavRoutes.INBOX,
        NavRoutes.DASHBOARD,
        NavRoutes.ADMIN
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == NavRoutes.INBOX,
                        onClick = { navController.navigateSingleTopTo(NavRoutes.INBOX) },
                        label = { Text("Bandeja") },
                        icon = {}
                    )
                    NavigationBarItem(
                        selected = currentRoute == NavRoutes.DASHBOARD,
                        onClick = { navController.navigateSingleTopTo(NavRoutes.DASHBOARD) },
                        label = { Text("Dashboard") },
                        icon = {}
                    )
                    NavigationBarItem(
                        selected = currentRoute == NavRoutes.ADMIN,
                        onClick = { navController.navigateSingleTopTo(NavRoutes.ADMIN) },
                        label = { Text("Admin") },
                        icon = {}
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoutes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(NavRoutes.INBOX) {
                            popUpTo(NavRoutes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            composable(NavRoutes.INBOX) {
                InboxScreen(
                    onTicketClick = { ticketId ->
                        navController.navigate(NavRoutes.ticketDetail(ticketId))
                    }
                )
            }

            composable(NavRoutes.TICKET_DETAIL) {
                TicketDetailScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NavRoutes.DASHBOARD) {
                DashboardScreen()
            }

            composable(NavRoutes.ADMIN) {
                AdminScreen()
            }
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(this@navigateSingleTopTo.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
