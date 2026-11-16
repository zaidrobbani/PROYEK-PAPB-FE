package com.example.proyek_papb_fe.feature.auth.register_umkm.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.proyek_papb_fe.feature.auth.register_umkm.container.RegisterUmkmScreen
import kotlinx.serialization.Serializable

@Serializable
data object RegisterUmkmRoute

fun NavController.navigateToRegisterUmkm() {
    navigate(RegisterUmkmRoute)
}

fun NavGraphBuilder.registerUmkmScreen(
    onChangeRole: () -> Unit,
    onLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    composable<RegisterUmkmRoute> {
        RegisterUmkmScreen(
            onChangeRole = onChangeRole,
            onLogin = onLogin,
            onRegisterSuccess = onRegisterSuccess
        )
    }
}
