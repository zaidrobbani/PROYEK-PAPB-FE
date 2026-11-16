package com.example.proyek_papb_fe.feature.auth.register_umkm.container

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.auth.component.shared.RegisterForm
import com.example.proyek_papb_fe.feature.auth.component.shared.rememberRegisterFormState

@Composable
fun RegisterUmkmScreen(
    onChangeRole: () -> Unit,
    onLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    RegisterForm(
        state = rememberRegisterFormState(),
        roleTitle = "Peran: Pemilik UMKM",
        roleDescription = "Mencari talenta digital terkurasi",
        emailLabel = "Email Bisnis",
        emailRequiredMessage = "Email bisnis wajib diisi.",
        onChangeRole = onChangeRole,
        onLogin = onLogin,
        onRegisterSuccess = onRegisterSuccess,
        modifier = modifier
    )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 1180)
@Composable
private fun RegisterUmkmScreenPreview() {
    PROYEKPAPBFETheme {
        RegisterUmkmScreen(
            onChangeRole = {},
            onLogin = {},
            onRegisterSuccess = {}
        )
    }
}
