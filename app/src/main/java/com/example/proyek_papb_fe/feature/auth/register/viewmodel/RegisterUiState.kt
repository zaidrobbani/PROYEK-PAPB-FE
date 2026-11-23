package com.example.proyek_papb_fe.feature.auth.register.viewmodel

data class RegisterUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    /** Error per field dari BE, key mengikuti nama field form (fullName, email, whatsapp, ...). */
    val fieldErrors: Map<String, String> = emptyMap(),
    val registeredEmail: String? = null
)
