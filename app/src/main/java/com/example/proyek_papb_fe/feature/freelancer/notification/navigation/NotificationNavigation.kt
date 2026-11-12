package com.example.proyek_papb_fe.feature.freelancer.notification.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.freelancer.notification.container.NotificationContainer
import com.example.proyek_papb_fe.feature.freelancer.revision.container.RevisionRequestContainer
import kotlinx.serialization.Serializable

/** Figma: Notifikasi (539:1407). Halaman detail dari Profil (tanpa bottom bar). */
@Serializable
data object NotificationRoute

/** Figma: Notifikasi Permintaan Revisi (Freelancer) (17:5737), untuk project [projectId]. */
@Serializable
data class RevisionRequestRoute(val projectId: Int)

fun NavController.navigateToNotifications() = navigate(NotificationRoute) { launchSingleTop = true }

fun NavController.navigateToRevisionRequest(projectId: Int) = navigate(RevisionRequestRoute(projectId)) { launchSingleTop = true }

fun NavGraphBuilder.notificationScreen(
    onBack: () -> Unit,
    onRevisionRequestClick: (projectId: Int) -> Unit,
    onMessageClick: (projectId: Int) -> Unit,
    onWalletClick: () -> Unit,
    onProfessionalProfileClick: () -> Unit,
    onFindProjectsClick: () -> Unit
) {
    composable<NotificationRoute> {
        NotificationContainer(
            onBack = onBack,
            onRevisionRequestClick = onRevisionRequestClick,
            onMessageClick = onMessageClick,
            onWalletClick = onWalletClick,
            onProfessionalProfileClick = onProfessionalProfileClick,
            onFindProjectsClick = onFindProjectsClick
        )
    }
}

fun NavGraphBuilder.revisionRequestScreen(
    onBack: () -> Unit,
    onOpenProjectChat: (projectId: Int) -> Unit
) {
    composable<RevisionRequestRoute> { entry ->
        RevisionRequestContainer(
            projectId = entry.toRoute<RevisionRequestRoute>().projectId,
            onBack = onBack,
            onOpenProjectChat = onOpenProjectChat
        )
    }
}
