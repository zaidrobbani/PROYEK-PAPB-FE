package com.example.proyek_papb_fe.feature.umkm.freelancer_profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.FreelancerRepository
import com.example.proyek_papb_fe.core.data.repository.ProjectRepository
import com.example.proyek_papb_fe.core.data.repository.WorkflowRepository
import com.example.proyek_papb_fe.core.domain.model.Application
import com.example.proyek_papb_fe.core.domain.model.ApplicationStatusCode
import com.example.proyek_papb_fe.core.domain.model.ProjectStatusCode
import com.example.proyek_papb_fe.core.ui.components.initialsOf
import com.example.proyek_papb_fe.core.util.formatShortDate
import com.example.proyek_papb_fe.feature.umkm.data.ClientReview
import com.example.proyek_papb_fe.feature.umkm.data.FreelancerProfile
import com.example.proyek_papb_fe.feature.umkm.data.PortfolioItem
import com.example.proyek_papb_fe.feature.umkm.data.ProfileSkill
import com.example.proyek_papb_fe.feature.umkm.data.ProjectApplication
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class UmkmFreelancerProfileUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val profile: FreelancerProfile? = null,
    /** Lamaran freelancer ini (bila dibuka dari daftar Pengajuan). */
    val application: ProjectApplication? = null,
    val canSelect: Boolean = false,
    val isSelecting: Boolean = false,
    val message: String? = null,
    val selected: Boolean = false
)

class UmkmFreelancerProfileViewModel(
    private val freelancerId: String,
    private val applicationId: String?,
    private val freelancerRepository: FreelancerRepository,
    private val projectRepository: ProjectRepository,
    private val workflowRepository: WorkflowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UmkmFreelancerProfileUiState())
    val uiState: StateFlow<UmkmFreelancerProfileUiState> = _uiState.asStateFlow()

    private var applicationProjectId: String? = null

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                coroutineScope {
                    val detail = async { freelancerRepository.detail(freelancerId) }
                    val reviews = async { runCatching { freelancerRepository.reviews(freelancerId) }.getOrNull() }
                    val application = async { applicationId?.let { findApplication(it) } }
                    val d = detail.await()
                    val summary = d.summary
                    val app = application.await()
                    val profile = FreelancerProfile(
                        id = summary.userId,
                        name = summary.name,
                        headline = summary.professionalTitle,
                        verified = true,
                        location = summary.city,
                        rating = summary.averageRating?.let { String.format(Locale.US, "%.1f", it) } ?: "-",
                        reviewCount = summary.reviewCount,
                        completedProjects = null,
                        onTimeLabel = null,
                        experienceLabel = "${summary.experienceCount} pengalaman",
                        startingPrice = summary.servicePriceMin,
                        priceUnit = "/project",
                        skills = summary.skills.map { ProfileSkill(it.name) },
                        portfolioTotal = d.portfolios.size,
                        portfolio = d.portfolios.mapIndexed { index, portfolio ->
                            PortfolioItem(
                                id = portfolio.id,
                                title = portfolio.title,
                                summary = portfolio.description,
                                tag = "Portofolio",
                                tagIsSage = index % 2 == 1,
                                link = portfolio.projectUrl
                            )
                        },
                        reviews = reviews.await()?.items.orEmpty().map { review ->
                            ClientReview(
                                initials = initialsOf(review.reviewerName),
                                name = review.reviewerName,
                                business = review.businessName ?: review.projectTitle,
                                timeLabel = review.createdAtMillis?.let(::formatShortDate).orEmpty(),
                                stars = review.rating,
                                quote = review.text ?: review.highlightLabels.joinToString()
                            )
                        }
                    )
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            profile = profile,
                            application = app?.first?.let { a ->
                                ProjectApplication(
                                    id = a.id,
                                    projectId = a.projectId,
                                    freelancerId = a.freelancerId,
                                    freelancerName = summary.name,
                                    specialty = summary.professionalTitle,
                                    verified = true,
                                    rating = profile.rating,
                                    reviewLabel = "(${summary.reviewCount} ulasan)",
                                    bid = a.proposedPrice,
                                    workingDays = a.durationDays,
                                    status = a.status
                                )
                            },
                            canSelect = app?.second == true
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

    /** Lamaran dicari dari project UMKM yang masih TERBUKA (BE tidak punya endpoint detail lamaran). */
    private suspend fun findApplication(id: String): Pair<Application, Boolean>? {
        val projects = workflowRepository.myProjects().items
        for (item in projects) {
            if (item.applicationCount == 0) continue
            val match = runCatching { projectRepository.applicants(item.project.id) }.getOrDefault(emptyList())
                .firstOrNull { it.id == id } ?: continue
            applicationProjectId = item.project.id
            val selectable = item.project.status == ProjectStatusCode.TERBUKA && match.status == ApplicationStatusCode.SUBMITTED
            return match to selectable
        }
        return null
    }

    fun select() {
        val projectId = applicationProjectId ?: return
        val application = _uiState.value.application ?: return
        if (_uiState.value.isSelecting) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSelecting = true) }
            try {
                workflowRepository.selectFreelancer(projectId, application.id)
                _uiState.update {
                    it.copy(
                        isSelecting = false,
                        canSelect = false,
                        selected = true,
                        message = "${application.freelancerName} dipilih. Lanjutkan kesepakatan & rencana kerja."
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSelecting = false, message = e.userMessage()) }
            }
        }
    }

    fun showMessage(message: String) = _uiState.update { it.copy(message = message) }

    fun onMessageShown() = _uiState.update { it.copy(message = null) }

    fun onSelectedHandled() = _uiState.update { it.copy(selected = false) }

    companion object {
        fun factory(
            freelancerId: String,
            applicationId: String?,
            freelancerRepository: FreelancerRepository,
            projectRepository: ProjectRepository,
            workflowRepository: WorkflowRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                UmkmFreelancerProfileViewModel(freelancerId, applicationId, freelancerRepository, projectRepository, workflowRepository)
            }
        }
    }
}
