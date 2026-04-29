package com.practice.reelbreak.ui.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practice.reelbreak.ui.navigation.Routes
import com.practice.reelbreak.ui.theme.LocalAppColors

data class NavItem(
    val icon: ImageVector,
    val label: String,
    val route: String
)

val navItems = listOf(
    NavItem(Icons.Filled.Home,     "Dashboard", Routes.DASHBOARD),
    NavItem(Icons.Filled.Shield,   "Focus",     Routes.FOCUS),
    NavItem(Icons.Filled.Settings, "Settings",  Routes.SETTINGS)
)

@Composable
fun FloatingButtonGroup(
    selectedRoute: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        // Subtle shadow for floating effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(32.dp),
                    ambientColor = if (colors.isDark) Color(0x408B5CF6) else Color(0x306B3FA0),
                    spotColor   = if (colors.isDark) Color(0x408B5CF6) else Color(0x306B3FA0)
                )
        )

        // Nav bar background
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .clip(RoundedCornerShape(29.dp))
                .background(
                    if (colors.isDark)
                        Brush.linearGradient(listOf(Color(0xCC1C1230), Color(0xCC160E28)))
                    else
                        Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0xFFFAF8FF)))
                )
                .border(
                    width = 1.dp,
                    color = if (colors.isDark) Color(0x33A78BFA) else Color(0xFFE0D8F0),
                    shape = RoundedCornerShape(29.dp)
                )
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                FigmaNavItem(
                    item = item,
                    isSelected = selectedRoute == item.route,
                    onClick = { onItemSelected(item.route) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FigmaNavItem(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current

    Box(
        modifier = modifier
            .height(46.dp)
            .clip(RoundedCornerShape(23.dp))
            .background(
                if (isSelected) {
                    if (colors.isDark)
                        Brush.verticalGradient(listOf(Color(0xFF7C3AED), Color(0xFF4C1D95)))
                    else
                        Brush.verticalGradient(listOf(Color(0xFFEDE8FF), Color(0xFFDDD0FF)))
                } else {
                    Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                }
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            // Selected state: icon + dot indicator below
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = if (colors.isDark) Color.White else Color(0xFF6B3FA0),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.height(1.dp))
                Text(
                    text = item.label,
                    color = if (colors.isDark) Color.White else Color(0xFF6B3FA0),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            // Unselected: icon only, muted
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = if (colors.isDark) colors.textMuted else Color(0xFFAA9FC8),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}