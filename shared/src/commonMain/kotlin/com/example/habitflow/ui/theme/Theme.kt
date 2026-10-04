package com.example.habitflow.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = IntelliJPrimary,
    onPrimary = IntelliJTextPrimary,
    secondary = IntelliJSecondary,
    onSecondary = IntelliJTextPrimary,
    background = IntelliJBackground,
    onBackground = IntelliJTextPrimary,
    surface = IntelliJSurface,
    onSurface = IntelliJTextPrimary,
    surfaceVariant = IntelliJSurfaceElevated,
    onSurfaceVariant = IntelliJTextSecondary,
    error = IntelliJError,
    onError = IntelliJTextPrimary,
)

@Composable
fun HabitFlowTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content,
    )
}
