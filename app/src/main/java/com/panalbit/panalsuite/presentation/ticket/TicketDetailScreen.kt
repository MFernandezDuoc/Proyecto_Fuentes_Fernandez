package com.panalbit.panalsuite.presentation.ticket

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.panalbit.panalsuite.domain.model.TicketStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(
    onBack: () -> Unit,
    viewModel: TicketDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val ticket = uiState.ticket

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(ticket?.id ?: "Detalle de Ticket") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Atrás") }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (ticket != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Categoría: ${ticket.category}", style = MaterialTheme.typography.titleMedium)
                        Text("Tipo: ${ticket.type}")
                        Text("Prioridad: ${ticket.priority.name}")
                        Text("Estado actual: ${ticket.status.name}")
                        Text("Sucursal: ${ticket.branch}")
                        Text("Asignado a: ${ticket.assignedTo ?: "Sin asignar"}")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Cambiar Estado:", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.changeStatus(TicketStatus.IN_PROGRESS) },
                        enabled = ticket.status != TicketStatus.IN_PROGRESS
                    ) {
                        Text("En Progreso")
                    }
                    Button(
                        onClick = { viewModel.changeStatus(TicketStatus.RESOLVED) },
                        enabled = ticket.status != TicketStatus.RESOLVED
                    ) {
                        Text("Resolver")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Comentarios", style = MaterialTheme.typography.titleMedium)

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ticket.comments) { comment ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("${comment.authorName} (${comment.createdAt}):", style = MaterialTheme.typography.labelMedium)
                                Text(comment.content, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.newCommentText,
                        onValueChange = viewModel::onCommentChange,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Escribe un comentario...") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = viewModel::sendComment) {
                        Text("Enviar")
                    }
                }
            }
        }
    }
}
