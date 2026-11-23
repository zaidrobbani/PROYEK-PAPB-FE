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

@Serializable
data object ProfessionalProfileRoute

@Serializable
data object PortfolioRoute

@Serializable
data class PortfolioFormRoute(val portfolioId: String? = null)

@Serializable
data object ExperienceRoute

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
    onFormDone: () -> Unit
) {
    composable<ExperienceRoute> { ExperienceListContainer(onBack = onBack, onAddClick = onAddClick, onEditClick = onEditClick) }
    composable<ExperienceFormRoute> { entry ->
        ExperienceFormContainer(experienceId = entry.toRoute<ExperienceFormRoute>().experienceId, onBack = onBack, onDone = onFormDone)
    }
}
