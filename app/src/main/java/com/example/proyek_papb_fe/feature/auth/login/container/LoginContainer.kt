package com.example.proyek_papb_fe.feature.auth.login.container

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.data.FreelancerProfileStore
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.feature.umkm.data.UmkmAccount
import com.example.proyek_papb_fe.feature.auth.login.viewmodel.LoginViewModel
import com.example.proyek_papb_fe.feature.auth.role_selection.container.UserRole

@Composable
fun LoginContainer(
    onLoginSuccess: (UserRole) -> Unit,
    onForgotPassword: () -> Unit,
    onRegister: () -> Unit,
    viewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.factory(AppContainer.authRepository)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.loggedInRole) {
        uiState.loggedInRole?.let { role ->
            viewModel.onNavigated()
            FreelancerProfileStore.clear()
            UmkmAccount.clear()
            onLoginSuccess(role)
        }
    }
    LaunchedEffect(uiState.infoMessage) {
        uiState.infoMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onInfoMessageShown()
        }
    }

    LoginScreen(
        uiState = uiState,
        onSubmit = viewModel::login,
        onInputChanged = viewModel::onInputChanged,
        onResendVerification = viewModel::resendVerification,
        onForgotPassword = onForgotPassword,
        onGoogleLogin = {
            Toast.makeText(context, "Login Google sedang tidak tersedia, silakan masuk dengan email", Toast.LENGTH_SHORT).show()
        },
        onRegister = onRegister
    )
}
