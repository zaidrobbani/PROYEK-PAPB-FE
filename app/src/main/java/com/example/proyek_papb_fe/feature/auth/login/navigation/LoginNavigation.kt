package com.example.proyek_papb_fe.feature.auth.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.proyek_papb_fe.feature.auth.login.container.LoginContainer
import com.example.proyek_papb_fe.feature.auth.role_selection.container.UserRole
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute

fun NavController.navigateToLogin() {
    navigate(LoginRoute) {
        popUpTo(LoginRoute)
        launchSingleTop = true
    }
}

fun NavGraphBuilder.loginScreen(
    onLoginSuccess: (UserRole) -> Unit,
    onForgotPassword: () -> Unit,
    onRegister: () -> Unit
) {
    composable<LoginRoute> {
        LoginContainer(
            onLoginSuccess = onLoginSuccess,
            onForgotPassword = onForgotPassword,
            onRegister = onRegister
        )
    }
}
