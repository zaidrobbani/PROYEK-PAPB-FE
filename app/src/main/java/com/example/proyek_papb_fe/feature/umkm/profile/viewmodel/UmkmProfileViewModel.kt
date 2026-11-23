package com.example.proyek_papb_fe.feature.umkm.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.AuthRepository
import com.example.proyek_papb_fe.core.data.repository.NotificationRepository
import com.example.proyek_papb_fe.core.data.repository.PaymentRepository
import com.example.proyek_papb_fe.core.data.repository.ProfileRepository
import com.example.proyek_papb_fe.core.data.repository.ProfilePhotoRepository
import com.example.proyek_papb_fe.core.data.repository.WorkflowRepository
import com.example.proyek_papb_fe.core.domain.model.PaymentStatusCode
import com.example.proyek_papb_fe.core.domain.model.ProjectStatusCode
import com.example.proyek_papb_fe.core.domain.model.TransactionGroup
import com.example.proyek_papb_fe.feature.umkm.data.UmkmAccount
import com.example.proyek_papb_fe.feature.umkm.data.needsUmkmAction
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UmkmProfileUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val projectCount: Int = 0,
    val completedCount: Int = 0,
    val attentionCount: Int = 0,
    val paidTotal: Long = 0,
    val unreadNotifications: Int = 0
)

class UmkmProfileViewModel(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val workflowRepository: WorkflowRepository,
    private val paymentRepository: PaymentRepository,
    private val notificationRepository: NotificationRepository,
    private val photoRepository: ProfilePhotoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UmkmProfileUiState())
    val uiState: StateFlow<UmkmProfileUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                coroutineScope {
                    val profile = async { profileRepository.umkmProfile() }
                    val projects = async { workflowRepository.myProjects() }
                    val history = async { paymentRepository.history(TransactionGroup.ALL) }
                    val unread = async { notificationRepository.unreadCount() }
                    UmkmAccount.update(authRepository.currentUser()?.name.orEmpty(), profile.await())
                    UmkmAccount.updatePhoto(photoRepository.currentLocalUri())
                    val items = projects.await().items
                    _uiState.value = UmkmProfileUiState(
                        isLoading = false,
                        projectCount = items.size,
                        completedCount = items.count { it.project.status == ProjectStatusCode.SELESAI },
                        attentionCount = items.count { it.needsUmkmAction() },
                        paidTotal = history.await().items
                            .filter { it.status == PaymentStatusCode.BERHASIL }
                            .sumOf { it.amount },
                        unreadNotifications = unread.await()
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.userMessage()) }
            }
        }
    }

    companion object {
        fun factory(
            authRepository: AuthRepository,
            profileRepository: ProfileRepository,
            workflowRepository: WorkflowRepository,
            paymentRepository: PaymentRepository,
            notificationRepository: NotificationRepository,
            photoRepository: ProfilePhotoRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                UmkmProfileViewModel(authRepository, profileRepository, workflowRepository, paymentRepository, notificationRepository, photoRepository)
            }
        }
    }
}
