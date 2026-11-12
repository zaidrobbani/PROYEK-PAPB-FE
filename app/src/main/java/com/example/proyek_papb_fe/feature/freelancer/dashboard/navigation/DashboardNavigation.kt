package com.example.proyek_papb_fe.feature.freelancer.dashboard.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.proyek_papb_fe.feature.freelancer.dashboard.container.DashboardContainer
import kotlinx.serialization.Serializable

@Serializable
data object DashboardRoute

fun NavGraphBuilder.dashboardScreen(
    onWalletClick: () -> Unit,
    onProjectClick: (Int) -> Unit
) {
    composable<DashboardRoute> {
        DashboardContainer(
            onWalletClick = onWalletClick,
            onProjectClick = onProjectClick
        )
    }
}
