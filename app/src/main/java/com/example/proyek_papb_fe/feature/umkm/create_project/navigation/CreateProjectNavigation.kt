package com.example.proyek_papb_fe.feature.umkm.create_project.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.umkm.create_project.container.CreateProjectDraft
import com.example.proyek_papb_fe.feature.umkm.create_project.container.CreateProjectScreen
import kotlinx.serialization.Serializable

/**
 * [projectId] terisi = mode Edit Project untuk project tersebut. [copyFromProjectId] terisi =
 * Pesan Lagi: form Buat Project diisi data pesanan sebelumnya, lalu dipublikasikan sebagai
 * project baru.
 */
@Serializable
data class CreateProjectRoute(val projectId: String? = null, val copyFromProjectId: String? = null)

fun NavController.navigateToCreateProject() {
    navigate(CreateProjectRoute()) { launchSingleTop = true }
}

fun NavController.navigateToOrderAgain(sourceProjectId: String) {
    navigate(CreateProjectRoute(copyFromProjectId = sourceProjectId)) { launchSingleTop = true }
}

fun NavController.navigateToEditProject(projectId: String) {
    navigate(CreateProjectRoute(projectId)) { launchSingleTop = true }
}

fun NavGraphBuilder.createProjectScreen(
    onBack: () -> Unit,
    onSaveDraft: () -> Unit,
    onPublished: (CreateProjectDraft) -> Unit
) {
    composable<CreateProjectRoute> { entry ->
        val route = entry.toRoute<CreateProjectRoute>()
        CreateProjectScreen(
            editProjectId = route.projectId,
            copyFromProjectId = route.copyFromProjectId,
            onBack = onBack,
            onSaveDraft = onSaveDraft,
            onPublished = onPublished
        )
    }
}
