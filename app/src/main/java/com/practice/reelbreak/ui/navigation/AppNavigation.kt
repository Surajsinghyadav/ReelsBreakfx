package com.practice.reelbreak.ui.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.practice.reelbreak.ui.dashboard.DashboardScreen
import com.practice.reelbreak.ui.focusedmode.FocusScreen
import com.practice.reelbreak.ui.onboarding.OnboardingScreen
import com.practice.reelbreak.ui.settings.SettingsScreen
import com.practice.reelbreak.viewmodel.DashboardViewModel
import com.practice.reelbreak.viewmodel.MainViewModel
import com.practice.reelbreak.viewmodel.PermissionsViewModel

@Composable
fun AppNavigation(
    startDestination: NavKey,                          // OnboardingKey or DashboardKey
    mainViewModel: MainViewModel,
    dashboardViewModel: DashboardViewModel = viewModel(),
    permissionsViewModel: PermissionsViewModel = viewModel(),
) {
    // ── Back-stack: start at the correct initial screen ──────────────
    val backStack = rememberNavBackStack(startDestination)

    // ── Active tab index (drives bottom-nav highlight) ────────────────
    var selectedTab by remember { mutableIntStateOf(
        when (startDestination) {
            is DashboardKey -> 0
            is FocusKey     -> 1
            is SettingsKey  -> 2
            else            -> 0
        }
    )}

    // ── Bottom-nav tab handler ────────────────────────────────────────
    val onTabSelected: (Int) -> Unit = { index ->
        selectedTab = index
        // Replace the top of the main-graph back-stack with the target tab.
        // We keep the back-stack clean (at most one of each tab key).
        val targetKey: NavKey = when (index) {
            0 -> DashboardKey
            1 -> FocusKey
            2 -> SettingsKey
            else -> DashboardKey
        }

        // Navigation 3 NavBackStack is a MutableList<NavKey> (SnapshotStateList internally in Nav 3.0.0-alpha01)
        // Note: NavBackStack is a typealias for SnapshotStateList<K> in some versions or a wrapper.
        // Looking at common Nav 3 patterns:

        val dashboardIdx = backStack.indexOfFirst { it is DashboardKey }

        if (dashboardIdx != -1) {
            // Trim back to Dashboard, then push target (unless already Dashboard)
            while (backStack.size > dashboardIdx + 1) {
                backStack.removeAt(backStack.size - 1)
            }
        }

        if (backStack.lastOrNull() != targetKey) {
            backStack.add(targetKey)
        }
    }

    // ── Nav Display (Nav 3) ───────────────────────────────────────────
    NavDisplay(
        backStack = backStack,
        onBack = {
            if (backStack.size > 1) {
                backStack.removeAt(backStack.size - 1)
            }
        },
        entryProvider = entryProvider {

            // ── Onboarding ────────────────────────────────────────
            entry<OnboardingKey> {
                OnboardingScreen(
                    onComplete = {
                        mainViewModel.completeOnboarding()
                        backStack.clear()
                        backStack.add(DashboardKey)
                        selectedTab = 0
                    },
                    onSkip = {
                        mainViewModel.completeOnboarding()
                        backStack.clear()
                        backStack.add(DashboardKey)
                        selectedTab = 0
                    }
                )
            }

            // ── Dashboard ─────────────────────────────────────────
            entry<DashboardKey> {
                DashboardScreen(
                    dashboardViewModel  = dashboardViewModel,
                    permissionsViewModel = permissionsViewModel,
                    selectedTab         = selectedTab,
                    onTabSelected       = onTabSelected
                )
            }

            // ── Focus ─────────────────────────────────────────────
            entry<FocusKey> {
                FocusScreen(
                    selectedTab   = selectedTab,
                    onTabSelected = onTabSelected
                )
            }

            // ── Settings ──────────────────────────────────────────
            entry<SettingsKey> {
                SettingsScreen(
                    selectedTab   = selectedTab,
                    onTabSelected = onTabSelected
                )
            }
        },

        // ── Forward transition (push) ─────────────────────────────
        transitionSpec = {
            (slideInHorizontally(
                initialOffsetX = { it },
                animationSpec  = tween(380, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(380, easing = FastOutSlowInEasing))) togetherWith
                    (slideOutHorizontally(
                        targetOffsetX = { -it / 3 },
                        animationSpec = tween(380, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(380, easing = FastOutSlowInEasing)))
        },

        // ── Back transition (pop) ─────────────────────────────────
        popTransitionSpec = {
            (slideInHorizontally(
                initialOffsetX = { -it / 3 },
                animationSpec  = tween(380, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(380, easing = FastOutSlowInEasing))) togetherWith
                    (slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(380, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(380, easing = FastOutSlowInEasing)))
        },

        // ── Predictive-back (Android 13+ gesture) ────────────────
        predictivePopTransitionSpec = {
            (slideInHorizontally(
                initialOffsetX = { -it / 3 },
                animationSpec  = tween(320, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(320, easing = FastOutSlowInEasing))) togetherWith
                    (slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(320, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(320, easing = FastOutSlowInEasing)))
        }
    )
}