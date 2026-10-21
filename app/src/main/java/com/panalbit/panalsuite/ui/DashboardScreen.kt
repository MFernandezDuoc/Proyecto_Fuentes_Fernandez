package com.panalbit.panalsuite.ui

/**
 * Indicadores de seguimiento.
 *
 * Se recalculan cada vez que se entra a la pantalla, a partir de los tickets
 * guardados.
 */

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.panalbit.panalsuite.model.DashboardStats
import com.panalbit.panalsuite.ui.components.ErrorBanner
import com.panalbit.panalsuite.ui.components.KpiCard
import com.panalbit.panalsuite.ui.components.LoadingState
import com.panalbit.panalsuite.ui.components.SectionTitle
import com.panalbit.panalsuite.ui.components.WorkloadBar
import com.panalbit.panalsuite.ui.theme.PanalSuiteTheme
import com.panalbit.panalsuite.viewmodel.DashboardUiState
import com.panalbit.panalsuite.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    DashboardContent(
        state = state,
        onRefresh = { viewModel.refresh() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    state: DashboardUiState,
    onRefresh: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Indicadores") },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar indicadores")
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

            ErrorBanner(message = state.errorMessage.orEmpty(), onRetry = onRefresh)

            Text(
                text = "Resumen para ${state.currentUserName.ifBlank { "la jefatura" }}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiCard(
                    title = "ABIERTOS",
                    value = state.stats.totalOpen.toString(),
                    icon = Icons.Default.Work,
                    accent = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = "EN CURSO",
                    value = state.stats.totalInProgress.toString(),
                    icon = Icons.Default.Schedule,
                    accent = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiCard(
                    title = "RESUELTOS",
                    value = state.stats.totalResolved.toString(),
                    icon = Icons.Default.CheckCircle,
                    accent = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f),
                    subtitle = "${state.stats.resolutionRate}% del total"
                )
                KpiCard(
                    title = "CRÍTICOS",
                    value = state.stats.totalCritical.toString(),
                    icon = Icons.Default.Warning,
                    accent = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f),
                    subtitle = "Sin cerrar"
                )
            }

            KpiCard(
                title = "TIEMPO PROMEDIO DE RESOLUCIÓN",
                value = state.averageResolutionText,
                icon = Icons.Default.Schedule,
                accent = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
                subtitle = "${state.stats.totalTickets} ticket(s) en seguimiento"
            )

            if (state.isLoading && !state.hasData) {
                LoadingState(message = "Calculando indicadores...")
            } else if (state.workloadSorted.isNotEmpty()) {
                WorkloadSection(state)
            }
        }
    }
}

@Composable
private fun WorkloadSection(state: DashboardUiState) {
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
            SectionTitle("Carga por equipo")

            state.workloadSorted.forEach { (team, count) ->
                WorkloadBar(
                    team = team,
                    count = count,
                    maxCount = state.maxWorkload
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardContentPreview() {
    PanalSuiteTheme {
        DashboardContent(
            state = DashboardUiState(
                stats = DashboardStats(
                    totalOpen = 3,
                    totalInProgress = 4,
                    totalResolved = 3,
                    totalCritical = 1,
                    averageResolutionHours = 18.5,
                    workloadByTeam = mapOf(
                        "Infraestructura" to 3,
                        "Soporte Aplicaciones" to 2,
                        "Producto" to 2,
                        "Mesa de Ayuda" to 1
                    )
                ),
                currentUserName = "Paula Rivas"
            ),
            onRefresh = {}
        )
    }
}
