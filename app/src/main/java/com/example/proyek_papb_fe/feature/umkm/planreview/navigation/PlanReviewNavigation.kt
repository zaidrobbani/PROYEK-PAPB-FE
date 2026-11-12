package com.example.proyek_papb_fe.feature.umkm.planreview.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.umkm.planreview.container.PlanReviewContainer
import kotlinx.serialization.Serializable

/** Figma: Tinjau Rencana Pengerjaan (UMKM) (270:663) untuk project [projectId]. */
@Serializable
data class PlanReviewRoute(val projectId: Int)

fun NavController.navigateToPlanReview(projectId: Int) = navigate(PlanReviewRoute(projectId)) { launchSingleTop = true }

fun NavGraphBuilder.planReviewScreen(
    onBack: () -> Unit,
    /** "Setujui Rencana" berhasil: buka Pembayaran (17:1267). */
    onApproved: (projectId: Int) -> Unit
) {
    composable<PlanReviewRoute> { entry ->
        PlanReviewContainer(projectId = entry.toRoute<PlanReviewRoute>().projectId, onBack = onBack, onApproved = onApproved)
    }
}
