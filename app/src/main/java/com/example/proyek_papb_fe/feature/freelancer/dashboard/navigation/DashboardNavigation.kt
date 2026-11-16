package com.example.proyek_papb_fe.feature.freelancer.dashboard.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.proyek_papb_fe.feature.freelancer.dashboard.container.DashboardContainer
import kotlinx.serialization.Serializable

@Serializable
data object DashboardRoute

fun NavGraphBuilder.dashboardScreen(
    onWalletClick: () -> Unit,
    /** Kartu "Proyek Aktif Saya": buka halaman proyek milik projectId ini. */
    onActiveProjectClick: (projectId: Int) -> Unit,
    /** "Lihat Semua" di "Proyek Aktif Saya". */
    onSeeAllActiveProjectsClick: () -> Unit,
    /** Kartu "Rekomendasi Proyek": buka Detail Proyek projectId ini. */
    onRecommendedProjectClick: (projectId: Int) -> Unit,
    /** "Lihat Semua" di "Rekomendasi Proyek". */
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
