package com.example.proyek_papb_fe.feature.auth.change_password.container

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.auth.change_password.component.ForgotCurrentPasswordLink
import com.example.proyek_papb_fe.feature.auth.change_password.component.PasswordInputField
import com.example.proyek_papb_fe.feature.auth.change_password.component.SecurityAccountCard
import com.example.proyek_papb_fe.feature.auth.change_password.component.SecurityInfoCard
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthAppBar
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthPrimaryButton
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics

internal data class ChangePasswordErrors(
    val current: String? = null,
    val new: String? = null,
    val confirm: String? = null
) {
    val hasError: Boolean get() = current != null || new != null || confirm != null
}

internal fun validateChangePassword(current: String, new: String, confirm: String) = ChangePasswordErrors(
    current = "Kata sandi saat ini wajib diisi.".takeIf { current.isEmpty() },
    new = when {
        new.isEmpty() -> "Kata sandi baru wajib diisi."
        new == current -> "Kata sandi baru harus berbeda dari kata sandi saat ini."
        else -> null
    },
    confirm = when {
        confirm.isEmpty() -> "Konfirmasi kata sandi wajib diisi."
        confirm != new -> "Konfirmasi kata sandi tidak sama."
        else -> null
    }
)

@Composable
fun ChangePasswordScreen(
    email: String,
    onBack: () -> Unit,
    onForgotPassword: () -> Unit,
    onChanged: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var current by rememberSaveable { mutableStateOf("") }
    var new by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    var currentVisible by rememberSaveable { mutableStateOf(false) }
    var newVisible by rememberSaveable { mutableStateOf(false) }
    var submitted by rememberSaveable { mutableStateOf(false) }
    val errors = if (submitted) validateChangePassword(current, new, confirm) else ChangePasswordErrors()
    val currentError = errors.current

    val onSubmit: () -> Unit = {
        submitted = true
        if (!validateChangePassword(current, new, confirm).hasError) {
            Toast.makeText(
                context,
                "Ubah kata sandi belum tersedia di server. Gunakan \"Lupa kata sandi?\" untuk mengatur ulang lewat email.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AuthAppBar(title = "Keamanan & Kata Sandi", onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(start = 17.dp, end = 17.dp, top = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SecurityAccountCard(email)
            Text(
                text = "Ubah Kata Sandi",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium.withFrameMetrics(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.903.sp,
                    lineHeight = 23.241.sp
                )
            )
            PasswordInputField(
                label = "Kata Sandi Saat Ini",
                value = current,
                onValueChange = { current = it },
                error = currentError,
                visible = currentVisible,
                onToggleVisible = { currentVisible = !currentVisible },
                imeAction = ImeAction.Next,
                labelTrailing = { ForgotCurrentPasswordLink(onClick = onForgotPassword) }
            )
            PasswordInputField(
                label = "Kata Sandi Baru",
                value = new,
                onValueChange = { new = it },
                error = errors.new,
                visible = newVisible,
                onToggleVisible = { newVisible = !newVisible },
                imeAction = ImeAction.Next
            )
            PasswordInputField(
                label = "Konfirmasi Kata Sandi Baru",
                value = confirm,
                onValueChange = { confirm = it },
                error = errors.confirm,
                visible = newVisible,
                onToggleVisible = { newVisible = !newVisible },
                imeAction = ImeAction.Done,
                showMatch = confirm.isNotEmpty() && confirm == new,
                onDone = onSubmit
            )
            SecurityInfoCard(
                "Lupa kata sandi saat ini? Gunakan \"Lupa kata sandi?\" untuk menerima tautan reset " +
                    "lewat email akun."
            )
            AuthPrimaryButton(
                text = "Simpan Kata Sandi Baru",
                onClick = onSubmit,
                shadowStyle = AuthShadowStyle.None,
                height = 51.dp
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun ChangePasswordScreenPreview() {
    PROYEKPAPBFETheme {
        ChangePasswordScreen(
            email = "rina.pratiwi@kulinerbandung.id",
            onBack = {},
            onForgotPassword = {},
            onChanged = {}
        )
    }
}
