package com.panalbit.panalsuite.ui

/**
 * Detalle de un ticket.
 *
 * Como el ViewModel se crea una sola vez junto con la Activity, la navegación le
 * indica cuál ticket abrir.
 */

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.panalbit.panalsuite.model.ChangeEntry
import com.panalbit.panalsuite.model.Comment
import com.panalbit.panalsuite.model.Evidence
import com.panalbit.panalsuite.model.Ticket
import com.panalbit.panalsuite.model.TicketPriority
import com.panalbit.panalsuite.model.TicketStatus
import com.panalbit.panalsuite.model.UserRole
import com.panalbit.panalsuite.ui.components.EmptyState
import com.panalbit.panalsuite.ui.components.ErrorBanner
import com.panalbit.panalsuite.ui.components.LabeledValue
import com.panalbit.panalsuite.ui.components.LoadingState
import com.panalbit.panalsuite.ui.components.PriorityChip
import com.panalbit.panalsuite.ui.components.SectionTitle
import com.panalbit.panalsuite.ui.components.SelectableChip
import com.panalbit.panalsuite.ui.components.StatusChip
import com.panalbit.panalsuite.ui.components.TagChip
import com.panalbit.panalsuite.ui.components.fullDate
import com.panalbit.panalsuite.ui.components.relativeTime
import com.panalbit.panalsuite.ui.theme.PanalSuiteTheme
import com.panalbit.panalsuite.viewmodel.TicketDetailUiState
import com.panalbit.panalsuite.viewmodel.TicketDetailViewModel

