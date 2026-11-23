package com.example.proyek_papb_fe.feature.freelancer.dashboard.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.proyek_papb_fe.feature.freelancer.dashboard.container.DashboardContainer
import kotlinx.serialization.Serializable

@Serializable
data object DashboardRoute

fun NavController.navigateToFreelancerDashboard() {
    navigate(DashboardRoute) {
        popUpTo(graph.id) { inclusive = true }
    }
}

fun NavGraphBuilder.dashboardScreen(
    onWalletClick: () -> Unit,
    onActiveProjectClick: (projectId: String) -> Unit,
    onSeeAllActiveProjectsClick: () -> Unit,
    onRecommendedProjectClick: (projectId: String) -> Unit,
    onSeeAllRecommendationsClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    composable<DashboardRoute> {
        DashboardContainer(
            onWalletClick = onWalletClick,
            onActiveProjectClick = onActiveProjectClick,
            onSeeAllActiveProjectsClick = onSeeAllActiveProjectsClick,
            onRecommendedProjectClick = onRecommendedProjectClick,
            onSeeAllRecommendationsClick = onSeeAllRecommendationsClick,
            onNotificationClick = onNotificationClick
        )
    }
}
