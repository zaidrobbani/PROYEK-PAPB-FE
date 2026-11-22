package com.example.proyek_papb_fe.feature.umkm.find_freelancer.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.umkm.component.shared.UmkmTab
import com.example.proyek_papb_fe.feature.umkm.dashboard.navigation.UmkmDashboardRoute
import com.example.proyek_papb_fe.feature.umkm.find_freelancer.container.FindFreelancerScreen
import kotlinx.serialization.Serializable

@Serializable
data class FindFreelancerRoute(val query: String = "", val openFilter: Boolean = false)

fun NavController.navigateToFindFreelancer(query: String = "", openFilter: Boolean = false) {
    navigate(FindFreelancerRoute(query, openFilter)) {
        popUpTo(UmkmDashboardRoute)
        launchSingleTop = true
    }
}

fun NavGraphBuilder.findFreelancerScreen(
    onNotificationsClick: () -> Unit,
    onViewProfile: (freelancerId: String) -> Unit,
    onTabSelected: (UmkmTab) -> Unit
) {
    composable<FindFreelancerRoute> { entry ->
        val route = entry.toRoute<FindFreelancerRoute>()
        FindFreelancerScreen(
            initialQuery = route.query,
            initialShowFilter = route.openFilter,
            onNotificationsClick = onNotificationsClick,
            onViewProfile = onViewProfile,
            onTabSelected = onTabSelected
        )
    }
}
