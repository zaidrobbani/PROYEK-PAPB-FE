package com.example.proyek_papb_fe.core.navigation

import androidx.navigation.NavController
import com.example.proyek_papb_fe.feature.freelancer.dashboard.navigation.DashboardRoute

fun NavController.navigateToTopLevel(route: TopLevelRoute) {
    navigate(route) {
        popUpTo<DashboardRoute> {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
