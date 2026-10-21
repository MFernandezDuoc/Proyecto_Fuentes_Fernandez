package com.panalbit.panalsuite.ui

/**
 * Pantalla de Ajustes.
 *
 * Acá se elige la forma en que se ve la aplicación y se guardan los datos de
 * contacto del usuario, para que la app los recuerde entre una ejecución y otra.
 */

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.panalbit.panalsuite.model.ThemeMode
import com.panalbit.panalsuite.model.TicketPriority
import com.panalbit.panalsuite.model.UserPreferences
import com.panalbit.panalsuite.ui.components.SelectableChip
import com.panalbit.panalsuite.ui.components.priorityChipColors
import com.panalbit.panalsuite.ui.theme.PanalSuiteTheme
import com.panalbit.panalsuite.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {

    val preferences by viewModel.preferences.collectAsState()

    SettingsScreenContent(
        preferences = preferences,
        onThemeModeChange = { viewModel.onThemeModeChange(it) },
        onDefaultPriorityChange = { viewModel.onDefaultPriorityChange(it) },
        onContactDataChange = { name, email -> viewModel.onContactDataChange(name, email) },
        onClearSavedData = { viewModel.clearSavedData() },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    preferences: UserPreferences,
    onThemeModeChange: (ThemeMode) -> Unit,
    onDefaultPriorityChange: (TicketPriority) -> Unit,
    onContactDataChange: (String, String) -> Unit,
    onClearSavedData: () -> Unit,
    onBack: () -> Unit
) {

    var showClearDialog by rememberSaveable { mutableStateOf(false) }
    var showSavedMessage by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var contactName by remember(preferences.contactName) { mutableStateOf(preferences.contactName) }
    var contactEmail by remember(preferences.contactEmail) { mutableStateOf(preferences.contactEmail) }

    LaunchedEffect(showSavedMessage) {
        if (showSavedMessage) {
            scope.launch { snackbarHostState.showSnackbar("Datos de contacto guardados") }
            showSavedMessage = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes") },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Apariencia",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            ThemeMode.entries.forEach { mode ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onThemeModeChange(mode) }
                ) {
                    RadioButton(
                        selected = preferences.themeMode == mode,
                        onClick = { onThemeModeChange(mode) }
                    )
                    Text(text = mode.label)
                }
            }

            HorizontalDivider()

            Text(
                text = "Prioridad por defecto",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TicketPriority.entries.forEach { priority ->
                    SelectableChip(
                        text = priority.label,
                        selected = preferences.defaultPriority == priority,
                        onClick = { onDefaultPriorityChange(priority) },
                        dotColor = priorityChipColors(priority).content
                    )
                }
            }

            Text(
                text = "Es con la que parte el formulario de nueva solicitud.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            Text(
                text = "Mis datos de contacto",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = contactName,
                onValueChange = { contactName = it },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = contactEmail,
                onValueChange = { contactEmail = it },
                label = { Text("Correo") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    onContactDataChange(contactName, contactEmail)
                    showSavedMessage = true
                },
                enabled = contactName.trim() != preferences.contactName ||
                        contactEmail.trim() != preferences.contactEmail,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar datos de contacto")
            }

            HorizontalDivider()

            Text(
                text = "Datos guardados en el teléfono",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (preferences.hasContactData || preferences.lastUsername.isNotBlank()) {
                Text(text = "Nombre: ${preferences.contactName.ifBlank { "-" }}")
                Text(text = "Correo: ${preferences.contactEmail.ifBlank { "-" }}")
                Text(text = "Último acceso: ${preferences.lastUsername.ifBlank { "-" }}")

                OutlinedButton(
                    onClick = { showClearDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Borrar datos guardados")
                }
            } else {
                Text(
                    text = "No hay datos guardados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Borrar datos guardados") },
            text = {
                Text("Se olvidarán el nombre, el correo y el último acceso. La apariencia y la prioridad por defecto no se tocan.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearSavedData()
                        showClearDialog = false
                    }
                ) {
                    Text("Borrar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenContentPreview() {
    PanalSuiteTheme {
        SettingsScreenContent(
            preferences = UserPreferences(
                contactName = "Camila Rojas",
                contactEmail = "camila@panalbit.cl",
                lastUsername = "solicitante",
                defaultPriority = TicketPriority.HIGH,
                themeMode = ThemeMode.DARK
            ),
            onThemeModeChange = {},
            onDefaultPriorityChange = {},
            onContactDataChange = { _, _ -> },
            onClearSavedData = {},
            onBack = {}
        )
    }
}
