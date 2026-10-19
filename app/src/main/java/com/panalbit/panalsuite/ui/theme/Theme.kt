package com.panalbit.panalsuite.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB74D),
    secondary = Color(0xFFFF8A65),
    tertiary = Color(0xFF81C784)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFE65100),
    secondary = Color(0xFFF57C00),
    tertiary = Color(0xFF2E7D32)
)

@Composable
fun PanalSuiteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
