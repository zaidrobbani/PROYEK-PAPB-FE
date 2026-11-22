package com.example.proyek_papb_fe.feature.auth.login.container

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthPrimaryButton
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthPromptLink
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthShadowStyle
import com.example.proyek_papb_fe.feature.auth.component.shared.RegisterFieldIcon
import com.example.proyek_papb_fe.feature.auth.component.shared.RegisterTextField
import com.example.proyek_papb_fe.feature.auth.component.shared.authDropShadow
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.auth.login.component.LoginAlternativeOption
import com.example.proyek_papb_fe.feature.auth.login.component.LoginErrorCallout
import com.example.proyek_papb_fe.feature.auth.login.component.LoginFieldErrorIcon
import com.example.proyek_papb_fe.feature.auth.login.component.LoginFieldIssueLabel
import com.example.proyek_papb_fe.feature.auth.login.component.LoginHeader
import com.example.proyek_papb_fe.feature.auth.login.component.LoginPasswordToggle
import com.example.proyek_papb_fe.feature.auth.login.viewmodel.LoginUiState

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onSubmit: (email: String, password: String) -> Unit,
    onInputChanged: () -> Unit,
    onResendVerification: (email: String) -> Unit,
    onForgotPassword: () -> Unit,
    onGoogleLogin: () -> Unit,
    onRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var submitted by rememberSaveable { mutableStateOf(false) }

    val errors = if (submitted) validateLogin(email, password) else LoginErrors()
    val emailError = errors.email ?: uiState.fieldErrors["email"]
    val passwordError = errors.password ?: uiState.fieldErrors["password"] ?: uiState.errorMessage

    val submit: () -> Unit = {
        submitted = true
        if (!validateLogin(email, password).hasError) onSubmit(email, password)
    }

    val colors = MaterialTheme.colorScheme
    val cardShape = RoundedCornerShape(16.dp)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.903.dp, end = 16.903.dp, bottom = 25.354.dp)
    ) {
        LoginHeader(modifier = Modifier.padding(top = 12.677.dp, bottom = 25.354.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .authDropShadow(cardShape, AuthShadowStyle.Subtle)
                .background(colors.surfaceContainerLow, cardShape)
                .padding(16.903.dp),
            verticalArrangement = Arrangement.spacedBy(16.903.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.677.dp)
            ) {
                RegisterTextField(
                    label = "Alamat Email",
                    value = email,
                    onValueChange = {
                        email = it
                        onInputChanged()
                    },
                    error = emailError,
                    labelTrailing = if (emailError != null) {
                        { LoginFieldIssueLabel() }
                    } else {
                        null
                    },
                    leadingContent = { MailIcon() },
                    trailingContent = { if (emailError != null) LoginFieldErrorIcon() },
                    textEndPadding = 42.256.dp,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    withShadow = false,
                    borderColor = colors.outlineVariant,
                    textColor = colors.onSurfaceVariant,
                    errorContent = { LoginErrorCallout(message = it) }
                )
                RegisterTextField(
                    label = "Kata Sandi",
                    value = password,
                    onValueChange = {
                        password = it
                        onInputChanged()
                    },
                    error = passwordError,
                    labelTrailing = { ForgotPasswordLink(onClick = onForgotPassword) },
                    leadingContent = { LockIcon() },
                    trailingContent = {
                        LoginPasswordToggle(
                            visible = passwordVisible,
                            onToggle = { passwordVisible = !passwordVisible }
                        )
                    },
                    textEndPadding = 50.708.dp,
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { submit() }),
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    withShadow = false,
                    errorContent = { LoginErrorCallout(message = it) }
                )
                AuthPrimaryButton(
                    text = "Masuk",
                    onClick = submit,
                    modifier = Modifier.padding(top = 4.226.dp),
                    shadowStyle = AuthShadowStyle.Subtle,
                    isLoading = uiState.isLoading
                )
                if (uiState.needsEmailVerification) {
                    ResendVerificationLink(
                        isSending = uiState.isResendingVerification,
                        onClick = { onResendVerification(email) },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
            LoginAlternativeOption(onGoogleClick = onGoogleLogin)
        }
        AuthPromptLink(
            prompt = "Belum punya akun?",
            action = "Daftar",
            onClick = onRegister,
            actionColor = colors.primary,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.903.dp),
            spacing = 6.338.dp
        )
        Spacer(Modifier.height(25.354.dp + 38.031.dp))
    }
}

@Composable
private fun ForgotPasswordLink(onClick: () -> Unit) {
    Text(
        text = "Lupa Password?",
        modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelSmall.withFrameMetrics(
            fontWeight = FontWeight.Medium,
            fontSize = 11.621.sp,
            lineHeight = 14.79.sp
        )
    )
}

@Composable
private fun ResendVerificationLink(
    isSending: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = if (isSending) "Mengirim tautan verifikasi..." else "Kirim ulang email verifikasi",
        modifier = modifier.clickable(enabled = !isSending, role = Role.Button, onClick = onClick),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelSmall.withFrameMetrics(
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.677.sp,
            lineHeight = 16.903.sp
        )
    )
}

@Composable
private fun BoxScope.MailIcon() {
    RegisterFieldIcon(R.drawable.ic_register_mail, 18.66.dp, 18.31.dp, 16.667.dp, 13.333.dp)
}

@Composable
private fun BoxScope.LockIcon() {
    RegisterFieldIcon(R.drawable.ic_login_lock, 20.42.dp, 15.67.dp, 13.333.dp, 17.5.dp)
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun LoginScreenPreview() {
    PROYEKPAPBFETheme {
        LoginScreen(
            uiState = LoginUiState(),
            onSubmit = { _, _ -> },
            onInputChanged = {},
            onResendVerification = {},
            onForgotPassword = {},
            onGoogleLogin = {},
            onRegister = {}
        )
    }
}
