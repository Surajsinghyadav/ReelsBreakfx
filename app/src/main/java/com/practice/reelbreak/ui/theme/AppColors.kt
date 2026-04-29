package com.practice.reelbreak.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class AppColors(
    val isDark: Boolean,
    // ── Text
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    // ── Surfaces
    val background: Brush,
    val cardSurface: Brush,
    val glassSurface: Brush,
    // ── Brand
    val purplePrimary: Color,
    val purpleDeep: Color,
    val purpleSoft: Color,
    val blueAccent: Color,
    val successGreen: Color,
    val warningOrange: Color,
    val errorRed: Color,
    // ── Buttons
    val button: Brush,
    val buttonDanger: Brush,
    val buttonSuccess: Brush,
    // ── Nav
    val navSelected: Brush,
    // ── Mode cards
    val modeBlock: Brush,
    val modeLimit: Brush,
    val modeSmart: Brush,
    // ── Borders & glows
    val borderSubtle: Color,
    val borderPurple: Color,
    val borderActive: Color,
    val glowPurple: Color,
    val glowBlue: Color,
    val glowTeal: Color,
    val glowRed: Color
)

// ── LIGHT theme matching Figma design ─────────────────────────────────────────
fun lightAppColors() = AppColors(
    isDark          = false,
    textPrimary     = Color(0xFF1A1035),
    textSecondary   = Color(0xFF4A4068),
    textMuted       = Color(0xFF8B82A8),

    // Soft lavender-white background like Figma
    background      = Brush.linearGradient(
        colors = listOf(Color(0xFFF2EFFF), Color(0xFFEDE8FF), Color(0xFFEBE5FF)),
        start  = Offset(0f, 0f),
        end    = Offset(1080f, 2400f)
    ),
    // Pure white cards
    cardSurface     = Brush.linearGradient(
        colors = listOf(Color(0xFFFFFFFF), Color(0xFFFAF8FF)),
        start  = Offset(0f, 0f),
        end    = Offset(400f, 400f)
    ),
    glassSurface    = Brush.linearGradient(colors = listOf(Color(0x88FFFFFF), Color(0x44FFFFFF))),

    purplePrimary   = Color(0xFF6B3FA0),   // Figma purple
    purpleDeep      = Color(0xFF4A2070),
    purpleSoft      = Color(0xFF9B72D0),

    blueAccent      = Color(0xFF3B6FE0),
    successGreen    = Color(0xFF22A860),
    warningOrange   = Color(0xFFE07800),
    errorRed        = Color(0xFFD42B2B),

    // Figma button: solid purple pill
    button          = Brush.linearGradient(colors = listOf(Color(0xFF6B3FA0), Color(0xFF5530880))),
    buttonDanger    = Brush.linearGradient(colors = listOf(Color(0xFFD42B2B), Color(0xFF8B0000))),
    buttonSuccess   = Brush.linearGradient(colors = listOf(Color(0xFF22A860), Color(0xFF0E6040))),

    navSelected     = Brush.verticalGradient(colors = listOf(Color(0xFF6B3FA0), Color(0xFF4A2070))),

    // Figma mode cards: purple filled for selected
    modeBlock       = Brush.linearGradient(colors = listOf(Color(0xFF6B3FA0), Color(0xFF4A2070)), start = Offset(0f,0f), end = Offset(300f,300f)),
    modeLimit       = Brush.linearGradient(colors = listOf(Color(0xFF6B3FA0), Color(0xFF4A2070)), start = Offset(0f,0f), end = Offset(300f,300f)),
    modeSmart       = Brush.linearGradient(colors = listOf(Color(0xFF6B3FA0), Color(0xFF4A2070)), start = Offset(0f,0f), end = Offset(300f,300f)),

    borderSubtle    = Color(0xFFE0D8F0),
    borderPurple    = Color(0xFFB094E0),
    borderActive    = Color(0xFF6B3FA0),
    glowPurple      = Color(0x306B3FA0),
    glowBlue        = Color(0x303B6FE0),
    glowTeal        = Color(0x3022A860),
    glowRed         = Color(0x30D42B2B)
)

// ── DARK theme ────────────────────────────────────────────────────────────────
fun darkAppColors() = AppColors(
    isDark        = true,
    textPrimary   = Color(0xFFEEECF5),
    textSecondary = Color(0xFF9B97B8),
    textMuted     = Color(0xFF5C5875),

    background    = Brush.linearGradient(
        colors = listOf(Color(0xFF0D0618), Color(0xFF130A22), Color(0xFF180D2A), Color(0xFF130A22), Color(0xFF0D0618)),
        start = Offset(0f, 0f), end = Offset(1080f, 2400f)
    ),
    cardSurface   = Brush.linearGradient(
        colors = listOf(Color(0xFF1C1230), Color(0xFF160E28)),
        start = Offset(0f, 0f), end = Offset(400f, 400f)
    ),
    glassSurface  = Brush.linearGradient(colors = listOf(Color(0x28A78BFA), Color(0x0CA78BFA))),

    purplePrimary = Color(0xFF8B5CF6),
    purpleDeep    = Color(0xFF6D28D9),
    purpleSoft    = Color(0xFFA78BFA),

    blueAccent    = Color(0xFF60A5FA),
    successGreen  = Color(0xFF34D399),
    warningOrange = Color(0xFFFBBF24),
    errorRed      = Color(0xFFF87171),

    button        = Brush.linearGradient(colors = listOf(Color(0xFF7C3AED), Color(0xFF4C1D95))),
    buttonDanger  = Brush.linearGradient(colors = listOf(Color(0xFFF87171), Color(0xFFB91C1C))),
    buttonSuccess = Brush.linearGradient(colors = listOf(Color(0xFF34D399), Color(0xFF065F46))),

    navSelected   = Brush.verticalGradient(colors = listOf(Color(0xFF8B5CF6), Color(0xFF4C1D95))),

    modeBlock     = Brush.linearGradient(colors = listOf(Color(0xFF2E1065), Color(0xFF1A0840)), start = Offset(0f,0f), end = Offset(300f,300f)),
    modeLimit     = Brush.linearGradient(colors = listOf(Color(0xFF1E3A8A), Color(0xFF0F1A3D)), start = Offset(0f,0f), end = Offset(300f,300f)),
    modeSmart     = Brush.linearGradient(colors = listOf(Color(0xFF064E3B), Color(0xFF0A1F18)), start = Offset(0f,0f), end = Offset(300f,300f)),

    borderSubtle  = Color(0x22A78BFA),
    borderPurple  = Color(0x668B5CF6),
    borderActive  = Color(0xFFA78BFA),
    glowPurple    = Color(0x408B5CF6),
    glowBlue      = Color(0x4060A5FA),
    glowTeal      = Color(0x4034D399),
    glowRed       = Color(0x40F87171)
)