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

@Serializable
data class ResultReviewRoute(val projectId: String)

@Serializable
data class ProjectCompletedRoute(val projectId: String)

@Serializable
data class FreelancerReviewRoute(val projectId: String)

fun NavController.navigateToResultReview(projectId: String, builder: NavOptionsBuilder.() -> Unit = {}) =
    navigate(ResultReviewRoute(projectId)) { launchSingleTop = true; builder() }

fun NavController.navigateToProjectCompleted(projectId: String, builder: NavOptionsBuilder.() -> Unit = {}) =
    navigate(ProjectCompletedRoute(projectId)) { launchSingleTop = true; builder() }

fun NavController.navigateToFreelancerReview(projectId: String, builder: NavOptionsBuilder.() -> Unit = {}) =
    navigate(FreelancerReviewRoute(projectId)) { launchSingleTop = true; builder() }

fun NavGraphBuilder.resultReviewScreens(
    onBack: () -> Unit,
    onOpenChat: (projectId: String) -> Unit,
    onCompleted: (projectId: String) -> Unit,
    onGiveReview: (projectId: String) -> Unit,
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
