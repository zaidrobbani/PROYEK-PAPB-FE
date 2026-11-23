package com.example.proyek_papb_fe.feature.auth.forgot_password.viewmodel

data class ForgotPasswordUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val sentToEmail: String? = null
)
