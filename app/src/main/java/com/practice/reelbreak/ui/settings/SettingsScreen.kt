package com.practice.reelbreak.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practice.reelbreak.ui.component.MainScaffold
import com.practice.reelbreak.ui.permission.PermissionSheetType
import com.practice.reelbreak.ui.theme.LocalAppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    selectedTab: Int = 2,
    onTabSelected: (Int) -> Unit,
    viewModel: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val colors = LocalAppColors.current

    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.refreshPermissions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Permission detail sheet
    val activeSheet = uiState.activePermissionSheet
    if (activeSheet != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = viewModel::closePermissionSheet,
            sheetState = sheetState,
            containerColor = if (colors.isDark) Color(0xFF1A1228) else Color(0xFFFFFFFF),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            PermissionDetailSheet(
                type = activeSheet,
                isGranted = when (activeSheet) {
                    PermissionSheetType.ACCESSIBILITY -> uiState.isAccessibilityGranted
                    PermissionSheetType.USAGE_ACCESS  -> uiState.isUsageAccessGranted
                    PermissionSheetType.OVERLAY       -> uiState.isOverlayGranted
                },
                onOpenSettings = {
                    viewModel.openPermissionSettings(activeSheet)
                    viewModel.closePermissionSheet()
                },
                onDismiss = viewModel::closePermissionSheet
            )
        }
    }

    MainScaffold(selectedTab = selectedTab, onTabSelected = onTabSelected) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
        ) {
            // Purple header — same style as Dashboard and Focus
            SettingsPurpleHeader()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = 16.dp, bottom = 120.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // App Settings card
                item {
                    FigmaSettingsCard {
                        Column {
                            FigmaCardTitle(text = "App Settings")
                            Spacer(Modifier.height(4.dp))

                            FigmaToggleRow(
                                icon = Icons.Outlined.Notifications,
                                iconBg = Color(0xFFEDE8FF),
                                iconTint = Color(0xFF6B3FA0),
                                title = "Notifications",
                                subtitle = "Get reminders and limit alerts",
                                isEnabled = uiState.isNotificationsEnabled,
                                onToggle = viewModel::toggleNotifications
                            )
                            FigmaDivider()
                            FigmaToggleRow(
                                icon = Icons.Outlined.CalendarToday,
                                iconBg = Color(0xFFEDE8FF),
                                iconTint = Color(0xFF6B3FA0),
                                title = "Weekend Relax",
                                subtitle = "Disable limits on Saturdays and Sundays",
                                isEnabled = uiState.isWeekendRelaxEnabled,
                                onToggle = viewModel::toggleWeekendRelax
                            )
                        }
                    }
                }

                // Frequently Asked Questions card
                item {
                    FigmaSettingsCard {
                        Column {
                            FigmaCardTitle(text = "Frequently Asked Questions")
                            Spacer(Modifier.height(8.dp))
                            FigmaFaqItem(
                                question = "How does ReelBreak detect short-form videos?",
                                answer = "ReelBreak uses Android's Accessibility Service to detect when you open a reels or shorts screen inside Instagram, YouTube, TikTok, Snapchat, or Facebook."
                            )
                            FigmaDivider()
                            FigmaFaqItem(
                                question = "What permissions does ReelBreak need?",
                                answer = "Accessibility Service (required), Usage Access (required for time tracking), and Display Over Apps (optional for overlay bubble)."
                            )
                            FigmaDivider()
                            FigmaFaqItem(
                                question = "Can I customize which apps are blocked?",
                                answer = "Yes! In Focus Mode you can choose exactly which apps to block during a session."
                            )
                            FigmaDivider()
                            FigmaFaqItem(
                                question = "Is my data private?",
                                answer = "All data stays on your device. ReelBreak never sends anything to any server. It only reads which app is on screen — never content, messages, or personal data."
                            )
                        }
                    }
                }

                // Support & Info card
                item {
                    FigmaSettingsCard {
                        Column {
                            FigmaCardTitle(text = "Support & Info")
                            Spacer(Modifier.height(4.dp))

                            FigmaActionRow(
                                icon = Icons.Outlined.ChatBubbleOutline,
                                iconBg = Color(0xFFEDE8FF),
                                iconTint = Color(0xFF6B3FA0),
                                title = "Send Feedback",
                                onClick = viewModel::sendFeedback
                            )
                            FigmaDivider()
                            FigmaActionRow(
                                icon = Icons.Outlined.StarOutline,
                                iconBg = Color(0xFFFFF8E0),
                                iconTint = Color(0xFFD4A017),
                                title = "Rate ReelBreak",
                                onClick = viewModel::rateApp
                            )
                            FigmaDivider()
                            FigmaActionRow(
                                icon = Icons.Outlined.Share,
                                iconBg = Color(0xFFE0F2FF),
                                iconTint = Color(0xFF3B6FE0),
                                title = "Share with Friends",
                                onClick = viewModel::shareApp
                            )
                            FigmaDivider()
                            FigmaActionRow(
                                icon = Icons.Outlined.Shield,
                                iconBg = Color(0xFFE8F5EE),
                                iconTint = Color(0xFF22A860),
                                title = "Privacy Policy",
                                onClick = viewModel::openPrivacyPolicy
                            )
                        }
                    }
                }

                // Version footer
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "ReelBreak v${uiState.appVersion}",
                            color = if (colors.isDark) colors.textMuted else Color(0xFF8B82A8),
                            fontSize = 12.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Built with",
                                color = if (colors.isDark) colors.textMuted else Color(0xFF8B82A8),
                                fontSize = 12.sp
                            )
                            Text(text = "❤️", fontSize = 12.sp)
                            Text(
                                text = "for mindful scrolling",
                                color = if (colors.isDark) colors.textMuted else Color(0xFF8B82A8),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Purple Header ─────────────────────────────────────────────────────────────
@Composable
private fun SettingsPurpleHeader() {
    val colors = LocalAppColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(
                if (colors.isDark)
                    Brush.linearGradient(listOf(Color(0xFF2D1060), Color(0xFF1A0840)))
                else
                    Brush.linearGradient(listOf(Color(0xFF6B3FA0), Color(0xFF4A2070)))
            )
            .padding(horizontal = 20.dp)
            .padding(top = 48.dp, bottom = 20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "Settings", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = "Customize your experience", fontSize = 13.sp, color = Color.White.copy(alpha = 0.80f))
        }
    }
}

