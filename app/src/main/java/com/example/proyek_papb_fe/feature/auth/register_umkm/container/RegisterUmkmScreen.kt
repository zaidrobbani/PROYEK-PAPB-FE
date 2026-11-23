package com.example.proyek_papb_fe.feature.auth.register_umkm.container

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.session.AccountRole
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.auth.component.shared.RegisterForm
import com.example.proyek_papb_fe.feature.auth.component.shared.rememberRegisterFormState
import com.example.proyek_papb_fe.feature.auth.register.viewmodel.RegisterViewModel

@Composable
fun RegisterUmkmScreen(
    onChangeRole: () -> Unit,
    onLogin: () -> Unit,
    onRegisterSuccess: (email: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModel.factory(AppContainer.authRepository, AccountRole.UMKM)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.registeredEmail) {
        uiState.registeredEmail?.let { email ->
            viewModel.onNavigated()
            onRegisterSuccess(email)
        }
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onErrorShown()
        }
    }

    RegisterForm(
        state = rememberRegisterFormState(),
        roleTitle = "Peran: Pemilik UMKM",
        roleDescription = "Mencari talenta digital terkurasi",
        emailLabel = "Email Bisnis",
        emailRequiredMessage = "Email bisnis wajib diisi.",
        onChangeRole = onChangeRole,
        onLogin = onLogin,
        onSubmit = { input ->
            viewModel.register(
                fullName = input.fullName,
                email = input.email,
                whatsapp = input.whatsapp,
                password = input.password,
                confirmPassword = input.confirmPassword,
                termsAccepted = input.termsAccepted
            )
        },
        serverErrors = uiState.fieldErrors,
        isLoading = uiState.isLoading,
        onInputChanged = viewModel::onInputChanged,
        modifier = modifier
    )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 1180)
@Composable
private fun RegisterUmkmScreenPreview() {
    PROYEKPAPBFETheme {
        RegisterForm(
            state = rememberRegisterFormState(),
            roleTitle = "Peran: Pemilik UMKM",
            roleDescription = "Mencari talenta digital terkurasi",
            emailLabel = "Email Bisnis",
            emailRequiredMessage = "Email bisnis wajib diisi.",
            onChangeRole = {},
            onLogin = {},
            onSubmit = {}
        )
    }
}