@Composable
fun TicketDetailScreen(
    viewModel: TicketDetailViewModel,
    onBack: () -> Unit
) {

    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = androidx.compose.runtime.remember { SnackbarHostState() }

    val feedback = state.feedbackMessage
    LaunchedEffect(feedback) {
        if (feedback != null) {
            snackbarHostState.showSnackbar(feedback)
            viewModel.onFeedbackShown()
        }
    }

    TicketDetailContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onCommentChange = { viewModel.onCommentChange(it) },
        onSendComment = { viewModel.sendComment() },
        onStatusSelected = { viewModel.onStatusSelected(it) },
        onConfirmStatusChange = { viewModel.confirmStatusChange() },
        onDismissStatusDialog = { viewModel.dismissStatusDialog() },
        onShowEvidenceDialog = { viewModel.showEvidenceDialog() },
        onDismissEvidenceDialog = { viewModel.dismissEvidenceDialog() },
        onEvidenceDescriptionChange = { viewModel.onEvidenceDescriptionChange(it) },
        onEvidenceLocationToggle = { viewModel.onEvidenceLocationToggle(it) },
        onSaveEvidence = { viewModel.saveEvidence() },
        onRetry = { viewModel.load() },
        onDismissError = { viewModel.dismissError() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailContent(
    state: TicketDetailUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onCommentChange: (String) -> Unit,
    onSendComment: () -> Unit,
    onStatusSelected: (TicketStatus) -> Unit,
    onConfirmStatusChange: () -> Unit,
    onDismissStatusDialog: () -> Unit,
    onShowEvidenceDialog: () -> Unit,
    onDismissEvidenceDialog: () -> Unit,
    onEvidenceDescriptionChange: (String) -> Unit,
    onEvidenceLocationToggle: (Boolean) -> Unit,
    onSaveEvidence: () -> Unit,
    onRetry: () -> Unit,
    onDismissError: () -> Unit
) {

    val ticket = state.ticket

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(ticket?.id ?: "Detalle del ticket") },
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

        when {
            state.isLoading && ticket == null -> {
                LoadingState(
                    message = "Cargando ticket...",
                    modifier = Modifier.padding(padding)
                )
            }

            ticket == null -> {
                EmptyState(
                    icon = Icons.Default.SearchOff,
                    title = "Ticket no disponible",
                    message = state.errorMessage ?: "No se encontró el ticket solicitado",
                    modifier = Modifier.padding(padding)
                )
            }

            else -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                        .animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    ErrorBanner(
                        message = state.errorMessage.orEmpty(),
                        onRetry = onRetry,
                        onDismiss = onDismissError
                    )

                    TicketHeader(ticket)

                    HorizontalDivider()

                    DataSection(ticket)

                    HorizontalDivider()

                    StatusSection(
                        state = state,
                        onStatusSelected = onStatusSelected
                    )

                    HorizontalDivider()

                    EvidenceSection(
                        state = state,
                        onShowEvidenceDialog = onShowEvidenceDialog
                    )

                    HorizontalDivider()

                    CommentSection(
                        state = state,
                        onCommentChange = onCommentChange,
                        onSendComment = onSendComment
                    )

                    HorizontalDivider()

                    HistorySection(ticket)
                }
            }
        }
    }

    val pending = state.pendingStatus
    if (pending != null) {
        AlertDialog(
            onDismissRequest = onDismissStatusDialog,
            title = { Text("Cambiar estado") },
            text = {
                Text(
                    "El ticket ${ticket?.id.orEmpty()} pasará de ${ticket?.status?.label.orEmpty()} " +
                            "a ${pending.label}. El cambio queda registrado en la trazabilidad."
                )
            },
            confirmButton = {
                TextButton(onClick = onConfirmStatusChange) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissStatusDialog) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (state.showEvidenceDialog) {
        AlertDialog(
            onDismissRequest = onDismissEvidenceDialog,
            title = { Text("Registrar evidencia") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    OutlinedTextField(
                        value = state.evidenceDescription,
                        onValueChange = onEvidenceDescriptionChange,
                        label = { Text("Descripción") },
                        singleLine = false,
                        isError = state.showEvidenceErrors && state.evidenceError != null,
                        supportingText = {
                            val error = state.evidenceError
                            if (state.showEvidenceErrors && error != null) {
                                Text(error)
                            } else {
                                Text("Qué muestra la evidencia")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = state.evidenceIncludeLocation,
                            onCheckedChange = onEvidenceLocationToggle
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Adjuntar ubicación")
                    }

                    AnimatedVisibility(visible = state.evidenceIncludeLocation) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${state.simulatedLocation} (simulada)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onSaveEvidence) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissEvidenceDialog) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun TicketHeader(ticket: Ticket) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PriorityChip(priority = ticket.priority)
            StatusChip(status = ticket.status)
        }

        Text(
            text = ticket.title,
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = ticket.description,
            style = MaterialTheme.typography.bodyLarge
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TagChip(text = ticket.type)
            TagChip(text = ticket.category)
        }
    }
}

@Composable
private fun DataSection(ticket: Ticket) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        SectionTitle("Información")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LabeledValue("Sucursal", ticket.branch, modifier = Modifier.weight(1f))
            LabeledValue("Solicitante", ticket.requesterName, modifier = Modifier.weight(1f))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LabeledValue("Equipo resolutor", ticket.resolverTeam, modifier = Modifier.weight(1f))
            LabeledValue(
                "Responsable",
                ticket.assignedTo ?: "Sin asignar",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LabeledValue("Creado", fullDate(ticket.createdAt), modifier = Modifier.weight(1f))
            LabeledValue("Última actualización", relativeTime(ticket.updatedAt), modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatusSection(
    state: TicketDetailUiState,
    onStatusSelected: (TicketStatus) -> Unit
) {
    val ticket = state.ticket ?: return

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        SectionTitle("Cambiar estado")

        if (!state.canManage) {
            Text(
                text = "Tu rol (${state.currentUserRole.label}) puede revisar el ticket pero no cambiar su estado.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else if (state.availableStatuses.isEmpty()) {
            Text(
                text = "El ticket está ${ticket.status.label.lowercase()}, ya no admite más cambios.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(
                text = "Estado actual: ${ticket.status.label}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                state.availableStatuses.forEach { status ->
                    SelectableChip(
                        text = status.label,
                        selected = false,
                        onClick = { onStatusSelected(status) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EvidenceSection(
    state: TicketDetailUiState,
    onShowEvidenceDialog: () -> Unit
) {
    val ticket = state.ticket ?: return

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        SectionTitle("Evidencias (${ticket.evidences.size})")

        if (ticket.evidences.isEmpty()) {
            Text(
                text = "Todavia no hay evidencias adjuntas.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            ticket.evidences.forEach { evidence ->
                EvidenceRow(evidence)
            }
        }

        if (state.canManage) {
            OutlinedButton(
                onClick = onShowEvidenceDialog,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Registrar evidencia")
            }
        }
    }
}

@Composable
private fun EvidenceRow(evidence: Evidence) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = evidence.description,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            if (evidence.location != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = evidence.location,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = relativeTime(evidence.createdAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CommentSection(
    state: TicketDetailUiState,
    onCommentChange: (String) -> Unit,
    onSendComment: () -> Unit
) {
    val ticket = state.ticket ?: return

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        SectionTitle("Comentarios (${ticket.comments.size})")

        if (ticket.comments.isEmpty()) {
            Text(
                text = "Sin comentarios todavia. Escribe el primero.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            ticket.comments.forEach { comment ->
                CommentRow(comment)
            }
        }

        OutlinedTextField(
            value = state.commentText,
            onValueChange = onCommentChange,
            label = { Text("Agregar comentario") },
            isError = state.showCommentErrors && state.commentError != null,
            supportingText = {
                val error = state.commentError
                if (state.showCommentErrors && error != null) {
                    Text(error)
                } else {
                    Text("Queda registrado con tu nombre y la hora")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onSendComment,
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
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enviar comentario")
            }
        }
    }
}

@Composable
private fun CommentRow(comment: Comment) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Comment,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = comment.authorName,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = relativeTime(comment.createdAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HistorySection(ticket: Ticket) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        SectionTitle("Trazabilidad (${ticket.history.size})")

        if (ticket.history.isEmpty()) {
            Text(
                text = "Sin cambios registrados.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            ticket.history.sortedByDescending { it.changedAt }.forEach { entry ->
                HistoryRow(entry)
            }
        }
    }
}

@Composable
private fun HistoryRow(entry: ChangeEntry) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "${entry.field}: ${entry.oldValue} -> ${entry.newValue}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${entry.changedBy} - ${relativeTime(entry.changedAt)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TicketDetailContentPreview() {
    val now = System.currentTimeMillis()

    val ticket = Ticket(
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
        description = "Al cerrar la venta la impresora responde con error de formato.",
        createdAt = now,
        updatedAt = now,
        comments = listOf(Comment("K-1", "Diego Munoz", "Se solicitó la configuración al proveedor.", now)),
        evidences = listOf(Evidence("E-1", "Pantalla con el error", null, now)),
        history = listOf(ChangeEntry("H-1", "Estado", "Abierto", "En progreso", "Diego Munoz", now))
    )

    PanalSuiteTheme {
        TicketDetailContent(
            state = TicketDetailUiState(
                ticket = ticket,
                currentUserName = "Diego Munoz",
                currentUserRole = UserRole.RESOLUTOR
            ),
            snackbarHostState = SnackbarHostState(),
            onBack = {},
            onCommentChange = {},
            onSendComment = {},
            onStatusSelected = {},
            onConfirmStatusChange = {},
            onDismissStatusDialog = {},
            onShowEvidenceDialog = {},
            onDismissEvidenceDialog = {},
            onEvidenceDescriptionChange = {},
            onEvidenceLocationToggle = {},
            onSaveEvidence = {},
            onRetry = {},
            onDismissError = {}
        )
    }
}
