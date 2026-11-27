package com.example.proyek_papb_fe.feature.auth.register.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.ApiException
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.AuthRepository
import com.example.proyek_papb_fe.core.data.repository.RegisterInput
import com.example.proyek_papb_fe.core.session.AccountRole
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: AuthRepository,
    private val role: AccountRole
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(
        fullName: String,
        email: String,
        whatsapp: String,
        password: String,
        confirmPassword: String,
        termsAccepted: Boolean
    ) {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, fieldErrors = emptyMap()) }
            try {
                repository.register(
                    RegisterInput(
                        name = fullName,
                        email = email,
                        phoneNumber = whatsapp,
                        password = password,
                        passwordConfirmation = confirmPassword,
                        role = role,
                        termsAccepted = termsAccepted
                    )
                )
                _uiState.update { it.copy(isLoading = false, registeredEmail = email.trim()) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val apiError = e as? ApiException
                val fieldErrors = apiError?.fieldErrors.orEmpty()
                    .mapKeys { (field, _) -> ServerFieldToForm[field] ?: field }
                    .toMutableMap()
                if (apiError?.httpStatus == 409) fieldErrors["email"] = e.userMessage()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        fieldErrors = fieldErrors,
                        errorMessage = if (fieldErrors.isEmpty()) e.userMessage() else null
                    )
                }
            }
        }
    }

    fun onInputChanged() {
        if (_uiState.value.errorMessage != null || _uiState.value.fieldErrors.isNotEmpty()) {
            _uiState.update { it.copy(errorMessage = null, fieldErrors = emptyMap()) }
        }
    }

    fun onErrorShown() = _uiState.update { it.copy(errorMessage = null) }

    fun onNavigated() = _uiState.update { it.copy(registeredEmail = null) }

    companion object {
        private val ServerFieldToForm = mapOf(
            "name" to "fullName",
            "email" to "email",
            "phone_number" to "whatsapp",
            "password" to "password",
            "password_confirmation" to "confirmPassword",
            "terms_accepted" to "terms"
        )

        fun factory(repository: AuthRepository, role: AccountRole): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { RegisterViewModel(repository, role) }
            }
    }
}
