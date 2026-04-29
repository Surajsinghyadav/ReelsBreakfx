package com.practice.reelbreak.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practice.reelbreak.ui.theme.LocalAppColors

@Composable
fun StrictDetails() {
    val colors = LocalAppColors.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Blocks all reels & shorts the moment you open any short-form feed. No timer, no grace period.",
            color = Color.White.copy(alpha = 0.80f),
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
    }
}

@Composable
fun LimitSettingsContent(
    dailyTimeLimitMinutes: Int,
    dailyReelLimit: Int,
    onTimeDecrement: () -> Unit,
    onTimeIncrement: () -> Unit,
    onReelDecrement: () -> Unit,
    onReelIncrement: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        // Daily Time Limit row — Figma style with label, value, and +/- stepper
        LimitRow(
            label = "Daily Time Limit",
            value = "$dailyTimeLimitMinutes min",
            onDecrement = onTimeDecrement,
            onIncrement = onTimeIncrement
        )

        // Visual slider bar for time
        LimitSliderBar(
            value = dailyTimeLimitMinutes.toFloat(),
            max = 200f
        )

        Spacer(Modifier.height(4.dp))

        // Daily Reel Count row
        LimitRow(
            label = "Daily Reel Count",
            value = "$dailyReelLimit reels",
            onDecrement = onReelDecrement,
            onIncrement = onReelIncrement
        )

        LimitSliderBar(
            value = dailyReelLimit.toFloat(),
            max = 100f
        )
    }
}

@Composable
private fun LimitRow(
    label: String,
    value: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.90f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Minus button
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.20f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDecrement
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("−", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        // Plus button
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.20f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onIncrement
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("+", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LimitSliderBar(value: Float, max: Float) {
    val fraction = (value / max).coerceIn(0f, 1f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Color.White.copy(alpha = 0.20f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction)
                .fillMaxHeight()
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White)
        )
    }
}

@Composable
fun SmartFilterDetails() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Only allow reels from accounts you follow or have liked before. Coming soon.",
            color = Color.White.copy(alpha = 0.80f),
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
    }
}

// Keep CompactStepper for any backwards compatibility
@Composable
fun CompactStepper(
    label: String,
    value: Int,
    unit: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = if (colors.isDark) 0.22f else 0.06f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = colors.textPrimary.copy(alpha = 0.92f), fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier.size(30.dp).clip(RoundedCornerShape(9.dp))
                    .background(Color.White.copy(alpha = if (colors.isDark) 0.12f else 0.16f))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDecrement),
                contentAlignment = Alignment.Center
            ) { Text("−", color = colors.textPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            Text("$value $unit", color = colors.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Box(
                modifier = Modifier.size(30.dp).clip(RoundedCornerShape(9.dp))
                    .background(Color.White.copy(alpha = if (colors.isDark) 0.12f else 0.16f))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onIncrement),
                contentAlignment = Alignment.Center
            ) { Text("+", color = colors.textPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun HowItWorksRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, iconTint: Color = Color.White.copy(alpha = 0.75f)) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
        androidx.compose.material3.Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(15.dp))
        Text(text = text, color = Color.White.copy(alpha = 0.80f), fontSize = 12.sp, lineHeight = 17.sp, modifier = Modifier.weight(1f))
    }
}