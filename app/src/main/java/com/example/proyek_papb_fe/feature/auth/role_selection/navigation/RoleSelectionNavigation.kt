package com.example.proyek_papb_fe.feature.auth.role_selection.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.proyek_papb_fe.feature.auth.role_selection.container.RoleSelectionScreen
import kotlinx.serialization.Serializable

@Serializable
data object RoleSelectionRoute

fun NavController.navigateToRoleSelection() {
    navigate(RoleSelectionRoute)
}

/** Kembali ke Pilih Peran yang sudah ada di back stack, tanpa menumpuk salinan baru. */
fun NavController.navigateBackToRoleSelection() {
    navigate(RoleSelectionRoute) {
        popUpTo(RoleSelectionRoute)
        launchSingleTop = true
    }
}

fun NavGraphBuilder.roleSelectionScreen(
    onRegisterUmkm: () -> Unit,
    onRegisterFreelancer: () -> Unit,
    onLogin: () -> Unit
) {
    composable<RoleSelectionRoute> {
        RoleSelectionScreen(
            onRegisterUmkm = onRegisterUmkm,
            onRegisterFreelancer = onRegisterFreelancer,
            onLogin = onLogin
        )
    }
}
