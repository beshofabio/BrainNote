package com.fabio.brainnote.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,

    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,

    onPrimary = DarkOnPrimary,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = LightSecondary,

    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,

    onPrimary = LightOnPrimary,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface
)

@Composable
fun BrainNoteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}