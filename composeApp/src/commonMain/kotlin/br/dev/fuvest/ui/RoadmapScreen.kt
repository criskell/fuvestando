package br.dev.fuvest.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.item
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.dev.fuvest.roadmap.Roadmap
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.todayIn

@Composable
fun RoadmapScreen() {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val next = Roadmap.nextMilestone(today)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            if (next != null) {
                val dias = today.daysUntil(next.date)
                Column {
                    Text(
                        dias.toString(),
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        (if (dias == 1) "dia" else "dias") + " até " + next.title.lowercase(),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            } else {
                Text("Acabou o calendário de 2027.", style = MaterialTheme.typography.titleMedium)
            }
        }

        items(Roadmap.milestones) { milestone ->
            val dias = today.daysUntil(milestone.date)
            val passou = dias < 0
            Card(
                Modifier.fillMaxWidth(),
                colors = if (milestone.critical && !passou) {
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                } else {
                    CardDefaults.cardColors()
                },
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "${milestone.date.dayOfMonth}/${milestone.date.monthNumber}" +
                            if (passou) "  ·  passou" else "  ·  faltam $dias dias",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(milestone.title, style = MaterialTheme.typography.titleMedium)
                    Text(milestone.detail, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        item {
            Text(
                "Obras obrigatórias",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 12.dp),
            )
        }

        items(Roadmap.obras) { obra ->
            Text(obra, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
