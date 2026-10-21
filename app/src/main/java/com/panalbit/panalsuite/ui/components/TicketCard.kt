package com.panalbit.panalsuite.ui.components

/**
 * La tarjeta de un ticket dentro de la bandeja.
 *
 * Muestra lo justo para decidir si vale la pena abrirlo: identificador,
 * prioridad, título, estado, categoría, sucursal y responsable.
 */

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.panalbit.panalsuite.model.Ticket
import com.panalbit.panalsuite.model.TicketPriority
import com.panalbit.panalsuite.model.TicketStatus
import com.panalbit.panalsuite.ui.theme.PanalSuiteTheme

@Composable
fun TicketCard(
    ticket: Ticket,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ticket.id,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                PriorityChip(priority = ticket.priority)
            }

            Text(
                text = ticket.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusChip(status = ticket.status)
                TagChip(text = ticket.category)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconLine(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    text = relativeTime(ticket.updatedAt)
                )
                IconLine(
                    icon = { Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    text = ticket.branch
                )
            }

            IconLine(
                icon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp)) },
                text = ticket.assignedTo ?: "Sin responsable asignado"
            )
        }
    }
}

@Composable
private fun IconLine(
    icon: @Composable () -> Unit,
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TicketCardPreview() {
    val now = System.currentTimeMillis()

    PanalSuiteTheme {
        TicketCard(
            ticket = Ticket(
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
                description = "La impresora responde con error de formato.",
                createdAt = now,
                updatedAt = now
            ),
            onClick = {}
        )
    }
}
