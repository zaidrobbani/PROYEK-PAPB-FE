package com.example.proyek_papb_fe.feature.auth.register_freelancer.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.proyek_papb_fe.feature.auth.register_freelancer.container.RegisterFreelancerScreen
import kotlinx.serialization.Serializable

@Serializable
data object RegisterFreelancerRoute

fun NavController.navigateToRegisterFreelancer() {
    navigate(RegisterFreelancerRoute)
}

fun NavGraphBuilder.registerFreelancerScreen(
    onChangeRole: () -> Unit,
    onLogin: () -> Unit,
    onRegisterSuccess: (email: String) -> Unit
) {
    composable<RegisterFreelancerRoute> {
        RegisterFreelancerScreen(
            onChangeRole = onChangeRole,
            onLogin = onLogin,
            onRegisterSuccess = onRegisterSuccess
        )
    }
}
