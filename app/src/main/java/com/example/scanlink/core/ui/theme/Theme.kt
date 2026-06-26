package com.example.scanlink.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ScanLinkDarkColorScheme = darkColorScheme(
    primary = Color(0xFF00E0A4),
    onPrimary = Color(0xFF0A2E26),
    secondary = Color(0xFF63DDB4),
    tertiary = Color(0xFF00CFA4),

    background = Color(0xFF0F0F11),
    onBackground = Color(0xFFFFFFFF),

    surface = Color(0xFF1A1A1E),
    onSurface = Color(0xFFFFFFFF),

    surfaceVariant = Color(0xFF1E1E24),
    onSurfaceVariant = Color(0xFFCCCCCC),
    outline = Color(0xFF3A3A42),
    outlineVariant = Color(0xFF2A2A32),

    error = Color(0xFFFF7777),
    onError = Color(0xFF000000)
)

private val ScanLinkLightColorScheme = lightColorScheme(
    primary = Color(0xFF00B889),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF00A77E),
    tertiary = Color(0xFF00A77E),

    background = Color(0xFFF7F9FA),
    onBackground = Color(0xFF0F172A),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),

    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF334155),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),

    error = Color(0xFFDC2626),
    onError = Color(0xFFFFFFFF)
)

@Composable
fun ScanLinkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) ScanLinkDarkColorScheme else ScanLinkLightColorScheme,
        content = content
    )
}
