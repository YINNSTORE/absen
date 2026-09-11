package com.albasroh.absensi.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PrimaryBlue = Color(0xFF1769E0)
private val LightBlue = Color(0xFFEAF2FF)
private val DarkBlue = Color(0xFF8AB4FF)

private val LightColors = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = LightBlue,
    onPrimaryContainer = Color(0xFF06285F),
    secondary = Color(0xFF4C6A92),
    background = Color(0xFFF7FAFF),
    surface = Color.White,
    surfaceVariant = Color(0xFFEFF3F9)
)

private val DarkColors = darkColorScheme(
    primary = DarkBlue,
    onPrimary = Color(0xFF06285F),
    primaryContainer = Color(0xFF173B72),
    onPrimaryContainer = Color(0xFFD8E6FF),
    secondary = Color(0xFFB8C7DF),
    background = Color(0xFF08111F),
    surface = Color(0xFF101B2B),
    surfaceVariant = Color(0xFF1A2638)
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(22.dp),
    large = RoundedCornerShape(30.dp)
)

@Composable
fun AppTheme(isDark: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isDark) DarkColors else LightColors,
        shapes = AppShapes,
        content = content
    )
}
