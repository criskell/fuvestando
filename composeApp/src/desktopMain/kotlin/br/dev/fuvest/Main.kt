package br.dev.fuvest

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import br.dev.fuvest.ui.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Fuvest",
        state = rememberWindowState(size = DpSize(520.dp, 860.dp)),
    ) {
        App()
    }
}
