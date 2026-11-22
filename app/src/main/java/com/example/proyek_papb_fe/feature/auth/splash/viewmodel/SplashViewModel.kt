package com.example.proyek_papb_fe.feature.auth.splash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.repository.AuthRepository
import com.example.proyek_papb_fe.core.session.AccountRole
import com.example.proyek_papb_fe.feature.auth.role_selection.container.UserRole
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SplashUiState(
    val isCheckingSession: Boolean = true,
    val loggedInRole: UserRole? = null
)

/** Bila masih ada token yang diterima BE, user langsung masuk ke beranda sesuai peran. */
class SplashViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val role = try {
                repository.refreshCurrentUser()?.role
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // 401 sudah menghapus sesi lewat AuthInterceptor. Error lain (server mati/offline):
                // tetap pakai sesi tersimpan, layar berikutnya menampilkan error + coba lagi.
                repository.currentUser()?.role
            }
            _uiState.update { SplashUiState(isCheckingSession = false, loggedInRole = role?.toUserRole()) }
        }
    }

    fun onNavigated() = _uiState.update { it.copy(loggedInRole = null) }

    private fun AccountRole.toUserRole(): UserRole? = when (this) {
        AccountRole.UMKM -> UserRole.Umkm
        AccountRole.FREELANCER -> UserRole.Freelancer
        AccountRole.ADMIN -> null
    }

    companion object {
        fun factory(repository: AuthRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { SplashViewModel(repository) }
        }
    }
}
