package com.example.proyek_papb_fe.feature.umkm.freelancer_applications.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.umkm.freelancer_applications.container.FreelancerApplicationsScreen
import kotlinx.serialization.Serializable

@Serializable
data class FreelancerApplicationsRoute(val projectId: String)

fun NavController.navigateToFreelancerApplications(projectId: String) {
    navigate(FreelancerApplicationsRoute(projectId)) { launchSingleTop = true }
}

fun NavGraphBuilder.freelancerApplicationsScreen(
    onBack: () -> Unit,
    onViewProfile: (freelancerId: String, applicationId: String) -> Unit,
    onFreelancerSelected: (applicationId: String) -> Unit
) {
    composable<FreelancerApplicationsRoute> { entry ->
        FreelancerApplicationsScreen(
            projectId = entry.toRoute<FreelancerApplicationsRoute>().projectId,
            onBack = onBack,
            onViewProfile = onViewProfile,
            onFreelancerSelected = onFreelancerSelected
        )
    }
}
