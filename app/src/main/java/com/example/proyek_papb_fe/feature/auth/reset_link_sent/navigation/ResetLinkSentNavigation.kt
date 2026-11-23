package com.example.proyek_papb_fe.feature.auth.reset_link_sent.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.auth.forgot_password.navigation.ForgotPasswordRoute
import com.example.proyek_papb_fe.feature.auth.reset_link_sent.container.ResetLinkSentScreen
import com.example.proyek_papb_fe.feature.auth.role_selection.navigation.RoleSelectionRoute
import kotlinx.serialization.Serializable

/** Layar "Cek email kamu" dipakai untuk tautan reset sandi dan tautan verifikasi akun baru. */
@Serializable
enum class EmailLinkPurpose { RESET_PASSWORD, VERIFY_EMAIL }

@Serializable
data class ResetLinkSentRoute(
    val email: String,
    val purpose: EmailLinkPurpose = EmailLinkPurpose.RESET_PASSWORD
)

fun NavController.navigateToResetLinkSent(email: String) {
    navigate(ResetLinkSentRoute(email)) {
        popUpTo(ForgotPasswordRoute) { inclusive = true }
    }
}

fun NavController.navigateToVerifyEmailSent(email: String) {
    navigate(ResetLinkSentRoute(email, EmailLinkPurpose.VERIFY_EMAIL)) {
        popUpTo(RoleSelectionRoute)
    }
}

fun NavGraphBuilder.resetLinkSentScreen(onBackToLogin: () -> Unit) {
    composable<ResetLinkSentRoute> { entry ->
        val route = entry.toRoute<ResetLinkSentRoute>()
        ResetLinkSentScreen(
            email = route.email,
            purpose = route.purpose,
            onBackToLogin = onBackToLogin
        )
    }
}
