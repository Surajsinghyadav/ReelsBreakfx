package com.practice.reelbreak

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.practice.reelbreak.ui.navigation.AppNavigation
import com.practice.reelbreak.ui.navigation.DashboardKey
import com.practice.reelbreak.ui.navigation.OnboardingKey
import com.practice.reelbreak.ui.theme.LocalAppColors
import com.practice.reelbreak.viewmodel.DashboardViewModel
import com.practice.reelbreak.viewmodel.MainViewModel

@Composable
fun ReelsBreakApp(
    mainViewModel: MainViewModel,
    dashboardViewModel: DashboardViewModel
) {
    val isOnboardingCompleted by mainViewModel.isOnboardingCompleted.collectAsState()
    val colors = LocalAppColors.current

    when (isOnboardingCompleted) {
        // Still loading from DataStore – show blank splash
        null -> Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
        )

        // First launch → Onboarding
        false -> AppNavigation(
            startDestination  = OnboardingKey,
            mainViewModel     = mainViewModel,
            dashboardViewModel = dashboardViewModel
        )

        // Returning user → Dashboard
        true -> AppNavigation(
            startDestination  = DashboardKey,
            mainViewModel     = mainViewModel,
            dashboardViewModel = dashboardViewModel
        )
    }
}