// ── Shared card wrapper ───────────────────────────────────────────────────────
@Composable
private fun FigmaSettingsCard(content: @Composable () -> Unit) {
    val colors = LocalAppColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (colors.isDark) colors.cardSurface
                else Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0xFFFAF8FF)))
            )
            .border(
                1.dp,
                if (colors.isDark) colors.borderSubtle else Color(0xFFE0D8F0),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
private fun FigmaCardTitle(text: String) {
    val colors = LocalAppColors.current
    Text(
        text = text,
        color = if (colors.isDark) colors.textPrimary else Color(0xFF1A1035),
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun FigmaDivider() {
    val colors = LocalAppColors.current
    Divider(
        modifier = Modifier.padding(vertical = 0.dp),
        color = if (colors.isDark) colors.borderSubtle else Color(0xFFF0EBF8),
        thickness = 1.dp
    )
}

// ── Toggle row ────────────────────────────────────────────────────────────────
@Composable
private fun FigmaToggleRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (colors.isDark) Color(0xFF2A1F40) else iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = if (colors.isDark) colors.purpleSoft else iconTint, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = title, color = if (colors.isDark) colors.textPrimary else Color(0xFF1A1035), fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(text = subtitle, color = if (colors.isDark) colors.textSecondary else Color(0xFF6B5F88), fontSize = 12.sp)
        }
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = if (colors.isDark) Color(0xFF7C3AED) else Color(0xFF6B3FA0),
                uncheckedThumbColor = if (colors.isDark) colors.textMuted else Color(0xFFBBB0D0),
                uncheckedTrackColor = if (colors.isDark) Color(0xFF2A1F40) else Color(0xFFEDE8FF),
                uncheckedBorderColor = if (colors.isDark) colors.borderSubtle else Color(0xFFD0C4F0)
            )
        )
    }
}

// ── FAQ item ──────────────────────────────────────────────────────────────────
@Composable
private fun FigmaFaqItem(question: String, answer: String) {
    val colors = LocalAppColors.current
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { expanded = !expanded }
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = question,
                color = if (colors.isDark) colors.textPrimary else Color(0xFF1A1035),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                lineHeight = 20.sp
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = if (colors.isDark) colors.textMuted else Color(0xFF8B82A8),
                modifier = Modifier.size(20.dp)
            )
        }
        if (expanded) {
            Text(
                text = answer,
                color = if (colors.isDark) colors.textSecondary else Color(0xFF6B5F88),
                fontSize = 13.sp,
                lineHeight = 19.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

// ── Action row (chevron) ──────────────────────────────────────────────────────
@Composable
private fun FigmaActionRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    onClick: () -> Unit
) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (colors.isDark) Color(0xFF2A1F40) else iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = if (colors.isDark) colors.purpleSoft else iconTint, modifier = Modifier.size(20.dp))
        }
        Text(
            text = title,
            color = if (colors.isDark) colors.textPrimary else Color(0xFF1A1035),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = if (colors.isDark) colors.textMuted else Color(0xFF8B82A8),
            modifier = Modifier.size(18.dp)
        )
    }
}

// Keep for backward compat
data class PermissionSheetData(
    val title: String,
    val description: String,
    val whyNeeded: String,
    val howToEnable: String
)

@Composable
fun RowDivider(horizontal: androidx.compose.ui.unit.Dp = 0.dp) {
    val colors = LocalAppColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontal)
            .height(1.dp)
            .background(colors.borderSubtle)
    )
}