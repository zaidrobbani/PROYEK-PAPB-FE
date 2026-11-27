package com.example.proyek_papb_fe.feature.freelancer.submission.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.ProjectRepository
import com.example.proyek_papb_fe.core.data.repository.WorkflowRepository
import com.example.proyek_papb_fe.core.domain.model.Application
import com.example.proyek_papb_fe.core.domain.model.ApplicationStatusCode
import com.example.proyek_papb_fe.core.domain.model.Project
import com.example.proyek_papb_fe.core.domain.model.ProjectStatusCode
import com.example.proyek_papb_fe.core.domain.model.WorkflowDetail
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SubmissionStatusVmState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val application: Application? = null,
    val workflow: WorkflowDetail? = null,
    val project: Project? = null
) {
    val isSelected: Boolean get() = application?.status == ApplicationStatusCode.ACCEPTED && workflow != null
    val isRejected: Boolean
        get() = application?.status == ApplicationStatusCode.NOT_SELECTED || application?.status == ApplicationStatusCode.CANCELLED
    val isCancelled: Boolean get() = workflow?.project?.status == ProjectStatusCode.DIBATALKAN
    val isCompleted: Boolean get() = workflow?.project?.status == ProjectStatusCode.SELESAI
    val isAgreed: Boolean
        get() = workflow?.project?.status !in setOf(null, ProjectStatusCode.MENUNGGU_KESEPAKATAN, ProjectStatusCode.DIBATALKAN)
}

class SubmissionStatusViewModel(
    private val projectId: String,
    private val projectRepository: ProjectRepository,
    private val workflowRepository: WorkflowRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SubmissionStatusVmState())
    val uiState: StateFlow<SubmissionStatusVmState> = _uiState.asStateFlow()

    fun load() {
        fetch(showLoading = true)
    }

    fun refresh(): Job = fetch(showLoading = false)

    private fun fetch(showLoading: Boolean): Job {
        return viewModelScope.launch {
            _uiState.update { it.copy(isLoading = showLoading || it.isLoading, errorMessage = null) }
            try {
                val application = projectRepository.myApplications().firstOrNull { it.projectId == projectId }
                val workflow = if (application?.status == ApplicationStatusCode.ACCEPTED) {
                    runCatching { workflowRepository.detail(projectId) }.getOrNull()
                } else null
                val project = if (workflow == null) runCatching { projectRepository.detail(projectId) }.getOrNull() else null
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        application = application,
                        workflow = workflow,
                        project = project,
                        errorMessage = if (application == null) "Belum ada penawaran terkirim untuk project ini." else null
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
            projectId: String,
            projectRepository: ProjectRepository,
            workflowRepository: WorkflowRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { SubmissionStatusViewModel(projectId, projectRepository, workflowRepository) }
        }
    }
}
