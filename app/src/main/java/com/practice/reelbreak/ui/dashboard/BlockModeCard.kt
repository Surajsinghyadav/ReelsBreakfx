package com.practice.reelbreak.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practice.reelbreak.ui.theme.LocalAppColors

@Composable
fun BlockModeCard(
    option: BlockModeOption,
    isSelected: Boolean,
    isExpanded: Boolean,
    isOn: Boolean,
    onClick: () -> Unit,
    onExpandToggle: () -> Unit,
    detailContent: (@Composable () -> Unit)?
) {
    val colors = LocalAppColors.current

    // Figma style: white card when unselected, purple when selected
    val cardBackground = if (isSelected) {
        Brush.linearGradient(listOf(Color(0xFF6B3FA0), Color(0xFF4A2070)))
    } else {
        if (colors.isDark) colors.cardSurface
        else Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0xFFFAF8FF)))
    }

    val borderColor = if (isSelected) Color(0xFF6B3FA0).copy(alpha = 0.6f)
    else if (colors.isDark) colors.borderSubtle
    else Color(0xFFE0D8F0)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isSelected) 8.dp else 2.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = if (isSelected) Color(0x306B3FA0) else Color.Transparent
            )
            .clip(RoundedCornerShape(16.dp))
            .background(cardBackground)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // Main row — clicking turns mode On/Off
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Content
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = option.title,
                        color = if (isSelected) Color.White else if (colors.isDark) colors.textPrimary else Color(0xFF1A1035),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = option.subtitle,
                        color = if (isSelected) Color.White.copy(alpha = 0.75f)
                        else if (colors.isDark) colors.textSecondary else Color(0xFF6B5F88),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    // Show expanded limit controls or coming soon inline
                    if (isSelected && option.mode == BlockMode.SMART_FILTER) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Coming Soon",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Radio button — Figma style
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = if (isSelected) Color.White else if (colors.isDark) colors.borderPurple else Color(0xFFB094E0),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }
            }

            // Expanded detail area
            if (detailContent != null && isSelected) {
                HorizontalDivider(
                    color = if (isSelected) Color.White.copy(alpha = 0.15f) else borderColor,
                    thickness = 0.8.dp
                )

                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically(tween(250)),
                    exit = shrinkVertically(tween(200))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSelected) Color.Black.copy(alpha = 0.15f)
                                else Color.Transparent
                            )
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        detailContent()
                    }
                }

                // "How it works" expand row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onExpandToggle
                        )
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isExpanded) "▲  Hide settings" else "▼  Show settings",
                        color = if (isSelected) Color.White.copy(alpha = 0.65f)
                        else if (colors.isDark) colors.textMuted else Color(0xFF8B82A8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}