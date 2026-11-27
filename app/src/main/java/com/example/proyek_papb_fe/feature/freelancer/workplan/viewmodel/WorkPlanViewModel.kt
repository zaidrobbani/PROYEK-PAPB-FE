package com.example.proyek_papb_fe.feature.freelancer.workplan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.WorkflowRepository
import com.example.proyek_papb_fe.core.domain.model.MilestoneDraft
import com.example.proyek_papb_fe.core.domain.model.MilestonePlan
import com.example.proyek_papb_fe.core.domain.model.ProjectStatusCode
import com.example.proyek_papb_fe.core.domain.model.WorkflowActionCode
import com.example.proyek_papb_fe.core.domain.model.WorkflowDetail
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WorkPlanVmState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val detail: WorkflowDetail? = null,
    val plan: MilestonePlan? = null,
    val isSaving: Boolean = false,
    val message: String? = null,
    /** Bertambah tiap rencana dimuat ulang dari BE, dipakai layar untuk mengisi ulang input. */
    val planVersion: Int = 0
) {
    val isEditable: Boolean
        get() = detail?.can(WorkflowActionCode.EDIT_MILESTONE_PLAN) == true && detail.agreement?.approvedByMe != true
}

/**
 * Rencana Pengerjaan freelancer (F2 + F4): simpan draf = PUT milestones, Ajukan ke UMKM = PUT lalu approve
 * kesepakatan, Mulai Pengerjaan setelah dibayar, dan tandai milestone selesai.
 */
class WorkPlanViewModel(
    private val projectId: String,
    private val workflowRepository: WorkflowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkPlanVmState())
    val uiState: StateFlow<WorkPlanVmState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        fetch(showLoading = true)
    }

    /** Tarik untuk muat ulang: konten lama tetap tampil selama memuat. */
    fun refresh(): Job = fetch(showLoading = false)

    private fun fetch(showLoading: Boolean): Job {
        return viewModelScope.launch {
            _uiState.update { it.copy(isLoading = showLoading || it.isLoading, errorMessage = null) }
            try {
                coroutineScope {
                    val detail = async { workflowRepository.detail(projectId) }
                    val plan = async { workflowRepository.milestones(projectId) }
                    _uiState.update {
                        it.copy(isLoading = false, detail = detail.await(), plan = plan.await(), planVersion = it.planVersion + 1)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.userMessage()) }
            }
        }
    }

    fun saveDraft(items: List<MilestoneDraft>) = runAction("Draf rencana pengerjaan disimpan") {
        val plan = workflowRepository.saveMilestones(projectId, items)
        _uiState.update { it.copy(plan = plan) }
    }

    fun submitToUmkm(items: List<MilestoneDraft>) = runAction(null) {
        workflowRepository.saveMilestones(projectId, items)
        // PUT milestone mereset persetujuan & menaikkan versi kesepakatan, jadi ambil versi terbaru dulu.
        val version = workflowRepository.detail(projectId).agreement?.version
        val updated = workflowRepository.approveAgreement(projectId, version)
        _uiState.update {
            it.copy(
                message = if (updated.project.status == ProjectStatusCode.MENUNGGU_PEMBAYARAN)
                    "Rencana disepakati kedua pihak. Menunggu pembayaran UMKM."
                else "Rencana pengerjaan diajukan ke UMKM"
            )
        }
    }

    fun startWork() = runAction("Pengerjaan dimulai. Semangat!") {
        workflowRepository.startWork(projectId)
    }

    fun completeMilestone(milestoneId: String) {
        val milestone = _uiState.value.plan?.items?.firstOrNull { it.id == milestoneId && !it.isDone } ?: return
        runAction("Milestone \"${milestone.name}\" ditandai selesai") {
            workflowRepository.completeMilestone(projectId, milestone.id)
        }
    }

    private fun runAction(successMessage: String?, block: suspend () -> Unit) {
        if (_uiState.value.isSaving) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                block()
                _uiState.update { it.copy(isSaving = false, message = successMessage ?: it.message) }
                load()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, message = e.userMessage()) }
            }
        }
    }

    fun onMessageShown() = _uiState.update { it.copy(message = null) }

    companion object {
        fun factory(projectId: String, workflowRepository: WorkflowRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { WorkPlanViewModel(projectId, workflowRepository) }
            }
    }
}
