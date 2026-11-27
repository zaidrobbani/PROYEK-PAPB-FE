package com.example.proyek_papb_fe.feature.umkm.freelancer_applications.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.FreelancerRepository
import com.example.proyek_papb_fe.core.data.repository.MasterDataRepository
import com.example.proyek_papb_fe.core.data.repository.ProjectRepository
import com.example.proyek_papb_fe.core.data.repository.WorkflowRepository
import com.example.proyek_papb_fe.core.domain.model.ApplicationStatusCode
import com.example.proyek_papb_fe.feature.umkm.data.ProjectApplication
import com.example.proyek_papb_fe.feature.umkm.data.UmkmAccount
import com.example.proyek_papb_fe.feature.umkm.data.UmkmProject
import com.example.proyek_papb_fe.feature.umkm.data.toUmkmProject
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
import java.util.Locale
import kotlin.math.roundToInt

data class FreelancerApplicationsUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val project: UmkmProject? = null,
    val ownerLabel: String = "",
    val applications: List<ProjectApplication> = emptyList(),
    val isSelecting: Boolean = false,
    val message: String? = null,
    val selectedApplicationId: String? = null
)

class FreelancerApplicationsViewModel(
    private val projectId: String,
    private val projectRepository: ProjectRepository,
    private val workflowRepository: WorkflowRepository,
    private val freelancerRepository: FreelancerRepository,
    private val masterDataRepository: MasterDataRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(FreelancerApplicationsUiState())
    val uiState: StateFlow<FreelancerApplicationsUiState> = _uiState.asStateFlow()

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
                    val detail = async { workflowRepository.detail(projectId) }
                    val applicants = async { projectRepository.applicants(projectId) }
                    val matches = async {
                        runCatching { projectRepository.recommendedFreelancers(projectId) }.getOrDefault(emptyList())
                            .associateBy { it.freelancerId }
                    }
                    val workflow = detail.await()
                    val categoryName = masterDataRepository.categoryName(workflow.project.categoryId)
                    val matchById = matches.await()
                    val applications = applicants.await()
                        .filter { it.status != ApplicationStatusCode.CANCELLED }
                        .map { application ->
                            async {
                                val profile = runCatching { freelancerRepository.detail(application.freelancerId) }.getOrNull()?.summary
                                val match = matchById[application.freelancerId]
                                ProjectApplication(
                                    id = application.id,
                                    projectId = application.projectId,
                                    freelancerId = application.freelancerId,
                                    freelancerName = profile?.name ?: match?.name ?: "Freelancer",
                                    photo = null,
                                    photoUrl = profile?.photoUrl,
                                    specialty = profile?.professionalTitle ?: match?.professionalTitle.orEmpty(),
                                    verified = profile != null,
                                    rating = profile?.averageRating?.let { String.format(Locale.US, "%.1f", it) } ?: "-",
                                    reviewLabel = "(${profile?.reviewCount ?: 0} ulasan)",
                                    matchPercent = match?.match?.scorePercent?.roundToInt(),
                                    bid = application.proposedPrice,
                                    workingDays = application.durationDays,
                                    note = application.message.takeIf { it.isNotBlank() },
                                    isTopRecommendation = match?.match?.meetsThreshold == true,
                                    status = application.status
                                )
                            }
                        }.awaitAll()
                    val owner = UmkmAccount.profile
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            project = workflow.toUmkmProject(categoryName).copy(offersCount = applications.size),
                            ownerLabel = listOf(owner.businessName, owner.ownerName).filter { s -> s.isNotBlank() }
                                .let { parts -> if (parts.size == 2) "${parts[0]} (${parts[1]})" else parts.firstOrNull() ?: workflow.umkm.name },
                            applications = applications
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

    fun select(application: ProjectApplication) {
        if (_uiState.value.isSelecting) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSelecting = true) }
            try {
                workflowRepository.selectFreelancer(projectId, application.id)
                _uiState.update {
                    it.copy(
                        isSelecting = false,
                        message = "${application.freelancerName} dipilih. Lanjutkan kesepakatan & rencana kerja.",
                        selectedApplicationId = application.id
                    )
                }
                load()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSelecting = false, message = e.userMessage()) }
            }
        }
    }

    fun onMessageShown() = _uiState.update { it.copy(message = null) }

    fun onSelectionHandled() = _uiState.update { it.copy(selectedApplicationId = null) }

    companion object {
        fun factory(
            projectId: String,
            projectRepository: ProjectRepository,
            workflowRepository: WorkflowRepository,
            freelancerRepository: FreelancerRepository,
            masterDataRepository: MasterDataRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                FreelancerApplicationsViewModel(projectId, projectRepository, workflowRepository, freelancerRepository, masterDataRepository)
            }
        }
    }
}
