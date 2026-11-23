package com.example.proyek_papb_fe.feature.umkm.planreview.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.umkm.planreview.container.PlanReviewContainer
import kotlinx.serialization.Serializable

@Serializable
data class PlanReviewRoute(val projectId: String)

fun NavController.navigateToPlanReview(projectId: String) = navigate(PlanReviewRoute(projectId)) { launchSingleTop = true }

fun NavGraphBuilder.planReviewScreen(
    onBack: () -> Unit,
    onApproved: (projectId: String) -> Unit
) {
    composable<PlanReviewRoute> { entry ->
        PlanReviewContainer(projectId = entry.toRoute<PlanReviewRoute>().projectId, onBack = onBack, onApproved = onApproved)
    }
}
