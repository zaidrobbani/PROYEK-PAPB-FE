package com.example.proyek_papb_fe.feature.auth.change_password.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.auth.change_password.container.ChangePasswordScreen
import kotlinx.serialization.Serializable

/** [email] akun yang sedang masuk; dipakai bersama UMKM dan Freelancer. */
@Serializable
data class ChangePasswordRoute(val email: String)

fun NavController.navigateToChangePassword(email: String) {
    navigate(ChangePasswordRoute(email)) { launchSingleTop = true }
}

fun NavGraphBuilder.changePasswordScreen(
    onBack: () -> Unit,
    onForgotPassword: () -> Unit,
    onChanged: () -> Unit
) {
    composable<ChangePasswordRoute> { entry ->
        ChangePasswordScreen(
            email = entry.toRoute<ChangePasswordRoute>().email,
            onBack = onBack,
            onForgotPassword = onForgotPassword,
            onChanged = onChanged
        )
    }
}
