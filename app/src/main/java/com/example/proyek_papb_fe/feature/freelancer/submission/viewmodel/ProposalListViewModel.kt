package com.example.proyek_papb_fe.feature.freelancer.submission.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.model.ProjectStatus
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.ProjectRepository
import com.example.proyek_papb_fe.core.data.repository.WorkflowRepository
import com.example.proyek_papb_fe.core.domain.model.ProjectStatusCode
import com.example.proyek_papb_fe.core.util.formatShortDate
import com.example.proyek_papb_fe.core.util.rupiah
import com.example.proyek_papb_fe.feature.freelancer.data.freelancerStatusOf
import com.example.proyek_papb_fe.feature.freelancer.submission.component.ProposalListItem
import com.example.proyek_papb_fe.feature.freelancer.submission.component.ProposalStage
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProposalListUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val items: List<ProposalListItem> = emptyList()
)

/** Tab "Proyek" freelancer: lamaran saya (`/applications/mine`) digabung status alur project (`/workflow/projects`). */
class ProposalListViewModel(
    private val projectRepository: ProjectRepository,
    private val workflowRepository: WorkflowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProposalListUiState())
    val uiState: StateFlow<ProposalListUiState> = _uiState.asStateFlow()

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
                    val applications = async { projectRepository.myApplications() }
                    val workflows = async { workflowRepository.myProjects().items.associateBy { it.project.id } }
                    val byProject = workflows.await()
                    val items = applications.await()
                        .sortedByDescending { it.submittedAtMillis ?: 0L }
                        .map { application ->
                            async {
                                val workflow = byProject[application.projectId]
                                // Project yang tidak lagi TERBUKA hanya bisa dibaca freelancer terpilih (lewat workflow).
                                val publicProject = if (workflow == null) {
                                    runCatching { projectRepository.detail(application.projectId) }.getOrNull()
                                } else null
                                val projectStatus = workflow?.project?.status ?: publicProject?.status
                                val status = freelancerStatusOf(application, projectStatus)
                                ProposalListItem(
                                    projectId = application.projectId,
                                    stage = when (status) {
                                        ProjectStatus.MENUNGGU_KESEPAKATAN -> ProposalStage.UNDER_REVIEW
                                        ProjectStatus.NEGOSIASI ->
                                            if (projectStatus == ProjectStatusCode.MENUNGGU_PEMBAYARAN) ProposalStage.PLAN_SUBMITTED
                                            else ProposalStage.NEGOTIATION
                                        ProjectStatus.PROJECT_BERJALAN -> ProposalStage.IN_PROGRESS
                                        ProjectStatus.SELESAI -> ProposalStage.COMPLETED
                                        ProjectStatus.DIBATALKAN -> ProposalStage.CANCELLED
                                    },
                                    submittedDate = application.submittedAtMillis?.let(::formatShortDate).orEmpty(),
                                    projectTitle = workflow?.project?.title ?: publicProject?.title ?: "Project tidak lagi tersedia",
                                    clientLine = workflow?.counterpart?.let { c -> listOfNotNull(c.subtitle ?: c.name, c.city).joinToString(" · ") }
                                        ?: "UMKM TEMUKARYA",
                                    offerAmount = rupiah(workflow?.agreement?.agreedPrice ?: application.proposedPrice),
                                    duration = "${workflow?.agreement?.durationDays ?: application.durationDays} hari"
                                )
                            }
                        }.awaitAll()
                    _uiState.update { it.copy(isLoading = false, items = items) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.userMessage()) }
            }
        }
    }

    companion object {
        fun factory(projectRepository: ProjectRepository, workflowRepository: WorkflowRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { ProposalListViewModel(projectRepository, workflowRepository) }
            }
    }
}
