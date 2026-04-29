package com.practice.reelbreak.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessibilityNew
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.practice.reelbreak.domain.model.ActiveBlockMode
import com.practice.reelbreak.ui.component.MainScaffold
import com.practice.reelbreak.ui.permission.PermissionBottomSheet
import com.practice.reelbreak.ui.permission.PermissionSheetType
import com.practice.reelbreak.ui.theme.LocalAppColors
import com.practice.reelbreak.viewmodel.DashboardViewModel
import com.practice.reelbreak.viewmodel.PermissionsViewModel
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState

data class PermissionPagerItem(
    val type: PermissionSheetType,
    val icon: ImageVector,
    val iconTint: Color,
    val title: String,
    val description: String,
    val buttonText: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    dashboardViewModel: DashboardViewModel = viewModel(),
    permissionsViewModel: PermissionsViewModel = viewModel(),
    selectedTab: Int = 0,
    onTabSelected: (Int) -> Unit
) {
    val dashboardState by dashboardViewModel.uiState.collectAsState()
    val colors = LocalAppColors.current
    val context = LocalContext.current

    val sheetState by permissionsViewModel.sheetState.collectAsState()
    val permModalState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val permissionUiState by permissionsViewModel.uiState.collectAsState()
    val permissionState = permissionUiState.permissionState

    // Stats — wired to real data (dummy for display until backend wired)
    val reelsWatched by remember { mutableStateOf(12) }
    val minutesSpent by remember { mutableStateOf(18) }

    LaunchedEffect(Unit) {
        permissionsViewModel.checkAndShowSheetIfNeeded(context)
    }

    if (sheetState.isVisible && sheetState.type != null) {
        PermissionBottomSheet(
            type = sheetState.type!!,
            sheetState = permModalState,
            onDismiss = { permissionsViewModel.dismissSheet() },
            onAgree = { permissionsViewModel.onPermissionSheetAgree(context, sheetState.type!!) }
        )
    }

    val basePermissionItems = listOf(
        PermissionPagerItem(
            type = PermissionSheetType.ACCESSIBILITY,
            icon = Icons.Outlined.AccessibilityNew,
            iconTint = Color(0xFF6B3FA0),
            title = "Accessibility Access",
            description = "Required to detect & block reels in real time.",
            buttonText = "Turn On"
        ),
        PermissionPagerItem(
            type = PermissionSheetType.USAGE_ACCESS,
            icon = Icons.Outlined.BarChart,
            iconTint = Color(0xFF3B6FE0),
            title = "Usage Access",
            description = "Required to track time spent on short-video apps.",
            buttonText = "Grant Access"
        ),
        PermissionPagerItem(
            type = PermissionSheetType.OVERLAY,
            icon = Icons.Outlined.Layers,
            iconTint = Color(0xFF22A860),
            title = "Overlay Permission",
            description = "Optional — shows a live reel counter over other apps.",
            buttonText = "Enable"
        )
    )

    val missingPermissionItems = basePermissionItems.filter { item ->
        when (item.type) {
            PermissionSheetType.ACCESSIBILITY -> !permissionState.accessibilityGranted
            PermissionSheetType.USAGE_ACCESS  -> !permissionState.usageStatsGranted
            PermissionSheetType.OVERLAY       -> !permissionState.overlayGranted
        }
    }

    MainScaffold(selectedTab = selectedTab, onTabSelected = onTabSelected) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
        ) {
            // Purple header banner
            DashboardHeader(
                isOverlayGranted = permissionState.overlayGranted,
                isOverlayEnabled = dashboardState.isOverlayEnabled,
                isDarkMode = dashboardState.isDarkMode,
                isAccessibilityGranted = permissionState.accessibilityGranted,
                onVisibilityClick = {
                    if (!permissionState.overlayGranted) permissionsViewModel.showSheet(PermissionSheetType.OVERLAY)
                    else dashboardViewModel.toggleOverlayEnabled()
                },
                onThemeToggle = { dashboardViewModel.toggleTheme() }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = 16.dp, bottom = 120.dp
                )
            ) {
                // Permission pager cards
                if (missingPermissionItems.isNotEmpty()) {
                    item {
                        val pagerState = rememberPagerState(
                            initialPage = 0,
                            pageCount = { missingPermissionItems.size }
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxWidth()
                            ) { page ->
                                val item = missingPermissionItems[page]
                                val isGranted = when (item.type) {
                                    PermissionSheetType.ACCESSIBILITY -> permissionState.accessibilityGranted
                                    PermissionSheetType.USAGE_ACCESS  -> permissionState.usageStatsGranted
                                    PermissionSheetType.OVERLAY       -> permissionState.overlayGranted
                                }
                                PermissionPagerCard(
                                    item = item,
                                    isGranted = isGranted,
                                    onClick = { permissionsViewModel.showSheet(item.type) }
                                )
                            }
                            if (missingPermissionItems.size > 1) {
                                PermissionPagerIndicator(
                                    currentPage = pagerState.currentPage,
                                    pageCount = missingPermissionItems.size
                                )
                            }
                        }
                    }
                }

                // Blocking Mode section
                item {
                    Text(
                        text = "Blocking Mode",
                        color = if (colors.isDark) colors.textPrimary else Color(0xFF1A1035),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    )
                }

                blockModeOptions.forEach { option ->
                    item {
                        val isOn = when (option.mode) {
                            BlockMode.BLOCK_NOW    -> dashboardState.activeMode == ActiveBlockMode.STRICT && permissionState.accessibilityGranted
                            BlockMode.LIMIT_BASED  -> dashboardState.activeMode == ActiveBlockMode.LIMIT
                            BlockMode.SMART_FILTER -> dashboardState.activeMode == ActiveBlockMode.SMART
                        }
                        val isSelected = dashboardState.activeMode == when (option.mode) {
                            BlockMode.BLOCK_NOW    -> ActiveBlockMode.STRICT
                            BlockMode.LIMIT_BASED  -> ActiveBlockMode.LIMIT
                            BlockMode.SMART_FILTER -> ActiveBlockMode.SMART
                        }

                        BlockModeCard(
                            option = option,
                            isSelected = isSelected,
                            isExpanded = dashboardState.expandedMode == option.mode,
                            isOn = isOn,
                            onClick = {
                                if (!permissionState.accessibilityGranted) {
                                    permissionsViewModel.showSheet(PermissionSheetType.ACCESSIBILITY)
                                } else {
                                    dashboardViewModel.onBlockModeCardClicked(option.mode)
                                }
                            },
                            onExpandToggle = { dashboardViewModel.onExpandToggle(option.mode) },
                            detailContent = {
                                when (option.mode) {
                                    BlockMode.BLOCK_NOW    -> StrictDetails()
                                    BlockMode.LIMIT_BASED  -> LimitSettingsContent(
                                        dailyTimeLimitMinutes = dashboardState.dailyTimeLimitMinutes,
                                        dailyReelLimit = dashboardState.dailyReelLimit,
                                        onTimeDecrement = { dashboardViewModel.decrementDailyTimeLimit() },
                                        onTimeIncrement = { dashboardViewModel.incrementDailyTimeLimit() },
                                        onReelDecrement  = { dashboardViewModel.decrementDailyReelLimit() },
                                        onReelIncrement  = { dashboardViewModel.incrementDailyReelLimit() }
                                    )
                                    BlockMode.SMART_FILTER -> SmartFilterDetails()
                                }
                            }
                        )
                    }
                }

                // Today's Stats section — Figma image 5
                item {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Today's Stats",
                        color = if (colors.isDark) colors.textPrimary else Color(0xFF1A1035),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    )
                }

                item {
                    TodayStatsRow(reelsWatched = reelsWatched, minutesSpent = minutesSpent)
                }
            }
        }
    }
}

@Composable
private fun TodayStatsRow(reelsWatched: Int, minutesSpent: Int) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            value = reelsWatched.toString(),
            label = "Reels Watched",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            value = minutesSpent.toString(),
            label = "Minutes Spent",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (colors.isDark) colors.cardSurface
                else Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0xFFF8F5FF)))
            )
            .border(
                1.dp,
                if (colors.isDark) colors.borderSubtle else Color(0xFFE0D8F0),
                RoundedCornerShape(16.dp)
            )
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = value,
                color = if (colors.isDark) colors.purpleSoft else Color(0xFF6B3FA0),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = if (colors.isDark) colors.textSecondary else Color(0xFF6B5F88),
                fontSize = 13.sp
            )
        }
    }
}