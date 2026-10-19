package com.panalbit.panalsuite.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val stats = uiState.stats

    Scaffold(
        topBar = { TopAppBar(title = { Text("Dashboard PanalSuite") }) }
    ) { padding ->
        if (uiState.isLoading || stats == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    KpiCard("Abiertos", stats.totalOpen.toString(), Modifier.weight(1f))
                    KpiCard("En Progreso", stats.totalInProgress.toString(), Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    KpiCard("Resueltos", stats.totalResolved.toString(), Modifier.weight(1f))
                    KpiCard("Tiempo Prom. (hrs)", stats.avgResolutionTimeHours.toString(), Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Carga por Equipo", style = MaterialTheme.typography.titleMedium)

                stats.teamWorkload.forEach { (team, count) ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(team)
                            Text("$count tickets")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KpiCard(title: String, value: String, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineLarge)
        }
    }
}
