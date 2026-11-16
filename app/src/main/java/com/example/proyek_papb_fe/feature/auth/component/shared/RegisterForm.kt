package com.example.proyek_papb_fe.feature.auth.component.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.R
import kotlinx.coroutines.launch

/** Tata letak form Registrasi yang sama pada frame UMKM dan Freelancer. */
@Composable
internal fun RegisterForm(
    state: RegisterFormState,
    roleTitle: String,
    roleDescription: String,
    emailLabel: String,
    emailRequiredMessage: String,
    onChangeRole: () -> Unit,
    onLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val errors = state.visibleErrors(emailRequiredMessage)
    val passwordTransformation =
        if (state.passwordVisible) VisualTransformation.None else PasswordVisualTransformation()

    val onSubmit: () -> Unit = {
        val result = state.submit(emailRequiredMessage)
        when {
            !result.hasError -> onRegisterSuccess()
            result.hasFieldError -> scope.launch { scrollState.animateScrollTo(0) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
            .verticalScroll(scrollState)
            .padding(start = 16.903.dp, end = 16.903.dp, bottom = 25.354.dp)
    ) {
        RegisterIntroCard(modifier = Modifier.padding(top = 12.677.dp, bottom = 16.903.dp))
        RegisterRoleChip(
            roleTitle = roleTitle,
            roleDescription = roleDescription,
            onChangeRole = onChangeRole,
            modifier = Modifier.padding(bottom = 16.903.dp)
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.677.dp)
        ) {
            RegisterTextField(
                label = "Nama Lengkap",
                value = state.fullName,
                onValueChange = { state.fullName = it },
                error = errors.fullName,
                labelTrailing = { RegisterFieldHint(text = "Sesuai KTP") },
                leadingContent = { PersonIcon() },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )
            RegisterTextField(
                label = emailLabel,
                value = state.email,
                onValueChange = { state.email = it },
                error = errors.email,
                leadingContent = { MailIcon() },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )
            RegisterTextField(
                label = "Nomor WhatsApp",
                value = state.whatsapp,
                onValueChange = { state.whatsapp = it },
                error = errors.whatsapp,
                labelTrailing = {
                    RegisterFieldHint(
                        text = "Notifikasi Proyek",
                        iconRes = R.drawable.ic_register_lock_small
                    )
                },
                leadingContent = { WhatsappPrefix() },
                textStartPadding = 84.513.dp,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            )
            RegisterTextField(
                label = "Kata Sandi",
                value = state.password,
                onValueChange = { state.password = it },
                error = errors.password,
                leadingContent = { PasswordIcon() },
                trailingContent = {
                    RegisterPasswordToggle(
                        visible = state.passwordVisible,
                        onToggle = { state.passwordVisible = !state.passwordVisible }
                    )
                },
                textEndPadding = 50.708.dp,
                keyboardOptions = passwordKeyboardOptions(ImeAction.Next),
                visualTransformation = passwordTransformation
            )
            RegisterTextField(
                label = "Konfirmasi Kata Sandi",
                value = state.confirmPassword,
                onValueChange = { state.confirmPassword = it },
                error = errors.confirmPassword,
                leadingContent = { KeyIcon() },
                trailingContent = {
                    if (state.passwordsMatch) {
                        RegisterPasswordMatchIcon()
                    }
                },
                textEndPadding = 46.482.dp,
                keyboardOptions = passwordKeyboardOptions(ImeAction.Done),
                visualTransformation = passwordTransformation
            )
            Spacer(Modifier.height(102.579.dp))
            RegisterTermsCheckbox(
                checked = state.termsAccepted,
                onCheckedChange = { state.termsAccepted = it },
                error = errors.terms
            )
            AuthPrimaryButton(
                text = "Daftar Sekarang",
                onClick = onSubmit,
                modifier = Modifier.padding(top = 4.226.dp)
            )
        }
        AuthLoginLink(
            onClick = onLogin,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.903.dp, bottom = 8.451.dp),
            linkFontSize = 14.79.sp,
            linkLineHeight = 21.128.sp,
            verticalPadding = 0.dp
        )
        RegisterTalentShowcase(modifier = Modifier.padding(top = 12.677.dp))
    }
}

private fun passwordKeyboardOptions(imeAction: ImeAction) = KeyboardOptions(
    autoCorrectEnabled = false,
    keyboardType = KeyboardType.Password,
    imeAction = imeAction
)

@Composable
private fun BoxScope.PersonIcon() {
    RegisterFieldIcon(R.drawable.ic_register_person, 17.96.dp, 19.02.dp, 12.dp, 12.dp)
}

@Composable
private fun BoxScope.MailIcon() {
    RegisterFieldIcon(R.drawable.ic_register_mail, 16.37.dp, 19.02.dp, 15.dp, 12.dp)
}

@Composable
private fun BoxScope.PasswordIcon() {
    RegisterFieldIcon(R.drawable.ic_register_lock, 17.96.dp, 16.64.dp, 12.dp, 15.75.dp)
}

@Composable
private fun BoxScope.KeyIcon() {
    RegisterFieldIcon(R.drawable.ic_register_key, 15.58.dp, 20.6.dp, 17.25.dp, 9.dp)
}

@Composable
private fun BoxScope.WhatsappPrefix() {
    Row(
        modifier = Modifier
            .align(Alignment.TopStart)
            .offset(x = 12.68.dp, y = 10.56.dp),
        horizontalArrangement = Arrangement.spacedBy(6.338.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.ic_register_phone),
            contentDescription = null
        )
        Text(
            text = "+62",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium.withFrameMetrics(
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.677.sp,
                lineHeight = 16.903.sp
            )
        )
    }
}
