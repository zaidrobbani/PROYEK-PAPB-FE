package com.example.proyek_papb_fe.feature.freelancer.projectdetail.viewmodel

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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProjectDetailUiState(
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
    private val workflowRepository: WorkflowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectDetailUiState())
    val uiState: StateFlow<ProjectDetailUiState> = _uiState.asStateFlow()

    private var umkmId: String? = null

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                coroutineScope {
                    val project = async { projectDetail() }
                    val match = async {
                        runCatching { projectRepository.recommendedProjects(limit = 100) }.getOrDefault(emptyList())
                            .firstOrNull { it.projectId == projectId }
                    }
                    val mine = async { runCatching { projectRepository.myApplications() }.getOrDefault(emptyList()) }
                    val p = project.await()
                    umkmId = p.umkmId
                    val recommendation = match.await()
                    // BE belum mengirim skill di detail project; skill tersedia dari hasil matching bila ada.
                    val skillNames = masterDataRepository.skillNames(recommendation?.requiredSkillIds.orEmpty())
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            project = p.toFreelancerProject(
                                categoryName = masterDataRepository.categoryName(p.categoryId),
                                skillNames = skillNames,
                                match = recommendation?.match
                            ),
                            isOpen = p.status == ProjectStatusCode.TERBUKA,
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

    /**
     * `GET /projects/{id}` hanya untuk project TERBUKA (atau pemiliknya). Setelah freelancer terpilih dan project
     * berjalan/selesai, detailnya diambil dari alur project `GET /projects/{id}/workflow` (keputusan BE W11).
     */
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
                createdAtMillis = null
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

    companion object {
        fun factory(
            projectId: String,
            projectRepository: ProjectRepository,
            masterDataRepository: MasterDataRepository,
            chatRepository: ChatRepository,
            workflowRepository: WorkflowRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ProjectDetailViewModel(projectId, projectRepository, masterDataRepository, chatRepository, workflowRepository)
            }
        }
    }
}
