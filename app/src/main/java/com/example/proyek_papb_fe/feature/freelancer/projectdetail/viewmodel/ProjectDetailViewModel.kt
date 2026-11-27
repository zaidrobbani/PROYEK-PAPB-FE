package com.example.proyek_papb_fe.feature.freelancer.projectdetail.viewmodel

import com.example.proyek_papb_fe.core.domain.model.ProjectFile
import com.example.proyek_papb_fe.core.data.repository.FileRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.model.FreelancerProject
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.ChatRepository
import com.example.proyek_papb_fe.core.data.repository.MasterDataRepository
import com.example.proyek_papb_fe.core.data.repository.ProjectRepository
import com.example.proyek_papb_fe.core.data.repository.WorkflowRepository
import com.example.proyek_papb_fe.core.domain.model.Project
import com.example.proyek_papb_fe.core.domain.model.ProjectStatusCode
import com.example.proyek_papb_fe.feature.freelancer.data.toFreelancerProject
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OpenedAttachment(val uri: String, val fileName: String, val mimeType: String)

data class ProjectDetailUiState(
    val attachments: List<ProjectFile> = emptyList(),
    val openingAttachmentId: String? = null,
    val openedAttachment: OpenedAttachment? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val project: FreelancerProject? = null,
    val isOpen: Boolean = false,
    val alreadyApplied: Boolean = false,
    val chatConversationId: String? = null,
    val message: String? = null
)

class ProjectDetailViewModel(
    private val projectId: String,
    private val projectRepository: ProjectRepository,
    private val masterDataRepository: MasterDataRepository,
    private val chatRepository: ChatRepository,
    private val workflowRepository: WorkflowRepository,
    private val fileRepository: FileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProjectDetailUiState())
    val uiState: StateFlow<ProjectDetailUiState> = _uiState.asStateFlow()

    private var umkmId: String? = null

    init {
        load()
    }

    fun load() {
        fetch(showLoading = true)
    }

    fun refresh(): Job = fetch(showLoading = false)

    private fun fetch(showLoading: Boolean): Job {
        return viewModelScope.launch {
            _uiState.update { it.copy(isLoading = showLoading || it.isLoading, errorMessage = null) }
            try {
                coroutineScope {
                    val project = async { projectDetail() }
                    val match = async {
                        runCatching { projectRepository.recommendedProjects(limit = 100) }.getOrDefault(emptyList())
                            .firstOrNull { it.projectId == projectId }
                    }
                    val mine = async { runCatching { projectRepository.myApplications() }.getOrDefault(emptyList()) }
                    val files = async { runCatching { projectRepository.attachments(projectId) }.getOrDefault(emptyList()) }
                    val p = project.await()
                    umkmId = p.umkmId
                    val recommendation = match.await()
                    val skillNames = masterDataRepository.skillNames(p.skillIds ?: recommendation?.requiredSkillIds.orEmpty())
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            project = p.toFreelancerProject(
                                categoryName = masterDataRepository.categoryName(p.categoryId),
                                skillNames = skillNames,
                                match = recommendation?.match
                            ),
                            isOpen = p.status == ProjectStatusCode.TERBUKA,
                            attachments = files.await(),
                            alreadyApplied = mine.await().any { a -> a.projectId == projectId }
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

    private suspend fun projectDetail(): Project = try {
        projectRepository.detail(projectId)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        val workflow = runCatching { workflowRepository.detail(projectId) }.getOrNull() ?: throw e
        workflow.project.let {
            Project(
                id = it.id,
                umkmId = workflow.umkm.id,
                categoryId = it.categoryId,
                title = it.title,
                description = it.description,
                budget = it.budget,
                deadline = it.deadline,
                deadlineMillis = it.deadlineMillis,
                status = it.status,
                publishedAtMillis = it.publishedAtMillis,
                createdAtMillis = null,
                owner = workflow.umkm
            )
        }
    }

    fun openChat() {
        val counterpart = umkmId ?: return
        viewModelScope.launch {
            try {
                val conversation = chatRepository.openConversation(counterpart, projectId)
                _uiState.update { it.copy(chatConversationId = conversation.id) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(message = e.userMessage()) }
            }
        }
    }

    fun onChatOpened() = _uiState.update { it.copy(chatConversationId = null) }

    fun onMessageShown() = _uiState.update { it.copy(message = null) }

    fun openAttachment(file: ProjectFile) {
        if (_uiState.value.openingAttachmentId != null) return
        viewModelScope.launch {
            _uiState.update { it.copy(openingAttachmentId = file.id) }
            try {
                val uri = fileRepository.downloadToCache(
                    "projects/$projectId/attachments/${file.id}/file", "project_${file.id}", file.fileName
                )
                _uiState.update {
                    it.copy(openingAttachmentId = null, openedAttachment = OpenedAttachment(uri, file.fileName, file.mimeType))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(openingAttachmentId = null, message = e.userMessage()) }
            }
        }
    }

    fun onAttachmentOpened() = _uiState.update { it.copy(openedAttachment = null) }

    companion object {
        fun factory(
            projectId: String,
            projectRepository: ProjectRepository,
            masterDataRepository: MasterDataRepository,
            chatRepository: ChatRepository,
            workflowRepository: WorkflowRepository,
            fileRepository: FileRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ProjectDetailViewModel(projectId, projectRepository, masterDataRepository, chatRepository, workflowRepository, fileRepository)
            }
        }
    }
}
