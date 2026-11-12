package com.example.proyek_papb_fe.feature.umkm.review.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.umkm.review.container.FreelancerReviewContainer
import com.example.proyek_papb_fe.feature.umkm.review.container.ProjectCompletedContainer
import com.example.proyek_papb_fe.feature.umkm.review.container.ResultReviewContainer
import kotlinx.serialization.Serializable

/** Figma: Review Hasil Project (17:1468), sisi UMKM, untuk project [projectId]. */
@Serializable
data class ResultReviewRoute(val projectId: Int)

/** Figma: Sukses Penyelesaian Proyek (17:6338). */
@Serializable
data class ProjectCompletedRoute(val projectId: Int)

/** Figma: Beri Ulasan Freelancer (17:6591). */
@Serializable
data class FreelancerReviewRoute(val projectId: Int)

fun NavController.navigateToResultReview(projectId: Int, builder: NavOptionsBuilder.() -> Unit = {}) =
    navigate(ResultReviewRoute(projectId)) { launchSingleTop = true; builder() }

fun NavController.navigateToProjectCompleted(projectId: Int, builder: NavOptionsBuilder.() -> Unit = {}) =
    navigate(ProjectCompletedRoute(projectId)) { launchSingleTop = true; builder() }

fun NavController.navigateToFreelancerReview(projectId: Int, builder: NavOptionsBuilder.() -> Unit = {}) =
    navigate(FreelancerReviewRoute(projectId)) { launchSingleTop = true; builder() }

fun NavGraphBuilder.resultReviewScreens(
    onBack: () -> Unit,
    onOpenChat: (projectId: Int) -> Unit,
    /** Review disetujui: buka Proyek Selesai dan keluarkan Review Hasil dari back stack. */
    onCompleted: (projectId: Int) -> Unit,
    onGiveReview: (projectId: Int) -> Unit,
    /** Ulasan terkirim: kembali ke Proyek Selesai. */
    onReviewDone: () -> Unit,
    onBackToHome: () -> Unit
) {
    composable<ResultReviewRoute> { entry ->
        ResultReviewContainer(
            projectId = entry.toRoute<ResultReviewRoute>().projectId,
            onBack = onBack,
            onOpenChat = onOpenChat,
            onCompleted = onCompleted
        )
    }
    composable<ProjectCompletedRoute> { entry ->
        ProjectCompletedContainer(
            projectId = entry.toRoute<ProjectCompletedRoute>().projectId,
            onBack = onBack,
            onGiveReview = onGiveReview,
            onBackToHome = onBackToHome
        )
    }
    composable<FreelancerReviewRoute> { entry ->
        FreelancerReviewContainer(
            projectId = entry.toRoute<FreelancerReviewRoute>().projectId,
            onBack = onBack,
            onDone = onReviewDone,
            onSkip = onBackToHome
        )
    }
}
