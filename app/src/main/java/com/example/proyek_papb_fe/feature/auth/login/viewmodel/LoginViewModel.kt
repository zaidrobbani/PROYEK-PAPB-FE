package com.example.proyek_papb_fe.feature.auth.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.ApiException
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.AuthRepository
import com.example.proyek_papb_fe.core.session.AccountRole
import com.example.proyek_papb_fe.feature.auth.role_selection.container.UserRole
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null, fieldErrors = emptyMap(), needsEmailVerification = false)
            }
            try {
                val user = repository.login(email, password)
                val role = when (user.role) {
                    AccountRole.UMKM -> UserRole.Umkm
                    AccountRole.FREELANCER -> UserRole.Freelancer
                    AccountRole.ADMIN -> null
                }
                if (role == null) {
                    repository.logout()
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Akun admin hanya dapat login melalui dashboard admin")
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, loggedInRole = role) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val apiError = e as? ApiException
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = if (apiError?.fieldErrors.isNullOrEmpty()) e.userMessage() else null,
                        fieldErrors = apiError?.fieldErrors.orEmpty(),
                        needsEmailVerification = apiError?.code == CODE_EMAIL_NOT_VERIFIED
                    )
                }
            }
        }
    }

    fun resendVerification(email: String) {
        if (_uiState.value.isResendingVerification) return
        viewModelScope.launch {
            _uiState.update { it.copy(isResendingVerification = true) }
            try {
                val message = repository.resendVerification(email)
                _uiState.update { it.copy(isResendingVerification = false, infoMessage = message) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isResendingVerification = false, infoMessage = e.userMessage()) }
            }
        }
    }

    fun onInputChanged() {
        if (_uiState.value.errorMessage != null || _uiState.value.fieldErrors.isNotEmpty()) {
            _uiState.update { it.copy(errorMessage = null, fieldErrors = emptyMap(), needsEmailVerification = false) }
        }
    }

    fun onInfoMessageShown() = _uiState.update { it.copy(infoMessage = null) }

    fun onNavigated() = _uiState.update { it.copy(loggedInRole = null) }

    companion object {
        const val CODE_EMAIL_NOT_VERIFIED = "EMAIL_NOT_VERIFIED"

        fun factory(repository: AuthRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { LoginViewModel(repository) }
        }
    }
}
