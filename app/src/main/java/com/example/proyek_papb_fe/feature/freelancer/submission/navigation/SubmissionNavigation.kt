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

/** Tab "Project": daftar project freelancer (dari penawaran terkirim), satu kartu per project. */
fun NavGraphBuilder.submissionScreen(
    /** Buka Status Pengajuan milik project kartu yang dipilih. */
    onProposalClick: (projectId: Int) -> Unit,
    onFindProjectsClick: () -> Unit
) {
    composable<SubmissionRoute> { backStackEntry ->
        // Jaga posisi scroll saat membuka Status Pengajuan (bottom bar hilang) dari bawah daftar.
        KeepSizeWhileLeaving(entry = backStackEntry) {
            SubmissionScreen(
                onProposalClick = onProposalClick,
                onFindProjectsClick = onFindProjectsClick
            )
        }
    }
}

/** Figma: Status Pengajuan (Menunggu Kesepakatan) (27:2), untuk satu project. */
@Serializable
data class SubmissionStatusRoute(val projectId: Int)

fun NavController.navigateToSubmissionStatus(
    projectId: Int,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(SubmissionStatusRoute(projectId)) {
        launchSingleTop = true
        builder()
    }
}

fun NavGraphBuilder.submissionStatusScreen(
    onBack: () -> Unit,
    onOpenNegotiation: (projectId: Int) -> Unit,
    onFindOtherProjects: () -> Unit,
    onViewProjectDetail: (projectId: Int) -> Unit
) {
    composable<SubmissionStatusRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<SubmissionStatusRoute>()
        SubmissionStatusContainer(
            projectId = route.projectId,
            onBack = onBack,
            onOpenNegotiation = onOpenNegotiation,
            onFindOtherProjects = onFindOtherProjects,
            onViewProjectDetail = onViewProjectDetail
        )
    }
}
