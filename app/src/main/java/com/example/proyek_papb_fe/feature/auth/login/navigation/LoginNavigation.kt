package com.example.proyek_papb_fe.feature.auth.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.proyek_papb_fe.feature.auth.login.container.LoginScreen
import com.example.proyek_papb_fe.feature.auth.role_selection.container.UserRole
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute

/** Bila Login sudah ada di back stack, kembali ke sana alih-alih menumpuk salinan baru. */
fun NavController.navigateToLogin() {
    navigate(LoginRoute) {
        popUpTo(LoginRoute)
        launchSingleTop = true
    }
}

fun NavGraphBuilder.loginScreen(
    onLoginSuccess: (UserRole) -> Unit,
    onForgotPassword: () -> Unit,
    onGoogleLogin: () -> Unit,
    onRegister: () -> Unit
) {
    composable<LoginRoute> {
        LoginScreen(
            onLoginSuccess = onLoginSuccess,
            onForgotPassword = onForgotPassword,
            onGoogleLogin = onGoogleLogin,
            onRegister = onRegister
        )
    }
}
