package br.dev.fuvest.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Ink = Color(0xFF11151A)
private val Surface = Color(0xFF1A2027)
private val Teal = Color(0xFF4FD1C5)
private val Sand = Color(0xFFE8E6E1)
private val Alert = Color(0xFFE86A4B)

private val scheme = darkColorScheme(
    primary = Teal,
    onPrimary = Ink,
    secondary = Sand,
    onSecondary = Ink,
    error = Alert,
    background = Ink,
    onBackground = Sand,
    surface = Surface,
    onSurface = Sand,
    surfaceVariant = Color(0xFF232B33),
    onSurfaceVariant = Color(0xFFA9B2BC),
)

@Composable
fun FuvestTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = scheme, content = content)
}
