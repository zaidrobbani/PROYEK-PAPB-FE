package com.example.proyek_papb_fe.feature.umkm.freelancer_profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.umkm.freelancer_profile.container.FreelancerProfileScreen
import kotlinx.serialization.Serializable

@Serializable
data class UmkmFreelancerProfileRoute(val freelancerId: String, val applicationId: String? = null)

fun NavController.navigateToUmkmFreelancerProfile(freelancerId: String, applicationId: String? = null) {
    navigate(UmkmFreelancerProfileRoute(freelancerId, applicationId)) { launchSingleTop = true }
}

fun NavGraphBuilder.umkmFreelancerProfileScreen(
    onBack: () -> Unit,
    onSelected: () -> Unit,
    onOpenApplications: (projectId: String) -> Unit
) {
    composable<UmkmFreelancerProfileRoute> { entry ->
        val route = entry.toRoute<UmkmFreelancerProfileRoute>()
        FreelancerProfileScreen(
            freelancerId = route.freelancerId,
            applicationId = route.applicationId,
            onBack = onBack,
            onSelected = onSelected,
            onOpenApplications = onOpenApplications
        )
    }
}
