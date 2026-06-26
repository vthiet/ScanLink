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
    primaryContainer = Color(0xFF0F3A35),
    onPrimaryContainer = Color(0xFFCCFBF1),
    secondary = Color(0xFF2DD4BF),
    onSecondary = Color(0xFF062B27),
    secondaryContainer = Color(0xFF142F2C),
    onSecondaryContainer = Color(0xFFD8FFF8),
    tertiary = Color(0xFF00CFA4),
    onTertiary = Color(0xFF052E2B),
    tertiaryContainer = Color(0xFF143631),
    onTertiaryContainer = Color(0xFFCCFBF1),

    background = Color(0xFF080D12),
    onBackground = Color(0xFFE7FFFA),

    surface = Color(0xFF111820),
    onSurface = Color(0xFFE7FFFA),

    surfaceVariant = Color(0xFF1A242E),
    onSurfaceVariant = Color(0xFFB8C7D1),
    outline = Color(0xFF33434D),
    outlineVariant = Color(0xFF23313A),
    scrim = Color(0xFF000000),

    error = Color(0xFFFF7777),
    onError = Color(0xFF000000)
)

private val ScanLinkLightColorScheme = lightColorScheme(
    primary = Color(0xFF00CFA4),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFCCFBF1),
    onPrimaryContainer = Color(0xFF042F2E),
    secondary = Color(0xFF14B8A6),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE6FFFA),
    onSecondaryContainer = Color(0xFF134E4A),
    tertiary = Color(0xFF2DD4BF),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFCCFBF1),
    onTertiaryContainer = Color(0xFF134E4A),

    background = Color(0xFFF7FFFD),
    onBackground = Color(0xFF0F172A),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),

    surfaceVariant = Color(0xFFF0FDFA),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFF99F6E4),
    outlineVariant = Color(0xFFCCFBF1),
    scrim = Color(0xFF042F2E),

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
