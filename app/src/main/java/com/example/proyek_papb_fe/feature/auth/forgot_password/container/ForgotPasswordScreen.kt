package com.example.proyek_papb_fe.feature.auth.forgot_password.container

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthAppBar
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthPrimaryButton
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.forgot_password.component.BackToLoginLink
import com.example.proyek_papb_fe.feature.auth.forgot_password.component.ForgotPasswordEmailField
import com.example.proyek_papb_fe.feature.auth.forgot_password.component.ForgotPasswordHeader
import com.example.proyek_papb_fe.feature.auth.forgot_password.component.ForgotPasswordInfoCard

/** Blueprint tidak mengatur Lupa Password, jadi hanya validasi wajib diisi dan format email. */
internal fun validateForgotPasswordEmail(email: String): String? = when {
    email.isBlank() -> "Alamat email wajib diisi."
    !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> "Format email tidak valid."
    else -> null
}

/**
 * Hanya UI lokal. Tidak ada email yang benar-benar dikirim karena backend di luar scope;
 * [onResetLinkSent] menerima email yang lolos validasi untuk screen berikutnya.
 */
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onBackToLogin: () -> Unit,
    onResetLinkSent: (email: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var email by rememberSaveable { mutableStateOf("") }
    var submitted by rememberSaveable { mutableStateOf(false) }

    // Pesan muncul setelah tombol ditekan, lalu ikut hilang saat isian diperbaiki.
    val error = if (submitted) validateForgotPasswordEmail(email) else null

    val onSubmit: () -> Unit = {
        submitted = true
        if (validateForgotPasswordEmail(email) == null) {
            onResetLinkSent(email.trim())
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AuthAppBar(title = "Lupa Password", onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(start = 17.dp, end = 17.dp, top = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ForgotPasswordHeader()
            ForgotPasswordEmailField(
                value = email,
                onValueChange = { email = it },
                error = error,
                onSubmit = onSubmit
            )
            ForgotPasswordInfoCard()
            AuthPrimaryButton(
                text = "Kirim Tautan Reset",
                onClick = onSubmit,
                shadowStyle = AuthShadowStyle.None,
                height = 51.dp,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_symbol_send),
                        contentDescription = null,
                        modifier = Modifier.size(19.dp)
                    )
                }
            )
            BackToLoginLink(onClick = onBackToLogin)
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun ForgotPasswordScreenPreview() {
    PROYEKPAPBFETheme {
        ForgotPasswordScreen(
            onBack = {},
            onBackToLogin = {},
            onResetLinkSent = {}
        )
    }
}
