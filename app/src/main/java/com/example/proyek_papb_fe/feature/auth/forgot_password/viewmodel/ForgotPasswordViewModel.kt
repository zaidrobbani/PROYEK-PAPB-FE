package com.example.proyek_papb_fe.feature.auth.forgot_password.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.ApiException
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun sendResetLink(email: String) {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                repository.forgotPassword(email)
                _uiState.update { it.copy(isLoading = false, sentToEmail = email.trim()) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val message = (e as? ApiException)?.fieldErrors?.get("email") ?: e.userMessage()
                _uiState.update { it.copy(isLoading = false, errorMessage = message) }
            }
        }
    }

    fun onInputChanged() {
        if (_uiState.value.errorMessage != null) _uiState.update { it.copy(errorMessage = null) }
    }

    fun onNavigated() = _uiState.update { it.copy(sentToEmail = null) }

    companion object {
        fun factory(repository: AuthRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { ForgotPasswordViewModel(repository) }
        }
    }
}
