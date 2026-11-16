package com.example.proyek_papb_fe.feature.auth.component.shared

import android.util.Patterns
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
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

/** Blueprint tidak menetapkan aturan kata sandi, jadi hanya wajib diisi dan harus sama. */
internal fun validateRegisterForm(
    input: RegisterFormInput,
    emailRequiredMessage: String
) = RegisterFormErrors(
    fullName = "Nama lengkap wajib diisi.".takeIf { input.fullName.isBlank() },
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
    password = "Kata sandi wajib diisi.".takeIf { input.password.isEmpty() },
    confirmPassword = when {
        input.confirmPassword.isEmpty() -> "Konfirmasi kata sandi wajib diisi."
        input.confirmPassword != input.password -> "Konfirmasi kata sandi tidak sama."
        else -> null
    },
    terms = "Setujui Syarat & Ketentuan terlebih dahulu.".takeIf { !input.termsAccepted }
)

/** Pemeriksaan sederhana nomor seluler Indonesia, dengan atau tanpa angka 0 di depan. */
private fun isValidWhatsapp(number: String): Boolean {
    val digits = number.filterNot { it == '-' || it == ' ' }
    val national = digits.removePrefix("0")
    return digits.all { it in '0'..'9' } && national.startsWith('8') && national.length in 9..12
}

/** State lokal form Registrasi. Kata sandi sengaja tidak masuk saved instance state. */
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

    /** Pesan muncul setelah tombol ditekan, lalu ikut hilang saat isian diperbaiki. */
    fun visibleErrors(emailRequiredMessage: String): RegisterFormErrors =
        if (submitted) validate(emailRequiredMessage) else RegisterFormErrors()

    fun submit(emailRequiredMessage: String): RegisterFormErrors {
        submitted = true
        return validate(emailRequiredMessage)
    }

    private fun validate(emailRequiredMessage: String) = validateRegisterForm(
        input = RegisterFormInput(
            fullName = fullName,
            email = email,
            whatsapp = whatsapp,
            password = password,
            confirmPassword = confirmPassword,
            termsAccepted = termsAccepted
        ),
        emailRequiredMessage = emailRequiredMessage
    )

    companion object {
        val Saver = listSaver<RegisterFormState, Any>(
            save = {
                listOf(
                    it.fullName,
                    it.email,
                    it.whatsapp,
                    it.passwordVisible,
                    it.termsAccepted,
                    it.submitted
                )
            },
            restore = {
                RegisterFormState(
                    fullName = it[0] as String,
                    email = it[1] as String,
                    whatsapp = it[2] as String,
                    passwordVisible = it[3] as Boolean,
                    termsAccepted = it[4] as Boolean,
                    submitted = it[5] as Boolean
                )
            }
        )
    }
}

@Composable
internal fun rememberRegisterFormState(): RegisterFormState =
    rememberSaveable(saver = RegisterFormState.Saver) { RegisterFormState() }
