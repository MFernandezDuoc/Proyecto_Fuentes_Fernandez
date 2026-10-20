package com.panalbit.panalsuite.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.panalbit.panalsuite.model.AuthSession
import com.panalbit.panalsuite.model.UserRole
import com.panalbit.panalsuite.model.UserPreferences
import com.panalbit.panalsuite.ui.components.LabeledValue
import com.panalbit.panalsuite.ui.components.RoleChip
import com.panalbit.panalsuite.ui.components.SectionTitle
import com.panalbit.panalsuite.ui.components.roleColor
import com.panalbit.panalsuite.ui.theme.PanalSuiteTheme
import com.panalbit.panalsuite.viewmodel.ProfileUiState
import com.panalbit.panalsuite.viewmodel.ProfileViewModel

/**
 * Perfil del usuario conectado. Muestra tambien cuanto hay guardado en el
 * almacenamiento local del telefono, para comprobar que la persistencia funciona.
 */
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    preferences: UserPreferences,
    onSettingsClick: () -> Unit,
    onLoggedOut: () -> Unit
) {

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    ProfileContent(
        state = state,
        preferences = preferences,
        onSettingsClick = onSettingsClick,
        onLogout = {
            viewModel.logout()
            onLoggedOut()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    state: ProfileUiState,
    preferences: UserPreferences,
    onSettingsClick: () -> Unit,
    onLogout: () -> Unit
) {

    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = state.fullName,
                                style = MaterialTheme.typography.titleLarge
                            )

                            if (state.session != null) {
                                RoleChip(
                                    text = state.roleLabel,
                                    color = roleColor(state.session.role)
                                )
                            }
                        }
                    }

                    LabeledValue("Nombre de acceso", state.username)

                    if (preferences.hasContactData) {
                        LabeledValue(
                            "Contacto guardado",
                            "${preferences.contactName}\n${preferences.contactEmail}"
                        )
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        SectionTitle("Almacenamiento local")
                    }

                    Text(
                        text = state.storageSummary,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = "Todo se guarda en el telefono con SharedPreferences, " +
                                "sobrevive al cierre de la aplicacion y no necesita internet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.size(4.dp))

            OutlinedButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar sesion")
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar sesion") },
            text = { Text("Se borra la sesion guardada. Los tickets y usuarios de prueba siguen en el telefono.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Cerrar sesion")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileContentPreview() {
    PanalSuiteTheme {
        ProfileContent(
            state = ProfileUiState(
                session = AuthSession(
                    userId = "U-03",
                    username = "supervisor",
                    fullName = "Paula Rivas",
                    role = UserRole.SUPERVISOR,
                    token = "demo"
                ),
                savedTickets = 10,
                savedUsers = 5,
                savedComments = 4
            ),
            preferences = UserPreferences(
                contactName = "Camila Rojas",
                contactEmail = "camila@panalbit.cl"
            ),
            onSettingsClick = {},
            onLogout = {}
        )
    }
}
