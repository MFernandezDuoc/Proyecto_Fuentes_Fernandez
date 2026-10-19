package com.panalbit.panalsuite.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.panalbit.panalsuite.model.TicketPriority
import com.panalbit.panalsuite.model.TicketStatus
import com.panalbit.panalsuite.ui.theme.PriorityCritical
import com.panalbit.panalsuite.ui.theme.PriorityCriticalDark
import com.panalbit.panalsuite.ui.theme.PriorityHigh
import com.panalbit.panalsuite.ui.theme.PriorityHighDark
import com.panalbit.panalsuite.ui.theme.PriorityLow
import com.panalbit.panalsuite.ui.theme.PriorityLowDark
import com.panalbit.panalsuite.ui.theme.PriorityMedium
import com.panalbit.panalsuite.ui.theme.PriorityMediumDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Par de colores para dibujar un chip: el fondo y el texto. */
data class ChipColors(val container: Color, val content: Color)

/**
 * La prioridad usa siempre el mismo color, pero mas claro en tema oscuro para
 * que se lea bien sobre el fondo negro.
 */
@Composable
fun priorityChipColors(priority: TicketPriority): ChipColors {
    val isDark = isSystemInDarkTheme()

    val base = when (priority) {
        TicketPriority.LOW -> if (isDark) PriorityLowDark else PriorityLow
        TicketPriority.MEDIUM -> if (isDark) PriorityMediumDark else PriorityMedium
        TicketPriority.HIGH -> if (isDark) PriorityHighDark else PriorityHigh
        TicketPriority.CRITICAL -> if (isDark) PriorityCriticalDark else PriorityCritical
    }

    return ChipColors(
        container = base.copy(alpha = if (isDark) 0.24f else 0.14f),
        content = base
    )
}

/** El color del estado sale del tema, asi acompana el modo claro y oscuro solo. */
@Composable
fun statusChipColors(status: TicketStatus): ChipColors {
    val scheme = MaterialTheme.colorScheme

    return when (status) {
        TicketStatus.OPEN -> ChipColors(scheme.primaryContainer, scheme.onPrimaryContainer)
        TicketStatus.IN_PROGRESS -> ChipColors(scheme.tertiaryContainer, scheme.onTertiaryContainer)
        TicketStatus.PENDING -> ChipColors(scheme.secondaryContainer, scheme.onSecondaryContainer)
        TicketStatus.RESOLVED -> ChipColors(scheme.secondaryContainer, scheme.onSecondaryContainer)
        TicketStatus.CLOSED -> ChipColors(scheme.surfaceVariant, scheme.onSurfaceVariant)
        TicketStatus.CANCELLED -> ChipColors(scheme.errorContainer, scheme.onErrorContainer)
    }
}

/** "hace 3 h" en vez de una fecha larga, para que la tarjeta se lea rapido. */
fun relativeTime(millis: Long): String {
    val elapsedMinutes = (System.currentTimeMillis() - millis) / 60_000

    return when {
        elapsedMinutes < 1 -> "hace instantes"
        elapsedMinutes < 60 -> "hace $elapsedMinutes min"
        elapsedMinutes < 1_440 -> "hace ${elapsedMinutes / 60} h"
        else -> "hace ${elapsedMinutes / 1_440} d"
    }
}

/** Fecha completa, para el detalle del ticket. */
fun fullDate(millis: Long): String =
    SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date(millis))

/** Un color distinto por rol, para reconocerlo de un vistazo. */
@Composable
fun roleColor(role: com.panalbit.panalsuite.model.UserRole): Color {
    val scheme = MaterialTheme.colorScheme

    return when (role) {
        com.panalbit.panalsuite.model.UserRole.SOLICITANTE -> scheme.primary
        com.panalbit.panalsuite.model.UserRole.RESOLUTOR -> scheme.secondary
        com.panalbit.panalsuite.model.UserRole.SUPERVISOR -> scheme.tertiary
        com.panalbit.panalsuite.model.UserRole.GERENCIA -> scheme.outline
        com.panalbit.panalsuite.model.UserRole.ADMINISTRADOR -> scheme.error
    }
}
