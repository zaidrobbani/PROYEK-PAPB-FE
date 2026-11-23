package com.example.proyek_papb_fe.feature.freelancer.negotiation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.model.CancellationReason
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.ChatRepository
import com.example.proyek_papb_fe.core.data.repository.WorkflowRepository
import com.example.proyek_papb_fe.core.domain.model.CancelReasonCode
import com.example.proyek_papb_fe.core.domain.model.Message
import com.example.proyek_papb_fe.core.domain.model.WorkflowActionCode
import com.example.proyek_papb_fe.core.domain.model.WorkflowDetail
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FreelancerNegotiationUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val detail: WorkflowDetail? = null,
    val messages: List<Message> = emptyList(),
    val isCancelling: Boolean = false,
    val cancelled: Boolean = false,
    val message: String? = null
) {
    val canCancel: Boolean get() = detail?.can(WorkflowActionCode.CANCEL_PROJECT) == true
}

fun CancellationReason.toCode(): String = when (this) {
    CancellationReason.PRICE_MISMATCH -> CancelReasonCode.HARGA_TIDAK_SESUAI
    CancellationReason.SCHEDULE_MISMATCH -> CancelReasonCode.JADWAL_TIDAK_COCOK
    CancellationReason.REQUIREMENT_CHANGED -> CancelReasonCode.KEBUTUHAN_BERUBAH
    CancellationReason.OTHER -> CancelReasonCode.LAINNYA
}

class FreelancerNegotiationViewModel(
    private val projectId: String,
    private val workflowRepository: WorkflowRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FreelancerNegotiationUiState())
    val uiState: StateFlow<FreelancerNegotiationUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val detail = workflowRepository.detail(projectId)
                // Pesan negosiasi = percakapan project dengan UMKM (dibuat bila belum ada).
                val messages = runCatching {
                    val conversation = chatRepository.openConversation(detail.umkm.id, projectId)
                    chatRepository.messages(conversation.id).items
                }.getOrDefault(emptyList())
                _uiState.update { it.copy(isLoading = false, detail = detail, messages = messages) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.userMessage()) }
            }
        }
    }

    fun cancel(reason: CancellationReason) {
        if (_uiState.value.isCancelling) return
        viewModelScope.launch {
            _uiState.update { it.copy(isCancelling = true) }
            try {
                // BE mewajibkan catatan bila alasan LAINNYA; sheet Figma tidak punya kolom catatan.
                val note = if (reason == CancellationReason.OTHER) reason.label else null
                workflowRepository.cancel(projectId, reason.toCode(), note)
                _uiState.update { it.copy(isCancelling = false, cancelled = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isCancelling = false, message = e.userMessage()) }
            }
        }
    }

    fun onMessageShown() = _uiState.update { it.copy(message = null) }

    fun onCancelledHandled() = _uiState.update { it.copy(cancelled = false) }

    companion object {
        fun factory(projectId: String, workflowRepository: WorkflowRepository, chatRepository: ChatRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { FreelancerNegotiationViewModel(projectId, workflowRepository, chatRepository) }
            }
    }
}
