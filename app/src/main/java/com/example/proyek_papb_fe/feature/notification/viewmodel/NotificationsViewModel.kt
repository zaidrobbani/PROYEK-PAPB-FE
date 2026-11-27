package com.example.proyek_papb_fe.feature.notification.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.NotificationRepository
import com.example.proyek_papb_fe.core.domain.model.AppNotification
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationsUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val items: List<AppNotification> = emptyList(),
    val unreadCount: Int = 0
)

/** Notifikasi (UMKM & freelancer). Filter kategori dilakukan lokal agar hitungan tiap tab tetap lengkap. */
class NotificationsViewModel(
    private val repository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    fun load() {
        fetch(showLoading = true)
    }

    /** Tarik untuk muat ulang: konten lama tetap tampil selama memuat. */
    fun refresh(): Job = fetch(showLoading = false)

    private fun fetch(showLoading: Boolean): Job {
        return viewModelScope.launch {
            _uiState.update { it.copy(isLoading = showLoading || it.isLoading, errorMessage = null) }
            try {
                val list = repository.notifications()
                _uiState.update { it.copy(isLoading = false, items = list.items, unreadCount = list.unreadCount) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.userMessage()) }
            }
        }
    }

    fun markRead(id: String) {
        val target = _uiState.value.items.firstOrNull { it.id == id } ?: return
        if (target.isRead) return
        _uiState.update { state ->
            state.copy(
                items = state.items.map { if (it.id == id) it.copy(isRead = true) else it },
                unreadCount = (state.unreadCount - 1).coerceAtLeast(0)
            )
        }
        viewModelScope.launch { runCatching { repository.markRead(id) } }
    }

    fun markAllRead() {
        _uiState.update { state -> state.copy(items = state.items.map { it.copy(isRead = true) }, unreadCount = 0) }
        viewModelScope.launch {
            try {
                repository.markAllRead()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.userMessage()) }
                load()
            }
        }
    }

    companion object {
        fun factory(repository: NotificationRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { NotificationsViewModel(repository) }
        }
    }
}
