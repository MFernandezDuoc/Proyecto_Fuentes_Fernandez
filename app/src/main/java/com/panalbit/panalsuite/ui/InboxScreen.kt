package com.panalbit.panalsuite.ui

/**
 * Bandeja de trabajo.
 *
 * Conoce al ViewModel y le pasa el estado a la parte visual.
 */

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.panalbit.panalsuite.model.Ticket
import com.panalbit.panalsuite.model.TicketFilter
import com.panalbit.panalsuite.model.TicketPriority
import com.panalbit.panalsuite.model.TicketStatus
import com.panalbit.panalsuite.model.UserRole
import com.panalbit.panalsuite.ui.components.EmptyState
import com.panalbit.panalsuite.ui.components.ErrorBanner
import com.panalbit.panalsuite.ui.components.LoadingState
import com.panalbit.panalsuite.ui.components.SelectableChip
import com.panalbit.panalsuite.ui.components.TicketCard
import com.panalbit.panalsuite.ui.components.priorityChipColors
import com.panalbit.panalsuite.ui.theme.PanalSuiteTheme
import com.panalbit.panalsuite.viewmodel.InboxUiState
import com.panalbit.panalsuite.viewmodel.InboxViewModel

@Composable
fun InboxScreen(
    viewModel: InboxViewModel,
    onTicketClick: (String) -> Unit,
    onNewTicketClick: () -> Unit
) {

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    InboxContent(
        state = state,
        onSearchChange = { viewModel.onSearchChange(it) },
        onStatusToggle = { viewModel.toggleStatusFilter(it) },
        onPriorityToggle = { viewModel.togglePriorityFilter(it) },
        onClearFilters = { viewModel.clearFilters() },
        onRefresh = { viewModel.refresh() },
        onDismissError = { viewModel.dismissError() },
        onTicketClick = onTicketClick,
        onNewTicketClick = onNewTicketClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxContent(
    state: InboxUiState,
    onSearchChange: (String) -> Unit,
    onStatusToggle: (TicketStatus) -> Unit,
    onPriorityToggle: (TicketPriority) -> Unit,
    onClearFilters: () -> Unit,
    onRefresh: () -> Unit,
    onDismissError: () -> Unit,
    onTicketClick: (String) -> Unit,
    onNewTicketClick: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Bandeja de trabajo",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (state.currentUserName.isNotBlank()) {
                            Text(
                                text = "${state.currentUserRole.label} - ${state.currentUserName}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar bandeja"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (state.currentUserRole.canCreateTicket) {
                FloatingActionButton(onClick = onNewTicketClick) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva solicitud")
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                ErrorBanner(
                    message = state.errorMessage.orEmpty(),
                    onRetry = onRefresh,
                    onDismiss = onDismissError
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MiniStat(
                        label = "Asignados",
                        value = state.totalForRole.toString(),
                        accent = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    MiniStat(
                        label = "Abiertos",
                        value = state.openCount.toString(),
                        accent = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f)
                    )
                    MiniStat(
                        label = "En curso",
                        value = state.inProgressCount.toString(),
                        accent = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = state.filter.searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text(InboxUiState.SEARCH_HINT, maxLines = 1) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (state.filter.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar busqueda")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                FilterRow(
                    title = "Estado",
                    selected = state.filter,
                    onStatusToggle = onStatusToggle,
                    onPriorityToggle = onPriorityToggle
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${state.visibleTickets.size} de ${state.totalForRole} ticket(s)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (state.filter.activeCount > 0) {
                        TextButton(onClick = onClearFilters) {
                            Text("Limpiar filtros")
                        }
                    }
                }
            }

            when {
                state.isLoading && state.tickets.isEmpty() -> {
                    LoadingState(message = "Cargando bandeja...")
                }

                state.isListEmpty -> {
                    EmptyState(
                        icon = Icons.Default.Inbox,
                        title = "Sin resultados",
                        message = state.emptyMessage
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 96.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.visibleTickets, key = { it.id }) { ticket ->
                            TicketCard(
                                ticket = ticket,
                                onClick = { onTicketClick(ticket.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterRow(
    title: String,
    selected: TicketFilter,
    onStatusToggle: (TicketStatus) -> Unit,
    onPriorityToggle: (TicketPriority) -> Unit
) {

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            TicketStatus.entries.forEach { status ->
                SelectableChip(
                    text = status.label,
                    selected = selected.status == status,
                    onClick = { onStatusToggle(status) }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Prioridad",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            TicketPriority.entries.forEach { priority ->
                SelectableChip(
                    text = priority.label,
                    selected = selected.priority == priority,
                    onClick = { onPriorityToggle(priority) },
                    dotColor = priorityChipColors(priority).content
                )
            }
        }
    }
}

@Composable
private fun MiniStat(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = accent.copy(alpha = 0.12f),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = accent
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InboxContentPreview() {
    val now = System.currentTimeMillis()

    val sample = listOf(
        Ticket(
            id = "C-1002",
            title = "Error al imprimir boleta",
            type = "Software",
            category = "Punto de venta",
            priority = TicketPriority.HIGH,
            status = TicketStatus.IN_PROGRESS,
            branch = "Sucursal Providencia",
            requesterName = "Carolina Soto",
            resolverTeam = "Soporte Aplicaciones",
            assignedTo = "Diego Munoz",
            description = "Error de formato al imprimir.",
            createdAt = now,
            updatedAt = now
        ),
        Ticket(
            id = "C-1009",
            title = "Caida de servicio de correo",
            type = "Infraestructura",
            category = "Servidores",
            priority = TicketPriority.CRITICAL,
            status = TicketStatus.RESOLVED,
            branch = "Casa matriz",
            requesterName = "Carolina Soto",
            resolverTeam = "Infraestructura",
            assignedTo = "Diego Munoz",
            description = "El servicio dejo de responder.",
            createdAt = now,
            updatedAt = now
        )
    )

    PanalSuiteTheme {
        InboxContent(
            state = InboxUiState(
                tickets = sample,
                currentUserName = "Diego Munoz",
                currentUserRole = UserRole.RESOLUTOR
            ),
            onSearchChange = {},
            onStatusToggle = {},
            onPriorityToggle = {},
            onClearFilters = {},
            onRefresh = {},
            onDismissError = {},
            onTicketClick = {},
            onNewTicketClick = {}
        )
    }
}
