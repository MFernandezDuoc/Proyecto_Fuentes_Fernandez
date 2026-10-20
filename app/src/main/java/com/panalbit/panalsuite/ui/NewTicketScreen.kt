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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.panalbit.panalsuite.model.TicketCatalog
import com.panalbit.panalsuite.model.TicketPriority
import com.panalbit.panalsuite.ui.components.DropdownField
import com.panalbit.panalsuite.ui.components.ErrorBanner
import com.panalbit.panalsuite.ui.components.SelectableChip
import com.panalbit.panalsuite.ui.theme.PanalSuiteTheme
import com.panalbit.panalsuite.viewmodel.NewTicketUiState
import com.panalbit.panalsuite.viewmodel.NewTicketViewModel

/**
 * Formulario de nueva solicitud.
 *
 * Es el formulario mas importante de la aplicacion: valida el titulo, el detalle
 * y obliga a elegir tipo, categoria, prioridad y sucursal antes de enviar.
 */
@Composable
fun NewTicketScreen(
    viewModel: NewTicketViewModel,
    onBack: () -> Unit,
    onCreated: (String) -> Unit
) {

    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Cuando la solicitud queda creada, la pantalla avisa hacia arriba y limpia la marca.
    LaunchedEffect(state.createdTicketId) {
        val ticketId = state.createdTicketId
        if (ticketId != null) {
            onCreated(ticketId)
            viewModel.onCreatedHandled()
        }
    }

    NewTicketContent(
        state = state,
        snackbarHostState = snackbarHostState,
        types = viewModel.types(),
        categories = viewModel.categories(),
        branches = viewModel.branches(),
        onTitleChange = { viewModel.onTitleChange(it) },
        onDescriptionChange = { viewModel.onDescriptionChange(it) },
        onTypeChange = { viewModel.onTypeChange(it) },
        onCategoryChange = { viewModel.onCategoryChange(it) },
        onPriorityChange = { viewModel.onPriorityChange(it) },
        onBranchChange = { viewModel.onBranchChange(it) },
        onSubmit = { viewModel.submit() },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTicketContent(
    state: NewTicketUiState,
    snackbarHostState: SnackbarHostState,
    types: List<String>,
    categories: List<String>,
    branches: List<String>,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onPriorityChange: (TicketPriority) -> Unit,
    onBranchChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva solicitud") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            ErrorBanner(message = state.errorMessage.orEmpty())

            Text(
                text = "Describe el problema o requerimiento. Los campos marcados se validan antes de enviar.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = state.title,
                onValueChange = onTitleChange,
                label = { Text("Título de la solicitud") },
                singleLine = true,
                isError = state.showErrors && state.titleError != null,
                supportingText = {
                    val error = state.titleError
                    if (state.showErrors && error != null) {
                        Text(error)
                    } else {
                        Text("Un resumen corto del problema")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = onDescriptionChange,
                label = { Text("Detalle") },
                minLines = 4,
                isError = state.showErrors && state.descriptionError != null,
                supportingText = {
                    val error = state.descriptionError
                    if (state.showErrors && error != null) {
                        Text(error)
                    } else {
                        Text(state.descriptionCounter)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            DropdownField(
                label = "Tipo de flujo",
                value = state.type,
                options = types,
                onOptionSelected = onTypeChange,
                modifier = Modifier.fillMaxWidth()
            )

            DropdownField(
                label = "Categoría",
                value = state.category,
                options = categories,
                onOptionSelected = onCategoryChange,
                modifier = Modifier.fillMaxWidth()
            )

            DropdownField(
                label = "Sucursal o ubicación",
                value = state.branch,
                options = branches,
                onOptionSelected = onBranchChange,
                modifier = Modifier.fillMaxWidth()
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                Text(
                    text = "Prioridad",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TicketPriority.entries.forEach { priority ->
                        SelectableChip(
                            text = priority.label,
                            selected = state.priority == priority,
                            onClick = { onPriorityChange(priority) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.size(4.dp))

            Button(
                onClick = onSubmit,
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text("Registrar solicitud")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NewTicketContentPreview() {
    PanalSuiteTheme {
        NewTicketContent(
            state = NewTicketUiState(
                title = "Falla",
                description = "Corta",
                showErrors = true
            ),
            snackbarHostState = SnackbarHostState(),
            types = TicketCatalog.types,
            categories = TicketCatalog.categories,
            branches = TicketCatalog.branches,
            onTitleChange = {},
            onDescriptionChange = {},
            onTypeChange = {},
            onCategoryChange = {},
            onPriorityChange = {},
            onBranchChange = {},
            onSubmit = {},
            onBack = {}
        )
    }
}
