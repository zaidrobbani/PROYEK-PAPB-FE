package com.example.proyek_papb_fe.feature.auth.reset_link_sent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.AuthRepository
import com.example.proyek_papb_fe.feature.auth.reset_link_sent.navigation.EmailLinkPurpose
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EmailLinkSentUiState(
    val isResending: Boolean = false,
    val message: String? = null,
    /** Bertambah tiap kirim ulang berhasil, dipakai layar untuk me-reset hitung mundur. */
    val resentCount: Int = 0
)

class EmailLinkSentViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailLinkSentUiState())
    val uiState: StateFlow<EmailLinkSentUiState> = _uiState.asStateFlow()

    fun resend(email: String, purpose: EmailLinkPurpose) {
        if (_uiState.value.isResending) return
        viewModelScope.launch {
            _uiState.update { it.copy(isResending = true) }
            try {
                val message = when (purpose) {
                    EmailLinkPurpose.RESET_PASSWORD -> repository.forgotPassword(email)
                    EmailLinkPurpose.VERIFY_EMAIL -> repository.resendVerification(email)
                }
                _uiState.update {
                    it.copy(isResending = false, message = message, resentCount = it.resentCount + 1)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isResending = false, message = e.userMessage()) }
            }
        }
    }

    fun onMessageShown() = _uiState.update { it.copy(message = null) }

    companion object {
        fun factory(repository: AuthRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { EmailLinkSentViewModel(repository) }
        }
    }
}
