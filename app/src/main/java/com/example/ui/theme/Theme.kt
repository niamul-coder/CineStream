package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkCineColorScheme = darkColorScheme(
    primary = CineCyan,
    onPrimary = Color.Black,
    primaryContainer = CineSurfaceHigh,
    onPrimaryContainer = CineCyan,
    secondary = CineElectricPurple,
    onSecondary = Color.White,
    secondaryContainer = CineSurfaceVariant,
    onSecondaryContainer = CineElectricPurple,
    tertiary = CineRed,
    onTertiary = Color.White,
    background = CineBackground,
    onBackground = CineTextPrimary,
    surface = CineSurface,
    onSurface = CineTextPrimary,
    surfaceVariant = CineSurfaceVariant,
    onSurfaceVariant = CineTextSecondary,
    outline = CineBorder,
    outlineVariant = CineBorder.copy(alpha = 0.5f),
    error = CineRed,
    onError = Color.White
)

val LightCineColorScheme = lightColorScheme(
    primary = CineViolet,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = CineViolet,
    secondary = CineCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = CineRed,
    onTertiary = Color.White,
    background = CineLightBackground,
    onBackground = CineLightTextPrimary,
    surface = CineLightSurface,
    onSurface = CineLightTextPrimary,
    surfaceVariant = CineLightSurfaceVariant,
    onSurfaceVariant = CineLightTextSecondary,
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
    error = CineRed,
    onError = Color.White
)

@Composable
fun CineStreamTheme(
    darkTheme: Boolean = true, // Default to immersive cinematic dark experience
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkCineColorScheme else LightCineColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    CineStreamTheme(darkTheme = darkTheme, content = content)
}
