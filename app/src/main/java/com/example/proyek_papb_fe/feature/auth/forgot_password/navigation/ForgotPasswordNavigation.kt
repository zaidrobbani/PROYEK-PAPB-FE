package com.example.proyek_papb_fe.feature.auth.forgot_password.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.proyek_papb_fe.feature.auth.forgot_password.container.ForgotPasswordScreen
import kotlinx.serialization.Serializable

@Serializable
data object ForgotPasswordRoute

fun NavController.navigateToForgotPassword() {
    navigate(ForgotPasswordRoute)
}

fun NavGraphBuilder.forgotPasswordScreen(
    onBack: () -> Unit,
    onBackToLogin: () -> Unit,
    onResetLinkSent: (email: String) -> Unit
) {
    composable<ForgotPasswordRoute> {
        ForgotPasswordScreen(
            onBack = onBack,
            onBackToLogin = onBackToLogin,
            onResetLinkSent = onResetLinkSent
        )
    }
}
