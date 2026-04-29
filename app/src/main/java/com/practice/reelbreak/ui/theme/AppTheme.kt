package com.practice.reelbreak.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalAppColors = staticCompositionLocalOf { lightAppColors() }

private fun darkMaterial() = darkColorScheme(
    background       = Color(0xFF0F0425),
    surface          = Color(0xFF1D0E42),
    primary          = Color(0xFF8B5CF6),
    onBackground     = Color.White,
    onSurface        = Color.White,
    secondary        = Color(0xFF7B5FCC),
    onPrimary        = Color.White,
    surfaceVariant   = Color(0xFF241B38),
    onSurfaceVariant = Color(0xFF9B97B8)
)

private fun lightMaterial() = lightColorScheme(
    background       = Color(0xFFF2EFFF),
    surface          = Color(0xFFFFFFFF),
    primary          = Color(0xFF6B3FA0),
    onBackground     = Color(0xFF1A1035),
    onSurface        = Color(0xFF1A1035),
    secondary        = Color(0xFF9B72D0),
    onPrimary        = Color.White,
    surfaceVariant   = Color(0xFFF5F0FF),
    onSurfaceVariant = Color(0xFF4A4068)
)

@Composable
fun ReelBreakTheme(
    isDarkMode: Boolean,
    content: @Composable () -> Unit
) {
    val appColors = if (isDarkMode) darkAppColors() else lightAppColors()

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = if (isDarkMode) darkMaterial() else lightMaterial(),
            typography  = Typography,
            content     = content
        )
    }
}