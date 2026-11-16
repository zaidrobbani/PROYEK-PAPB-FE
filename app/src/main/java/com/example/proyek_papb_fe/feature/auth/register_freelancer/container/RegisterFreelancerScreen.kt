package com.example.proyek_papb_fe.feature.auth.register_freelancer.container

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.auth.component.shared.RegisterForm
import com.example.proyek_papb_fe.feature.auth.component.shared.rememberRegisterFormState

@Composable
fun RegisterFreelancerScreen(
    onChangeRole: () -> Unit,
    onLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    RegisterForm(
        state = rememberRegisterFormState(),
        roleTitle = "Peran: Freelancer Digital",
        roleDescription = "Menawarkan jasa kreatif & digital",
        emailLabel = "Email Aktif",
        emailRequiredMessage = "Email aktif wajib diisi.",
        onChangeRole = onChangeRole,
        onLogin = onLogin,
        onRegisterSuccess = onRegisterSuccess,
        modifier = modifier
    )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 1180)
@Composable
private fun RegisterFreelancerScreenPreview() {
    PROYEKPAPBFETheme {
        RegisterFreelancerScreen(
            onChangeRole = {},
            onLogin = {},
            onRegisterSuccess = {}
        )
    }
}
