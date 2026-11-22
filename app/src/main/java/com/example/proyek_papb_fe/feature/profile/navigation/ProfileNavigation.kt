package com.example.proyek_papb_fe.feature.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.proyek_papb_fe.core.navigation.KeepSizeWhileLeaving
import com.example.proyek_papb_fe.feature.profile.container.ProfileScreen
import kotlinx.serialization.Serializable

@Serializable
data object ProfileRoute

fun NavGraphBuilder.profileScreen(
    onProfessionalProfileClick: () -> Unit,
    onPortfolioClick: () -> Unit,
    onExperienceClick: () -> Unit,
    onWalletClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    composable<ProfileRoute> { backStackEntry ->
        KeepSizeWhileLeaving(entry = backStackEntry) {
            ProfileScreen(
                onProfessionalProfileClick = onProfessionalProfileClick,
                onPortfolioClick = onPortfolioClick,
                onExperienceClick = onExperienceClick,
                onWalletClick = onWalletClick,
                onNotificationsClick = onNotificationsClick,
                onLogoutClick = onLogoutClick
            )
        }
    }
}
