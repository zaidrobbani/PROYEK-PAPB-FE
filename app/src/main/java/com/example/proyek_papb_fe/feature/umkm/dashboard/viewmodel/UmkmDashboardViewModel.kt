package com.example.proyek_papb_fe.feature.umkm.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.AuthRepository
import com.example.proyek_papb_fe.core.data.repository.FreelancerRepository
import com.example.proyek_papb_fe.core.data.repository.ProfileRepository
import com.example.proyek_papb_fe.core.data.repository.WorkflowRepository
import com.example.proyek_papb_fe.core.domain.model.FreelancerSearchFilter
import com.example.proyek_papb_fe.core.domain.model.ProjectStatusCode
import com.example.proyek_papb_fe.core.util.rupiah
import com.example.proyek_papb_fe.feature.umkm.dashboard.container.DashboardActiveProject
import com.example.proyek_papb_fe.feature.umkm.dashboard.container.DashboardFreelancer
import com.example.proyek_papb_fe.feature.umkm.data.UmkmAccount
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class UmkmDashboardUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val ownerFirstName: String = "",
    val hasBusinessProfile: Boolean = true,
    val activeProjects: List<DashboardActiveProject> = emptyList(),
    val recommendedFreelancers: List<DashboardFreelancer> = emptyList()
)

class UmkmDashboardViewModel(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val workflowRepository: WorkflowRepository,
    private val freelancerRepository: FreelancerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        UmkmDashboardUiState(ownerFirstName = authRepository.currentUser()?.name.orEmpty().substringBefore(" "))
    )
    val uiState: StateFlow<UmkmDashboardUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                coroutineScope {
                    val profile = async { profileRepository.umkmProfile() }
                    val projects = async { workflowRepository.myProjects() }
                    val freelancers = async { freelancerRepository.search(FreelancerSearchFilter(), limit = 10) }
                    val ownerName = authRepository.currentUser()?.name.orEmpty()
                    val business = profile.await()
                    UmkmAccount.update(ownerName, business)
                    _uiState.value = UmkmDashboardUiState(
                        isLoading = false,
                        ownerFirstName = ownerName.substringBefore(" "),
                        hasBusinessProfile = business != null,
                        activeProjects = projects.await().items
                            .filter { it.project.status !in InactiveStatuses }
                            .take(MaxActiveProjects)
                            .map { item ->
                                DashboardActiveProject(
                                    id = item.project.id,
                                    title = item.project.title,
                                    statusLabel = item.project.statusLabel,
                                    freelancerName = item.counterpart?.name ?: "Belum ada freelancer",
                                    budget = rupiah(item.agreement?.agreedPrice ?: item.project.budget),
                                    progress = ((item.progressPercent ?: 0.0) / 100.0).toFloat()
                                )
                            },
                        recommendedFreelancers = freelancers.await().items
                            .sortedByDescending { it.averageRating ?: 0.0 }
                            .take(MaxFreelancers)
                            .map { freelancer ->
                                DashboardFreelancer(
                                    id = freelancer.userId,
                                    name = freelancer.name,
                                    role = freelancer.professionalTitle,
                                    rating = freelancer.averageRating?.let { String.format(Locale.US, "%.1f", it) } ?: "-",
                                    reviewLabel = "(${freelancer.reviewCount} ulasan)",
                                    startingPrice = freelancer.servicePriceMin?.let { rupiah(it) } ?: "Harga menyesuaikan"
                                )
                            }
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
        private const val MaxActiveProjects = 3
        private const val MaxFreelancers = 6
        private val InactiveStatuses = setOf(ProjectStatusCode.SELESAI, ProjectStatusCode.DIBATALKAN)

        fun factory(
            authRepository: AuthRepository,
            profileRepository: ProfileRepository,
            workflowRepository: WorkflowRepository,
            freelancerRepository: FreelancerRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                UmkmDashboardViewModel(authRepository, profileRepository, workflowRepository, freelancerRepository)
            }
        }
    }
}
