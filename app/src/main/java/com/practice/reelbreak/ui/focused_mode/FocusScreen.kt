package com.practice.reelbreak.ui.focusedmode

import android.provider.Settings
import android.content.ComponentName
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.*
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.practice.reelbreak.R
import com.practice.reelbreak.core.accessibility.ReelsAccessibilityService
import com.practice.reelbreak.ui.component.MainScaffold
import com.practice.reelbreak.ui.focused_mode.TimerSelectorRow
import com.practice.reelbreak.ui.permission.PermissionBottomSheet
import com.practice.reelbreak.ui.permission.PermissionSheetType
import com.practice.reelbreak.ui.theme.LocalAppColors
import com.practice.reelbreak.viewmodel.PermissionsViewModel

data class FocusAppChip(
    val name: String,
    val packageName: String,
    val iconRes: Int
)

private val focusApps = listOf(
    FocusAppChip("Instagram", "com.instagram.android", R.drawable.ic_instagram),
    FocusAppChip("YouTube", "com.google.android.youtube", R.drawable.ic_youtube),
    FocusAppChip("TikTok", "com.zhiliaoapp.musically", R.drawable.ic_tiktok),
    FocusAppChip("Facebook", "com.facebook.katana", R.drawable.ic_facebook),
    FocusAppChip("Snapchat", "com.snapchat.android", R.drawable.ic_snapchat),
    FocusAppChip("Twitter", "com.twitter.android", R.drawable.ic_twitter)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen(
    viewModel: FocusViewModel = hiltViewModel(),
    permissionsViewModel: PermissionsViewModel = hiltViewModel(),
    selectedTab: Int = 1,
    onTabSelected: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val colors = LocalAppColors.current
    val context = LocalContext.current

    val permissionUiState by permissionsViewModel.uiState.collectAsState()
    val isAccessibilityGranted = permissionUiState.permissionState.accessibilityGranted
    val sheetState by permissionsViewModel.sheetState.collectAsState()
    val permModalState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val nowGranted = isAccessibilityServiceEnabled(context)
                if (nowGranted) {
//                    suraj
//                    permissionsViewModel.updateAccessibilityGranted(true)
                    permissionsViewModel.dismissSheet()
                } else {
                    permissionsViewModel.checkAndShowSheetIfNeeded(context)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) { permissionsViewModel.checkAndShowSheetIfNeeded(context) }

    if (sheetState.isVisible && sheetState.type != null) {
        PermissionBottomSheet(
            type = sheetState.type!!,
            sheetState = permModalState,
            onDismiss = { permissionsViewModel.dismissSheet() },
            onAgree = { permissionsViewModel.onPermissionSheetAgree(context, sheetState.type!!) }
        )
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { msg ->
            android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
            viewModel.dismissError()
        }
    }

    val totalMillis = state.selectedMinutes.toLong() * 60_000L
    val progress = if (totalMillis > 0 && state.isFocusActive)
        (state.remainingMillis.toFloat() / totalMillis.toFloat()).coerceIn(0f, 1f)
    else 1f

    MainScaffold(selectedTab = selectedTab, onTabSelected = onTabSelected) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
        ) {
            // Purple header banner — matches Figma
            FocusHeaderBanner(isFocusActive = state.isFocusActive)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .padding(top = 20.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Timer card — white card with circular timer + start button
                TimerCard(
                    remainingMillis = state.remainingMillis,
                    isActive = state.isFocusActive,
                    progress = progress,
                    isFocusActive = state.isFocusActive,
                    onToggle = {
                        val granted = isAccessibilityServiceEnabled(context)
                        when {
                            !granted -> permissionsViewModel.showSheet(PermissionSheetType.ACCESSIBILITY)
                            state.isFocusActive -> viewModel.stopFocusSession()
                            else -> viewModel.startFocusSession()
                        }
                    }
                )

                // Duration card
                DurationCard(
                    selectedMinutes = state.selectedMinutes,
                    enabled = !state.isFocusActive,
                    onSelect = { viewModel.setSelectedMinutes(it) }
                )

                // Block Apps card
                BlockAppsCard(
                    selectedPackages = state.selectedApps,
                    onToggle = { pkg -> viewModel.toggleAppSelection(pkg) }
                )
            }
        }
    }
}

