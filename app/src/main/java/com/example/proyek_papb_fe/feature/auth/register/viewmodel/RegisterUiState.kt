package com.example.proyek_papb_fe.feature.auth.register.viewmodel

data class RegisterUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val fieldErrors: Map<String, String> = emptyMap(),
    val registeredEmail: String? = null
)
