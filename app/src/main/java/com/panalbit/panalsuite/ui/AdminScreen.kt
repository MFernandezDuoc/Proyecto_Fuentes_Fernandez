package com.panalbit.panalsuite.ui

/**
 * Administración de los usuarios de prueba.
 *
 * Solo el rol Administrador llega acá, porque el menú inferior esconde la
 * opción para los demás roles.
 */

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.panalbit.panalsuite.model.User
import com.panalbit.panalsuite.model.UserRole
import com.panalbit.panalsuite.ui.components.EmptyState
import com.panalbit.panalsuite.ui.components.ErrorBanner
import com.panalbit.panalsuite.ui.components.LoadingState
import com.panalbit.panalsuite.ui.components.RoleChip
import com.panalbit.panalsuite.ui.components.SelectableChip
import com.panalbit.panalsuite.ui.components.roleColor
import com.panalbit.panalsuite.ui.theme.PanalSuiteTheme
import com.panalbit.panalsuite.viewmodel.AdminUiState
import com.panalbit.panalsuite.viewmodel.AdminViewModel

@Composable
fun AdminScreen(viewModel: AdminViewModel) {

    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    val feedback = state.feedbackMessage
    LaunchedEffect(feedback) {
        if (feedback != null) {
            snackbarHostState.showSnackbar(feedback)
            viewModel.onFeedbackShown()
        }
    }

    AdminContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onRefresh = { viewModel.refresh() },
        onShowForm = { viewModel.showForm() },
        onHideForm = { viewModel.hideForm() },
        onFullNameChange = { viewModel.onFullNameChange(it) },
        onUsernameChange = { viewModel.onUsernameChange(it) },
        onEmailChange = { viewModel.onEmailChange(it) },
        onRoleChange = { viewModel.onRoleChange(it) },
        onSubmit = { viewModel.submit() },
        onToggleActive = { viewModel.toggleUserActive(it) },
        onDismissError = { viewModel.dismissError() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminContent(
    state: AdminUiState,
    snackbarHostState: SnackbarHostState,
    onRefresh: () -> Unit,
    onShowForm: () -> Unit,
    onHideForm: () -> Unit,
    onFullNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onRoleChange: (UserRole) -> Unit,
    onSubmit: () -> Unit,
    onToggleActive: (User) -> Unit,
    onDismissError: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Usuarios de prueba") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onShowForm) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo usuario")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            ErrorBanner(
                message = state.errorMessage.orEmpty(),
                onRetry = onRefresh,
                onDismiss = onDismissError
            )

            Text(
                text = "${state.activeCount} activo(s) - ${state.inactiveCount} desactivado(s)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            when {
                state.isLoading && state.users.isEmpty() -> {
                    LoadingState(message = "Cargando usuarios...")
                }

                state.users.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Default.Group,
                        title = "Sin usuarios",
                        message = "Todavia no hay usuarios de prueba registrados"
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.users, key = { it.id }) { user ->
                            UserCard(
                                user = user,
                                onToggleActive = { onToggleActive(user) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (state.showForm) {
        AlertDialog(
            onDismissRequest = onHideForm,
            title = { Text("Nuevo usuario de prueba") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    OutlinedTextField(
                        value = state.fullName,
                        onValueChange = onFullNameChange,
                        label = { Text("Nombre completo") },
                        singleLine = true,
                        isError = state.showErrors && state.fullNameError != null,
                        supportingText = {
                            val error = state.fullNameError
                            if (state.showErrors && error != null) {
                                Text(error)
                            } else {
                                Text("Cómo aparece en la trazabilidad")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = state.username,
                        onValueChange = onUsernameChange,
                        label = { Text("Nombre de acceso") },
                        singleLine = true,
                        isError = state.showErrors && state.usernameError != null,
                        supportingText = {
                            val error = state.usernameError
                            if (state.showErrors && error != null) {
                                Text(error)
                            } else {
                                Text("Sin espacios")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = state.email,
                        onValueChange = onEmailChange,
                        label = { Text("Correo") },
                        singleLine = true,
                        isError = state.showErrors && state.emailError != null,
                        supportingText = {
                            val error = state.emailError
                            if (state.showErrors && error != null) {
                                Text(error)
                            } else {
                                Text("Ejemplo: nombre@panalbit.cl")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                        Text(
                            text = "Rol",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        UserRole.entries.forEach { role ->
                            SelectableChip(
                                text = role.label,
                                selected = state.role == role,
                                onClick = { onRoleChange(role) },
                                dotColor = roleColor(role)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onSubmit) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = onHideForm) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun UserCard(
    user: User,
    onToggleActive: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = user.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                RoleChip(text = user.role.label, color = roleColor(user.role))
            }

            Text(
                text = "@${user.username} - ${user.email}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (user.isActive) "Activo" else "Desactivado",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (user.isActive) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )

                Spacer(modifier = Modifier.width(4.dp))

                OutlinedButton(onClick = onToggleActive) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (user.isActive) "Desactivar" else "Activar")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AdminContentPreview() {
    PanalSuiteTheme {
        AdminContent(
            state = AdminUiState(
                users = listOf(
                    User("U-02", "resolutor", "Diego Munoz", "diego.munoz@panalbit.cl", UserRole.RESOLUTOR),
                    User("U-04", "gerencia", "Andres Leon", "andres.leon@panalbit.cl", UserRole.GERENCIA, false)
                )
            ),
            snackbarHostState = SnackbarHostState(),
            onRefresh = {},
            onShowForm = {},
            onHideForm = {},
            onFullNameChange = {},
            onUsernameChange = {},
            onEmailChange = {},
            onRoleChange = {},
            onSubmit = {},
            onToggleActive = {},
            onDismissError = {}
        )
    }
}
