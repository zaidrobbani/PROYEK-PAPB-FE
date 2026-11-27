package com.example.proyek_papb_fe.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.repository.AuthRepository
import com.example.proyek_papb_fe.core.session.SessionManager
import com.example.proyek_papb_fe.core.data.FreelancerProfileStore
import com.example.proyek_papb_fe.feature.umkm.data.UmkmAccount
import com.example.proyek_papb_fe.feature.umkm.data.UmkmConversationStore
import com.example.proyek_papb_fe.feature.umkm.data.UmkmNegotiationStore
import com.example.proyek_papb_fe.feature.umkm.data.UmkmNotificationStore
import com.example.proyek_papb_fe.feature.umkm.data.UmkmProjectStore
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class AppSessionViewModel(
    private val authRepository: AuthRepository,
    sessionManager: SessionManager
) : ViewModel() {
    val sessionExpired: SharedFlow<Unit> = sessionManager.expiredEvents

    val currentEmail: String get() = authRepository.currentUser()?.email.orEmpty()

    private var isLoggingOut = false

    fun logout(onDone: () -> Unit) {
        if (isLoggingOut) return
        isLoggingOut = true
        viewModelScope.launch {
            authRepository.logout()
            clearCaches()
            isLoggingOut = false
            onDone()
        }
    }

    fun clearCaches() {
        UmkmAccount.clear()
        UmkmProjectStore.replace(emptyList())
        UmkmConversationStore.reset()
        UmkmNegotiationStore.reset()
        UmkmNotificationStore.reset()
        FreelancerProfileStore.clear()
    }

    companion object {
        fun factory(authRepository: AuthRepository, sessionManager: SessionManager): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { AppSessionViewModel(authRepository, sessionManager) }
            }
    }
}
