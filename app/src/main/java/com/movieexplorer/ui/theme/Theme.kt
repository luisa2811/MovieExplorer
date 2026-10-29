package com.movieexplorer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryAmber,
    onPrimary = OnPrimaryDark,
    primaryContainer = SurfaceElevatedDark,
    onPrimaryContainer = TextPrimary,
    secondary = PrimaryAmber,
    onSecondary = OnPrimaryDark,
    secondaryContainer = SurfaceElevatedDark,
    onSecondaryContainer = TextPrimary,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevatedDark,
    onSurfaceVariant = TextSecondary,
    error = ErrorAccent,
    onError = TextPrimary,
    errorContainer = ErrorBackground,
    onErrorContainer = ErrorAccent,
    outline = TextSecondary,
    outlineVariant = SurfaceElevatedDark
)

@Composable
fun MovieExplorerTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
