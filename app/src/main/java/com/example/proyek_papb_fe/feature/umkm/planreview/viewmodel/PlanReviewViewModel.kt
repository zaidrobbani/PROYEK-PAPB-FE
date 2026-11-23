package com.example.proyek_papb_fe.feature.umkm.planreview.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.WorkflowRepository
import com.example.proyek_papb_fe.core.domain.model.MilestonePlan
import com.example.proyek_papb_fe.core.domain.model.ProjectStatusCode
import com.example.proyek_papb_fe.core.domain.model.WorkflowActionCode
import com.example.proyek_papb_fe.core.domain.model.WorkflowDetail
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlanReviewVmState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val detail: WorkflowDetail? = null,
    val plan: MilestonePlan? = null,
    val isSubmitting: Boolean = false,
    val message: String? = null,
    /** true saat kesepakatan final (MENUNGGU_PEMBAYARAN): layar lanjut ke pembayaran. */
    val readyToPay: Boolean = false
) {
    val canApprove: Boolean get() = detail?.can(WorkflowActionCode.APPROVE_AGREEMENT) == true
    val canRequestChanges: Boolean get() = detail?.can(WorkflowActionCode.REQUEST_AGREEMENT_CHANGES) == true
}

/** Tinjau Rencana Pengerjaan (UMKM): Setujui Rencana = approve agreement, Minta Ubah = request-changes. */
class PlanReviewViewModel(
    private val projectId: String,
    private val workflowRepository: WorkflowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlanReviewVmState())
    val uiState: StateFlow<PlanReviewVmState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                coroutineScope {
                    val detail = async { workflowRepository.detail(projectId) }
                    val plan = async { workflowRepository.milestones(projectId) }
                    val d = detail.await()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            detail = d,
                            plan = plan.await(),
                            readyToPay = d.project.status == ProjectStatusCode.MENUNGGU_PEMBAYARAN
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.userMessage()) }
            }
        }
    }

    fun approve() {
        val detail = _uiState.value.detail ?: return
        if (_uiState.value.isSubmitting) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            try {
                // agreement_version mencegah menyetujui versi lama bila freelancer baru saja mengubah rencana.
                val updated = workflowRepository.approveAgreement(projectId, detail.agreement?.version)
                val finalized = updated.project.status == ProjectStatusCode.MENUNGGU_PEMBAYARAN
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        detail = updated,
                        readyToPay = finalized,
                        message = if (finalized) "Kesepakatan final. Lanjutkan ke pembayaran."
                        else "Rencana disetujui. Menunggu persetujuan freelancer."
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSubmitting = false, message = e.userMessage()) }
                load()
            }
        }
    }

    fun requestChanges(note: String) {
        if (_uiState.value.isSubmitting) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            try {
                val updated = workflowRepository.requestChanges(projectId, note)
                _uiState.update {
                    it.copy(isSubmitting = false, detail = updated, message = "Permintaan ubah dikirim ke freelancer")
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSubmitting = false, message = e.userMessage()) }
            }
        }
    }

    fun onMessageShown() = _uiState.update { it.copy(message = null) }

    fun onReadyToPayHandled() = _uiState.update { it.copy(readyToPay = false) }

    companion object {
        fun factory(projectId: String, workflowRepository: WorkflowRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { PlanReviewViewModel(projectId, workflowRepository) }
            }
    }
}