// ── Purple header banner ──────────────────────────────────────────────────────
@Composable
private fun FocusHeaderBanner(isFocusActive: Boolean) {
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
            .padding(horizontal = 20.dp, vertical = 0.dp)
            .padding(top = 48.dp, bottom = 20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Focus Mode",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = if (isFocusActive) "Session in progress" else "Block distracting apps",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.80f)
            )
        }
    }
}

// ── Timer Card ────────────────────────────────────────────────────────────────
@Composable
private fun TimerCard(
    remainingMillis: Long,
    isActive: Boolean,
    progress: Float,
    isFocusActive: Boolean,
    onToggle: () -> Unit
) {
    val colors = LocalAppColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (colors.isDark) colors.cardSurface
                else Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0xFFFAF8FF)))
            )
            .border(
                1.dp,
                if (colors.isDark) colors.borderSubtle else Color(0xFFE8E0F5),
                RoundedCornerShape(20.dp)
            )
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Circular timer
            FigmaTimerCircle(
                remainingMillis = remainingMillis,
                isActive = isActive,
                progress = progress
            )

            // Start/Stop button — Figma pill style
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(8.dp, RoundedCornerShape(999.dp), spotColor = Color(0x306B3FA0))
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        if (isFocusActive)
                            Brush.linearGradient(listOf(Color(0xFF1A7A44), Color(0xFF22A860)))
                        else
                            Brush.linearGradient(listOf(Color(0xFF6B3FA0), Color(0xFF4A2070)))
                    )
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null,
                        onClick = onToggle
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isFocusActive) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isFocusActive) "Stop Focus Session" else "Start Focus Session",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// ── Figma-style circular timer ────────────────────────────────────────────────
@Composable
private fun FigmaTimerCircle(
    remainingMillis: Long,
    isActive: Boolean,
    progress: Float
) {
    val colors = LocalAppColors.current
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(800),
        label = "timerProgress"
    )

    val totalSeconds = remainingMillis / 1000L
    val hours   = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    val trackColor = if (colors.isDark) Color(0xFF2D1B4E) else Color(0xFFEDE8FF)
    val arcColor   = if (colors.isDark) Color(0xFF8B5CF6) else Color(0xFF6B3FA0)

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(200.dp)) {
            val stroke = 14.dp.toPx()
            val arcSz  = size.width - stroke

            // Track
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(stroke / 2, stroke / 2),
                size = Size(arcSz, arcSz),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            // Progress
            drawArc(
                color = arcColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = Offset(stroke / 2, stroke / 2),
                size = Size(arcSz, arcSz),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            if (isActive) {
                val timeStr = if (hours > 0)
                    String.format("%02d:%02d:%02d", hours, minutes, seconds)
                else
                    String.format("%02d:%02d", minutes, seconds)

                Text(
                    text = timeStr,
                    color = if (colors.isDark) Color.White else Color(0xFF1A1035),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Remaining",
                    color = if (colors.isDark) colors.textSecondary else Color(0xFF6B5F88),
                    fontSize = 12.sp
                )
            } else {
                // Show selected time when ready — Figma shows "00:30:00"
                Text(
                    text = "00:30:00",
                    color = if (colors.isDark) Color.White else Color(0xFF1A1035),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ready to start",
                    color = if (colors.isDark) colors.textSecondary else Color(0xFF6B5F88),
                    fontSize = 12.sp
                )
            }
        }
    }
}

// ── Duration Card ─────────────────────────────────────────────────────────────
@Composable
private fun DurationCard(
    selectedMinutes: Int,
    enabled: Boolean,
    onSelect: (Int) -> Unit
) {
    val colors = LocalAppColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (colors.isDark) colors.cardSurface
                else Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0xFFFAF8FF)))
            )
            .border(
                1.dp,
                if (colors.isDark) colors.borderSubtle else Color(0xFFE8E0F5),
                RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "Duration",
                color = if (colors.isDark) colors.textPrimary else Color(0xFF1A1035),
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )

            // Figma pill-shaped duration chips
            FigmaDurationRow(
                selectedMinutes = selectedMinutes,
                enabled = enabled,
                onSelect = onSelect
            )
        }
    }
}

private data class DurationChip(val minutes: Int, val label: String)

