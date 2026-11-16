package com.example.proyek_papb_fe.feature.umkm.dashboard.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.proyek_papb_fe.feature.umkm.component.shared.UmkmTab
import com.example.proyek_papb_fe.feature.umkm.dashboard.container.UmkmDashboardScreen
import kotlinx.serialization.Serializable

@Serializable
data object UmkmDashboardRoute

/** Dipanggil setelah login; halaman Auth dilepas dari back stack agar back tidak kembali ke Login. */
fun NavController.navigateToUmkmDashboard() {
    navigate(UmkmDashboardRoute) {
        popUpTo(graph.id) { inclusive = true }
    }
}

/** Tab Beranda dari tab UMKM lain: kembali ke Beranda yang sudah ada di back stack. */
fun NavController.backToUmkmDashboard() {
    popBackStack(UmkmDashboardRoute, inclusive = false)
}

fun NavGraphBuilder.umkmDashboardScreen(
    onNotificationsClick: () -> Unit,
    onSearch: (query: String) -> Unit,
    onFilterClick: () -> Unit,
    onCreateProject: () -> Unit,
    onSeeAllProjects: () -> Unit,
    onProjectClick: (projectId: String) -> Unit,
    onSeeAllFreelancers: () -> Unit,
    onFreelancerClick: (freelancerId: String) -> Unit,
    onTabSelected: (UmkmTab) -> Unit
) {
    composable<UmkmDashboardRoute> {
        UmkmDashboardScreen(
            onNotificationsClick = onNotificationsClick,
            onSearch = onSearch,
            onFilterClick = onFilterClick,
            onCreateProject = onCreateProject,
            onSeeAllProjects = onSeeAllProjects,
            onProjectClick = onProjectClick,
            onSeeAllFreelancers = onSeeAllFreelancers,
            onFreelancerClick = onFreelancerClick,
            onTabSelected = onTabSelected
        )
    }
}
