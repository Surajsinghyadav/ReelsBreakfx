package com.practice.reelbreak.ui.theme

import androidx.compose.ui.graphics.Color

// ── Light Mode Palette ──────────────────────────────────────────────
object LightColors {
    val background        = Color(0xFFF0EEF8)   // lavender-tinted page bg
    val surface           = Color(0xFFFFFFFF)   // card surface
    val surfaceOffset     = Color(0xFFEDEBF5)   // slightly deeper surface
    val purplePrimary     = Color(0xFF6B3FCC)   // main brand purple (buttons, selected)
    val purpleDeep        = Color(0xFF4A2A99)   // dark purple (header bg)
    val purpleSoft        = Color(0xFFD4C4F0)   // soft lavender (unselected pill bg)
    val purpleCard        = Color(0xFFE8E0F7)   // selected app card bg
    val textPrimary       = Color(0xFF1A1A2E)   // near-black heading text
    val textSecondary     = Color(0xFF5A5A7A)   // muted body text
    val textOnPurple      = Color(0xFFFFFFFF)   // text on purple bg
    val border            = Color(0xFF6B3FCC)   // selected card border
    val borderSubtle      = Color(0xFFE0DCEE)   // unselected card border
    val sliderTrack       = Color(0xFFFFFFFF).copy(alpha = 0.3f)
    val sliderThumb       = Color(0xFFFFFFFF)
    val borderPurple = Color(0xFF673AB7)
            val textMuted = Color(0xFF1C1B1B)
    val divider           = Color(0xFFE8E4F0)
    val success           = Color(0xFF27AE60)
    val error             = Color(0xFFE53935)
    val toggleActive      = Color(0xFF6B3FCC)
    val toggleInactive    = Color(0xFFD0CCDD)
    // Nav
    val navBackground     = Color(0xFFFFFFFF)
    val navSelected       = Color(0xFF6B3FCC)
    val navUnselected     = Color(0xFFAAAAAA)
    val navPillBg         = Color(0xFFEDE8F7)
}

// ── Dark Mode Palette ───────────────────────────────────────────────
object DarkColors {
    val background        = Color(0xFF0F0D1A)
    val surface           = Color(0xFF1A1730)
    val surfaceOffset     = Color(0xFF221F38)
    val purplePrimary     = Color(0xFF9B6FFF)
    val purpleDeep        = Color(0xFF3D2B7A)
    val purpleSoft        = Color(0xFF2A2244)
    val purpleCard        = Color(0xFF251E45)
    val textPrimary       = Color(0xFFEEEBFF)
    val textSecondary     = Color(0xFFAAABCC)
    val textOnPurple      = Color(0xFFFFFFFF)
    val border            = Color(0xFF7C55DD)
    val borderSubtle      = Color(0xFF2D2850)
    val sliderTrack       = Color(0xFFFFFFFF).copy(alpha = 0.2f)
    val sliderThumb       = Color(0xFFFFFFFF)
    val divider           = Color(0xFF272440)
    val success           = Color(0xFF4CAF7D)
    val error             = Color(0xFFEF5350)
    val toggleActive      = Color(0xFF9B6FFF)
    val toggleInactive    = Color(0xFF3A3660)
    val navBackground     = Color(0xFF16132A)
    val navSelected       = Color(0xFF9B6FFF)
    val navUnselected     = Color(0xFF6B6888)
    val navPillBg         = Color(0xFF231F3D)
}