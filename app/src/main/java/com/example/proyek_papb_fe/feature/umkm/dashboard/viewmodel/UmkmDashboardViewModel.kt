package com.example.proyek_papb_fe.feature.umkm.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.AuthRepository
import com.example.proyek_papb_fe.core.data.repository.FreelancerBestMatch
import com.example.proyek_papb_fe.core.data.repository.FreelancerMatchRepository
import com.example.proyek_papb_fe.core.data.repository.FreelancerRepository
import com.example.proyek_papb_fe.core.data.repository.ProfileRepository
import com.example.proyek_papb_fe.core.data.repository.ProfilePhotoRepository
import com.example.proyek_papb_fe.core.data.repository.WorkflowRepository
import com.example.proyek_papb_fe.core.domain.model.FreelancerSearchFilter
import com.example.proyek_papb_fe.core.domain.model.FreelancerSummary
import com.example.proyek_papb_fe.core.domain.model.ProjectStatusCode
import com.example.proyek_papb_fe.core.util.rupiah
import com.example.proyek_papb_fe.feature.umkm.dashboard.container.DashboardActiveProject
import com.example.proyek_papb_fe.feature.umkm.dashboard.container.DashboardFreelancer
import com.example.proyek_papb_fe.feature.umkm.data.UmkmAccount
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
    private val freelancerRepository: FreelancerRepository,
    private val photoRepository: ProfilePhotoRepository,
    private val matchRepository: FreelancerMatchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        UmkmDashboardUiState(ownerFirstName = authRepository.currentUser()?.name.orEmpty().substringBefore(" "))
    )
    val uiState: StateFlow<UmkmDashboardUiState> = _uiState.asStateFlow()

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
                    val profile = async { profileRepository.umkmProfile() }
                    val projects = async { workflowRepository.myProjects() }
                    val freelancers = async { freelancerRepository.search(FreelancerSearchFilter(), limit = 50).items }
                    val matches = async { runCatching { matchRepository.bestMatches() }.getOrDefault(emptyMap()) }
                    val ownerName = authRepository.currentUser()?.name.orEmpty()
                    val business = profile.await()
                    UmkmAccount.update(ownerName, business)
                    UmkmAccount.updatePhoto(photoRepository.currentLocalUri())
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
                                    freelancerPhotoUrl = item.counterpart?.photoUrl,
                                    budget = rupiah(item.agreement?.agreedPrice ?: item.project.budget),
                                    progress = ((item.progressPercent ?: 0.0) / 100.0).toFloat()
                                )
                            },
                        recommendedFreelancers = recommended(freelancers.await(), matches.await())
                            .map { (freelancer, match) ->
                                DashboardFreelancer(
                                    id = freelancer.userId,
                                    name = freelancer.name,
                                    photoUrl = freelancer.photoUrl,
                                    role = freelancer.professionalTitle,
                                    rating = freelancer.averageRating?.let { String.format(Locale.US, "%.1f", it) } ?: "-",
                                    reviewLabel = "(${freelancer.reviewCount} ulasan)",
                                    startingPrice = priceText(freelancer.servicePriceMin, freelancer.servicePriceMax),
                                    priceLabel = if (freelancer.servicePriceMin != null && freelancer.servicePriceMax != null &&
                                        freelancer.servicePriceMax > freelancer.servicePriceMin
                                    ) "Kisaran harga" else "Mulai dari",
                                    city = freelancer.city,
                                    isAvailable = freelancer.isAvailable,
                                    skills = freelancer.skills.map { it.name },
                                    experienceCount = freelancer.experienceCount,
                                    matchPercent = match?.percent,
                                    matchProjectTitle = match?.projectTitle
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

    /**
     * Rekomendasi = freelancer paling cocok (matching F1, ≥ 60%) dengan project TERBUKA, lalu sisanya
     * menurut rating. Freelancer cocok yang tidak ada di halaman pencarian diambil dari profil publiknya.
     */
    private suspend fun recommended(
        searched: List<FreelancerSummary>,
        matches: Map<String, FreelancerBestMatch>
    ): List<Pair<FreelancerSummary, FreelancerBestMatch?>> = coroutineScope {
        val byId = searched.associateBy { it.userId }
        val topMatched = matches.entries.sortedByDescending { it.value.percent }.take(MaxFreelancers)
        val matched = topMatched.map { (id, match) ->
            async { (byId[id] ?: runCatching { freelancerRepository.detail(id).summary }.getOrNull())?.let { it to match } }
        }.awaitAll().filterNotNull()
        val rest = searched
            .filter { it.userId !in matches }
            .sortedByDescending { it.averageRating ?: 0.0 }
            .map { it to null }
        (matched + rest).take(MaxFreelancers)
    }

    companion object {
        private const val MaxActiveProjects = 3
        private const val MaxFreelancers = 6
        private val InactiveStatuses = setOf(ProjectStatusCode.SELESAI, ProjectStatusCode.DIBATALKAN)

        fun factory(
            authRepository: AuthRepository,
            profileRepository: ProfileRepository,
            workflowRepository: WorkflowRepository,
            freelancerRepository: FreelancerRepository,
            photoRepository: ProfilePhotoRepository,
            matchRepository: FreelancerMatchRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                UmkmDashboardViewModel(
                    authRepository, profileRepository, workflowRepository, freelancerRepository, photoRepository, matchRepository
                )
            }
        }
    }
}

/** Harga layanan freelancer: rentang bila ada maksimum, selain itu harga minimum. */
private fun priceText(min: Long?, max: Long?): String = when {
    min != null && max != null && max > min -> "${rupiah(min)} – ${rupiah(max)}"
    min != null -> rupiah(min)
    max != null -> rupiah(max)
    else -> "Harga menyesuaikan"
}
