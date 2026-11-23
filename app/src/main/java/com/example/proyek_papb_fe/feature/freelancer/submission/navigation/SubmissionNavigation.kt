package com.example.proyek_papb_fe.feature.freelancer.submission.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.core.navigation.KeepSizeWhileLeaving
import com.example.proyek_papb_fe.feature.freelancer.submission.container.SubmissionScreen
import com.example.proyek_papb_fe.feature.freelancer.submission.container.SubmissionStatusContainer
import kotlinx.serialization.Serializable

@Serializable
data object SubmissionRoute

fun NavGraphBuilder.submissionScreen(
    onProposalClick: (projectId: String) -> Unit,
    onFindProjectsClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    composable<SubmissionRoute> { backStackEntry ->
        KeepSizeWhileLeaving(entry = backStackEntry) {
            SubmissionScreen(
                onProposalClick = onProposalClick,
                onFindProjectsClick = onFindProjectsClick,
                onNotificationClick = onNotificationClick
            )
        }
    }
}

@Serializable
data class SubmissionStatusRoute(val projectId: String)

fun NavController.navigateToSubmissionStatus(
    projectId: String,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(SubmissionStatusRoute(projectId)) {
        launchSingleTop = true
        builder()
    }
}

fun NavGraphBuilder.submissionStatusScreen(
    onBack: () -> Unit,
    onOpenNegotiation: (projectId: String) -> Unit,
    onFindOtherProjects: () -> Unit,
    onViewProjectDetail: (projectId: String) -> Unit,
    onOpenWorkPlan: (projectId: String) -> Unit = {}
) {
    composable<SubmissionStatusRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<SubmissionStatusRoute>()
        SubmissionStatusContainer(
            projectId = route.projectId,
            onBack = onBack,
            onOpenNegotiation = onOpenNegotiation,
            onFindOtherProjects = onFindOtherProjects,
            onViewProjectDetail = onViewProjectDetail,
            onOpenWorkPlan = onOpenWorkPlan
        )
    }
}
