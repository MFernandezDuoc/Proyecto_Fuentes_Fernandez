package com.panalbit.panalsuite.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.panalbit.panalsuite.model.TicketPriority
import com.panalbit.panalsuite.model.TicketStatus

/** Chip con el color de la prioridad del ticket. */
@Composable
fun PriorityChip(priority: TicketPriority, modifier: Modifier = Modifier) {
    val colors = priorityChipColors(priority)

    Surface(
        color = colors.container,
        shape = RoundedCornerShape(percent = 50),
        modifier = modifier
    ) {
        Text(
            text = priority.label,
            color = colors.content,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/** Chip con el color del estado del ticket. */
@Composable
fun StatusChip(status: TicketStatus, modifier: Modifier = Modifier) {
    val colors = statusChipColors(status)

    Surface(
        color = colors.container,
        shape = RoundedCornerShape(percent = 50),
        modifier = modifier
    ) {
        Text(
            text = status.label,
            color = colors.content,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/** Etiqueta neutra para datos secundarios, como la categoria o la sucursal. */
@Composable
fun TagChip(text: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(percent = 50),
        modifier = modifier
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/** Chip de rol, con el color primario para diferenciarlo de los de ticket. */
@Composable
fun RoleChip(text: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = color.copy(alpha = 0.16f),
        shape = RoundedCornerShape(percent = 50),
        modifier = modifier
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/**
 * Chip que se puede tocar para filtrar. Cuando esta seleccionado se pinta con el
 * color primario, para que se vea de inmediato que filtro esta activo.
 */
@Composable
fun SelectableChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    dotColor: Color? = null
) {
    val container = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val content = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = container,
        shape = RoundedCornerShape(percent = 50),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            if (dotColor != null) {
                Surface(
                    color = dotColor,
                    shape = CircleShape,
                    modifier = Modifier.size(8.dp)
                ) {}
            }

            Text(
                text = text,
                color = content,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
