package com.example.proyek_papb_fe.feature.umkm.dashboard.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.proyek_papb_fe.feature.umkm.component.shared.UmkmTab
import com.example.proyek_papb_fe.feature.umkm.dashboard.container.UmkmDashboardScreen
import kotlinx.serialization.Serializable

@Serializable
data object UmkmDashboardRoute

fun NavController.navigateToUmkmDashboard() {
    navigate(UmkmDashboardRoute) {
        popUpTo(graph.id) { inclusive = true }
    }
}

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
