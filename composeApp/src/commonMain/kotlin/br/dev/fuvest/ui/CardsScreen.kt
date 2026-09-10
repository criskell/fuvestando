package br.dev.fuvest.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.dev.fuvest.data.CardRepository
import br.dev.fuvest.domain.Card as FlashCard

@Composable
fun CardsScreen(repository: CardRepository, cards: List<FlashCard>) {
    var deck by remember { mutableStateOf("") }
    var front by remember { mutableStateOf("") }
    var back by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Novo cartão", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = deck,
            onValueChange = { deck = it },
            label = { Text("Matéria") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = front,
            onValueChange = { front = it },
            label = { Text("Frente") },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = back,
            onValueChange = { back = it },
            label = { Text("Verso") },
            modifier = Modifier.fillMaxWidth(),
        )
        Button(
            onClick = {
                repository.add(deck, front, back)
                front = ""
                back = ""
            },
            enabled = front.isNotBlank() && back.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Adicionar")
        }

        Text(
            "${cards.size} cartões",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(cards, key = { it.id }) { card ->
                Card(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(card.front, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "${card.deck} · ${card.state}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        TextButton(onClick = { repository.delete(card.id) }) {
                            Text("Apagar", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}
