package com.example.proyek_papb_fe.feature.auth.login.viewmodel

import com.example.proyek_papb_fe.feature.auth.role_selection.container.UserRole

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val fieldErrors: Map<String, String> = emptyMap(),
    val needsEmailVerification: Boolean = false,
    val isResendingVerification: Boolean = false,
    val infoMessage: String? = null,
    val loggedInRole: UserRole? = null
)
