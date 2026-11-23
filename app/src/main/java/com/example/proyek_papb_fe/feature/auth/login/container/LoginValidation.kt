package com.example.proyek_papb_fe.feature.auth.login.container

import android.util.Patterns

internal data class LoginErrors(
    val email: String? = null,
    val password: String? = null
) {
    val hasError: Boolean
        get() = email != null || password != null
}

internal fun validateLogin(email: String, password: String) = LoginErrors(
    email = when {
        email.isBlank() -> "Alamat email wajib diisi."
        !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
            "Format email tidak valid (harus mengandung domain lengkap)"
        else -> null
    },
    password = "Kata sandi wajib diisi.".takeIf { password.isEmpty() }
)
