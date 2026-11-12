package com.example.proyek_papb_fe.feature.freelancer.profilemanage.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.container.ExperienceFormContainer
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.container.ExperienceListContainer
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.container.PortfolioFormContainer
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.container.PortfolioListContainer
import com.example.proyek_papb_fe.feature.freelancer.profilemanage.container.ProfessionalProfileContainer
import kotlinx.serialization.Serializable

/** Figma: Kelola Profil Profesional (534:961). Halaman detail dari Profil (tanpa bottom bar). */
@Serializable
data object ProfessionalProfileRoute

/** Figma: Kelola Portofolio (535:1081). */
@Serializable
data object PortfolioRoute

/** Figma: Tambah Portofolio (535:1163) bila [portfolioId] null, Edit Portofolio (535:1222) bila diisi. */
@Serializable
data class PortfolioFormRoute(val portfolioId: String? = null)

/** Figma: Kelola Pengalaman (538:1235). */
@Serializable
data object ExperienceRoute

/** Figma: Tambah Pengalaman (538:1305) bila [experienceId] null, Edit Pengalaman (538:1363) bila diisi. */
@Serializable
data class ExperienceFormRoute(val experienceId: String? = null)

fun NavController.navigateToProfessionalProfile() = navigate(ProfessionalProfileRoute) { launchSingleTop = true }

fun NavController.navigateToPortfolio() = navigate(PortfolioRoute) { launchSingleTop = true }

fun NavController.navigateToPortfolioForm(portfolioId: String? = null) = navigate(PortfolioFormRoute(portfolioId)) { launchSingleTop = true }

fun NavController.navigateToExperience() = navigate(ExperienceRoute) { launchSingleTop = true }

fun NavController.navigateToExperienceForm(experienceId: String? = null) = navigate(ExperienceFormRoute(experienceId)) { launchSingleTop = true }

fun NavGraphBuilder.professionalProfileScreen(onBack: () -> Unit) {
    composable<ProfessionalProfileRoute> { ProfessionalProfileContainer(onBack = onBack) }
}

fun NavGraphBuilder.portfolioScreens(
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (String) -> Unit,
    /** Setelah simpan/hapus di form: kembali ke Kelola Portofolio. */
    onFormDone: () -> Unit
) {
    composable<PortfolioRoute> { PortfolioListContainer(onBack = onBack, onAddClick = onAddClick, onEditClick = onEditClick) }
    composable<PortfolioFormRoute> { entry ->
        PortfolioFormContainer(portfolioId = entry.toRoute<PortfolioFormRoute>().portfolioId, onBack = onBack, onDone = onFormDone)
    }
}

fun NavGraphBuilder.experienceScreens(
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (String) -> Unit,
    /** Setelah simpan/hapus di form: kembali ke Kelola Pengalaman. */
    onFormDone: () -> Unit
) {
    composable<ExperienceRoute> { ExperienceListContainer(onBack = onBack, onAddClick = onAddClick, onEditClick = onEditClick) }
    composable<ExperienceFormRoute> { entry ->
        ExperienceFormContainer(experienceId = entry.toRoute<ExperienceFormRoute>().experienceId, onBack = onBack, onDone = onFormDone)
    }
}
