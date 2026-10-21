package com.panalbit.panalsuite.ui

/**
 * Pantalla de acceso.
 *
 * La función que conoce al ViewModel le pide el estado, se lo pasa a la parte
 * visual y avisa hacia arriba cuando la sesión quedó iniciada.
 */

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.panalbit.panalsuite.model.AuthSession
import com.panalbit.panalsuite.model.User
import com.panalbit.panalsuite.model.UserRole
import com.panalbit.panalsuite.ui.components.ErrorBanner
import com.panalbit.panalsuite.ui.theme.PanalSuiteTheme
import com.panalbit.panalsuite.viewmodel.LoginUiState
import com.panalbit.panalsuite.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: (AuthSession) -> Unit
) {

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.session) {
        val session = state.session
        if (session != null) {
            onLoginSuccess(session)
            viewModel.onSessionHandled()
        }
    }

    LoginContent(
        state = state,
        demoAccounts = viewModel.demoAccounts(),
        demoPassword = viewModel.demoPassword(),
        onUsernameChange = { viewModel.onUsernameChange(it) },
        onPasswordChange = { viewModel.onPasswordChange(it) },
        onDemoAccountClick = { viewModel.fillDemoAccount(it) },
        onSubmit = { viewModel.submit() }
    )
}

@Composable
fun LoginContent(
    state: LoginUiState,
    demoAccounts: List<User>,
    demoPassword: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onDemoAccountClick: (User) -> Unit,
    onSubmit: () -> Unit
) {

    var passwordVisible by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.widthIn(max = 480.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                LoginHeader()

                OutlinedTextField(
                    value = state.username,
                    onValueChange = onUsernameChange,
                    label = { Text("Nombre de acceso") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    isError = state.showErrors && state.usernameError != null,
                    supportingText = {
                        val error = state.usernameError
                        if (state.showErrors && error != null) {
                            Text(error)
                        } else {
                            Text("Ejemplo: resolutor")
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                                contentDescription = if (passwordVisible) {
                                    "Ocultar contraseña"
                                } else {
                                    "Mostrar contraseña"
                                }
                            )
                        }
                    },
                    singleLine = true,
                    isError = state.showErrors && state.passwordError != null,
                    supportingText = {
                        val error = state.passwordError
                        if (state.showErrors && error != null) {
                            Text(error)
                        } else {
                            Text("Contraseña de prueba: $demoPassword")
                        }
                    },
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                ErrorBanner(message = state.errorMessage.orEmpty())

                Button(
                    onClick = onSubmit,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Text("Ingresar")
                    }
                }

                DemoAccountsSection(
                    accounts = demoAccounts,
                    onAccountClick = onDemoAccountClick
                )
            }
        }
    }
}

@Composable
private fun LoginHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Business,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )

        Text(
            text = "PanalSuite",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Gestión móvil de flujos de trabajo",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DemoAccountsSection(
    accounts: List<User>,
    onAccountClick: (User) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Cuentas de prueba",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Toca una para completar el formulario",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        accounts.forEach { account ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    onClick = { onAccountClick(account) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = account.fullName,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text(
                    text = account.role.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginContentPreview() {
    PanalSuiteTheme {
        LoginContent(
            state = LoginUiState(
                username = "resolu",
                password = "12",
                showErrors = true
            ),
            demoAccounts = listOf(
                User("U-01", "solicitante", "Carolina Soto", "carolina@panalbit.cl", UserRole.SOLICITANTE),
                User("U-02", "resolutor", "Diego Munoz", "diego@panalbit.cl", UserRole.RESOLUTOR)
            ),
            demoPassword = "1234",
            onUsernameChange = {},
            onPasswordChange = {},
            onDemoAccountClick = {},
            onSubmit = {}
        )
    }
}