private val durationChips = listOf(
    DurationChip(15,  "15m"),
    DurationChip(30,  "30m"),
    DurationChip(60,  "1h"),
    DurationChip(120, "2h"),
    DurationChip(240, "4h"),
    DurationChip(480, "8h"),
    DurationChip(1440,"1d"),
)

@Composable
private fun FigmaDurationRow(
    selectedMinutes: Int,
    enabled: Boolean,
    onSelect: (Int) -> Unit
) {
    val colors = LocalAppColors.current
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(durationChips) { chip ->
            val isSelected = chip.minutes == selectedMinutes
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .widthIn(min = 56.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        if (isSelected) {
                            if (colors.isDark) Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFF4C1D95)))
                            else Brush.linearGradient(listOf(Color(0xFF6B3FA0), Color(0xFF4A2070)))
                        } else {
                            if (colors.isDark) Brush.linearGradient(listOf(Color(0xFF1C1230), Color(0xFF160E28)))
                            else Brush.linearGradient(listOf(Color(0xFFEDE8FF), Color(0xFFE8E0FF)))
                        }
                    )
                    .border(
                        width = if (isSelected) 0.dp else 1.dp,
                        color = if (colors.isDark) Color(0xFF2D1B4E) else Color(0xFFD0C4F0),
                        shape = RoundedCornerShape(999.dp)
                    )
                    .then(
                        if (enabled) Modifier.clickable(
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                            indication = null
                        ) { onSelect(chip.minutes) } else Modifier
                    )
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = chip.label,
                    color = if (isSelected) Color.White
                    else if (colors.isDark) colors.textSecondary else Color(0xFF4A4068),
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

// ── Block Apps Card ───────────────────────────────────────────────────────────
@Composable
private fun BlockAppsCard(
    selectedPackages: Set<String>,
    onToggle: (String) -> Unit
) {
    val colors = LocalAppColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (colors.isDark) colors.cardSurface
                else Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0xFFFAF8FF)))
            )
            .border(
                1.dp,
                if (colors.isDark) colors.borderSubtle else Color(0xFFE8E0F5),
                RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "Block Apps",
                color = if (colors.isDark) colors.textPrimary else Color(0xFF1A1035),
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )

            // 2-column grid matching Figma
            focusApps.chunked(2).forEach { rowApps ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowApps.forEach { app ->
                        val isSelected = selectedPackages.contains(app.packageName)
                        FigmaAppChip(
                            app = app,
                            isSelected = isSelected,
                            onToggle = { onToggle(app.packageName) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowApps.size < 2) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun FigmaAppChip(
    app: FocusAppChip,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    Box(
        modifier = modifier
            .height(90.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) {
                    if (colors.isDark) Brush.linearGradient(listOf(Color(0xFF2D1B4E), Color(0xFF1E1040)))
                    else Brush.linearGradient(listOf(Color(0xFFEDE8FF), Color(0xFFE0D8FF)))
                } else {
                    if (colors.isDark) Brush.linearGradient(listOf(Color(0xFF1C1230), Color(0xFF160E28)))
                    else Brush.linearGradient(listOf(Color(0xFFF8F5FF), Color(0xFFF0EBFF)))
                }
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) {
                    if (colors.isDark) Color(0xFF8B5CF6) else Color(0xFF6B3FA0)
                } else {
                    if (colors.isDark) Color(0xFF2D1B4E) else Color(0xFFD8CFF0)
                },
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null,
                onClick = onToggle
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.06f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = app.iconRes),
                        contentDescription = app.name,
                        modifier = Modifier.size(28.dp).background(Color.White, shape = RoundedCornerShape(16.dp))
                    )
                }
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .background(Color(0xFF22A860)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✓", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Text(
                text = app.name,
                color = if (isSelected) {
                    if (colors.isDark) colors.textPrimary else Color(0xFF1A1035)
                } else {
                    if (colors.isDark) colors.textSecondary else Color(0xFF6B5F88)
                },
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}

fun isAccessibilityServiceEnabled(context: android.content.Context): Boolean {
    val expectedComponent = ComponentName(context, ReelsAccessibilityService::class.java)
    val enabledServices = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false
    return enabledServices.split(":").any { entry ->
        ComponentName.unflattenFromString(entry) == expectedComponent
    }
}