package com.example.proyek_papb_fe.feature.auth.reset_link_sent.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.proyek_papb_fe.feature.auth.forgot_password.navigation.ForgotPasswordRoute
import com.example.proyek_papb_fe.feature.auth.reset_link_sent.container.ResetLinkSentScreen
import kotlinx.serialization.Serializable

@Serializable
data class ResetLinkSentRoute(val email: String)

/** Lupa Password dilepas dari back stack, jadi tombol back sistem langsung kembali ke Login. */
fun NavController.navigateToResetLinkSent(email: String) {
    navigate(ResetLinkSentRoute(email)) {
        popUpTo(ForgotPasswordRoute) { inclusive = true }
    }
}

fun NavGraphBuilder.resetLinkSentScreen(onBackToLogin: () -> Unit) {
    composable<ResetLinkSentRoute> { entry ->
        ResetLinkSentScreen(
            email = entry.toRoute<ResetLinkSentRoute>().email,
            onBackToLogin = onBackToLogin
        )
    }
}
