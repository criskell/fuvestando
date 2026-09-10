package br.dev.fuvest.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import br.dev.fuvest.data.CardRepository

private enum class Tab(val label: String) {
    Review("Revisar"),
    Cards("Cartões"),
    Roadmap("Roadmap"),
}

@Composable
fun App(repository: CardRepository = remember { CardRepository() }) {
    var tab by remember { mutableStateOf(Tab.Review) }
    val cards by repository.cards.collectAsState()

    FuvestTheme {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    Tab.entries.forEach { entry ->
                        NavigationBarItem(
                            selected = tab == entry,
                            onClick = { tab = entry },
                            label = { Text(entry.label) },
                            icon = {},
                        )
                    }
                }
            },
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                when (tab) {
                    Tab.Review -> ReviewScreen(repository)
                    Tab.Cards -> CardsScreen(repository, cards)
                    Tab.Roadmap -> RoadmapScreen()
                }
            }
        }
    }
}
