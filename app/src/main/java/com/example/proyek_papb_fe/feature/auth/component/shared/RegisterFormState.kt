package com.example.proyek_papb_fe.feature.auth.component.shared

import androidx.compose.runtime.saveable.Saver
import android.os.Bundle
import android.util.Patterns
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

internal data class RegisterFormInput(
    val fullName: String,
    val email: String,
    val whatsapp: String,
    val password: String,
    val confirmPassword: String,
    val termsAccepted: Boolean
)

internal data class RegisterFormErrors(
    val fullName: String? = null,
    val email: String? = null,
    val whatsapp: String? = null,
    val password: String? = null,
    val confirmPassword: String? = null,
    val terms: String? = null
) {
    val hasFieldError: Boolean
        get() = listOf(fullName, email, whatsapp, password, confirmPassword).any { it != null }

    val hasError: Boolean
        get() = hasFieldError || terms != null
}

internal fun validateRegisterForm(
    input: RegisterFormInput,
    emailRequiredMessage: String
) = RegisterFormErrors(
    fullName = when {
        input.fullName.isBlank() -> "Nama lengkap wajib diisi."
        input.fullName.trim().length > 100 -> "Maksimal 100 karakter."
        else -> null
    },
    email = when {
        input.email.isBlank() -> emailRequiredMessage
        !Patterns.EMAIL_ADDRESS.matcher(input.email.trim()).matches() -> "Format email tidak valid."
        else -> null
    },
    whatsapp = when {
        input.whatsapp.isBlank() -> "Nomor WhatsApp wajib diisi."
        !isValidWhatsapp(input.whatsapp) -> "Nomor WhatsApp tidak valid. Contoh: 0812-3456-7890."
        else -> null
    },
    password = when {
        input.password.isEmpty() -> "Kata sandi wajib diisi."
        input.password.length < 8 -> "Minimal 8 karakter."
        input.password.length > 128 -> "Maksimal 128 karakter."
        else -> null
    },
    confirmPassword = when {
        input.confirmPassword.isEmpty() -> "Konfirmasi kata sandi wajib diisi."
        input.confirmPassword != input.password -> "Konfirmasi kata sandi tidak sama."
        else -> null
    },
    terms = "Setujui Syarat & Ketentuan terlebih dahulu.".takeIf { !input.termsAccepted }
)

private fun isValidWhatsapp(number: String): Boolean {
    val digits = number.filterNot { it == '-' || it == ' ' }
    val national = digits.removePrefix("0")
    return digits.all { it in '0'..'9' } && national.startsWith('8') && national.length in 9..12
}

@Stable
internal class RegisterFormState(
    fullName: String = "",
    email: String = "",
    whatsapp: String = "",
    passwordVisible: Boolean = false,
    termsAccepted: Boolean = false,
    submitted: Boolean = false
) {
    var fullName by mutableStateOf(fullName)
    var email by mutableStateOf(email)
    var whatsapp by mutableStateOf(whatsapp)
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var passwordVisible by mutableStateOf(passwordVisible)
    var termsAccepted by mutableStateOf(termsAccepted)
    private var submitted by mutableStateOf(submitted)

    val passwordsMatch: Boolean
        get() = confirmPassword.isNotEmpty() && confirmPassword == password

    fun toInput() = RegisterFormInput(
        fullName = fullName,
        email = email,
        whatsapp = whatsapp,
        password = password,
        confirmPassword = confirmPassword,
        termsAccepted = termsAccepted
    )

    fun visibleErrors(emailRequiredMessage: String): RegisterFormErrors =
        if (submitted) validate(emailRequiredMessage) else RegisterFormErrors()

    fun submit(emailRequiredMessage: String): RegisterFormErrors {
        submitted = true
        return validate(emailRequiredMessage)
    }

    private fun validate(emailRequiredMessage: String) = validateRegisterForm(
        input = toInput(),
        emailRequiredMessage = emailRequiredMessage
    )

    companion object {
        val Saver = Saver<RegisterFormState, Bundle>(
            save = {
                Bundle().apply {
                    putString("fullName", it.fullName)
                    putString("email", it.email)
                    putString("whatsapp", it.whatsapp)
                    putBoolean("passwordVisible", it.passwordVisible)
                    putBoolean("termsAccepted", it.termsAccepted)
                    putBoolean("submitted", it.submitted)
                }
            },
            restore = {
                RegisterFormState(
                    fullName = it.getString("fullName").orEmpty(),
                    email = it.getString("email").orEmpty(),
                    whatsapp = it.getString("whatsapp").orEmpty(),
                    passwordVisible = it.getBoolean("passwordVisible"),
                    termsAccepted = it.getBoolean("termsAccepted"),
                    submitted = it.getBoolean("submitted")
                )
            }
        )
    }
}

@Composable
internal fun rememberRegisterFormState(): RegisterFormState =
    rememberSaveable(saver = RegisterFormState.Saver) { RegisterFormState() }
