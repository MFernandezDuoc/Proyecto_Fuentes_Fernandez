package com.panalbit.panalsuite.presentation.inbox

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.panalbit.panalsuite.domain.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    onTicketClick: (String) -> Unit,
    viewModel: InboxViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bandeja de Tickets") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                label = { Text("Buscar ticket o categoría") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedStatus == null,
                        onClick = { viewModel.onStatusFilterSelect(null) },
                        label = { Text("Todos") }
                    )
                }
                items(TicketStatus.values()) { status ->
                    FilterChip(
                        selected = uiState.selectedStatus == status,
                        onClick = { viewModel.onStatusFilterSelect(status) },
                        label = { Text(status.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.tickets.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay tickets disponibles")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.tickets, key = { it.id }) { ticket ->
                        TicketCard(ticket = ticket, onClick = { onTicketClick(ticket.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun TicketCard(
    ticket: Ticket,
    onClick: () -> Unit
) {
    val priorityColor = when (ticket.priority) {
        TicketPriority.CRITICAL -> Color(0xFFD32F2F)
        TicketPriority.HIGH -> Color(0xFFF57C00)
        TicketPriority.MEDIUM -> Color(0xFFFBC02D)
        TicketPriority.LOW -> Color(0xFF388E3C)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ticket.id,
                    style = MaterialTheme.typography.titleMedium
                )
                AssistChip(
                    onClick = {},
                    label = { Text(ticket.priority.name, color = Color.White) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = priorityColor)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${ticket.type} · ${ticket.category}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Estado: ${ticket.status.name}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = ticket.updatedAt,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
