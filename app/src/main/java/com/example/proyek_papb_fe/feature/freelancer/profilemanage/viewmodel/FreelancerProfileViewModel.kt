package com.example.proyek_papb_fe.feature.freelancer.profilemanage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.FreelancerProfileStore
import com.example.proyek_papb_fe.core.data.model.EmploymentType
import com.example.proyek_papb_fe.core.data.model.FreelancerProfile
import com.example.proyek_papb_fe.core.data.model.MonthYear
import com.example.proyek_papb_fe.core.data.model.PortfolioCategory
import com.example.proyek_papb_fe.core.data.model.PortfolioItem
import com.example.proyek_papb_fe.core.data.model.WorkExperience
import com.example.proyek_papb_fe.core.data.remote.ApiException
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.AuthRepository
import com.example.proyek_papb_fe.core.data.repository.FileRepository
import com.example.proyek_papb_fe.core.data.repository.FreelancerRepository
import com.example.proyek_papb_fe.core.data.repository.MasterDataRepository
import com.example.proyek_papb_fe.core.data.repository.PaymentRepository
import com.example.proyek_papb_fe.core.data.repository.ProfileRepository
import com.example.proyek_papb_fe.core.data.repository.WorkflowRepository
import com.example.proyek_papb_fe.core.domain.model.AvailabilityStatus
import com.example.proyek_papb_fe.core.domain.model.Experience
import com.example.proyek_papb_fe.core.domain.model.ProjectStatusCode
import com.example.proyek_papb_fe.core.domain.model.UploadFile
import com.example.proyek_papb_fe.core.ui.components.initialsOf
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class FreelancerProfileUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
    val message: String? = null,
    /** Bertambah setiap simpan/hapus berhasil (layar menutup form / menampilkan snackbar). */
    val savedCount: Int = 0,
    val availableBalance: Long = 0,
    val skillOptions: List<String> = emptyList()
)

/** Profil freelancer: profil profesional, skill, portofolio (+ cover), dan pengalaman kerja. */
class FreelancerProfileViewModel(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val masterDataRepository: MasterDataRepository,
    private val freelancerRepository: FreelancerRepository,
    private val workflowRepository: WorkflowRepository,
    private val paymentRepository: PaymentRepository,
    private val fileRepository: FileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FreelancerProfileUiState())
    val uiState: StateFlow<FreelancerProfileUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                coroutineScope {
                    val profile = async { profileRepository.freelancerProfile() }
                    val skillIds = async { profileRepository.mySkillIds() }
                    val portfolios = async { profileRepository.portfolios() }
                    val experiences = async { profileRepository.experiences() }
                    val projects = async { runCatching { workflowRepository.myProjects().items }.getOrDefault(emptyList()) }
                    val wallet = async { runCatching { paymentRepository.wallet() }.getOrNull() }
                    val allSkills = async { runCatching { masterDataRepository.skills() }.getOrDefault(emptyList()) }
                    val me = authRepository.currentUser()
                    val reviews = async {
                        me?.id?.let { id -> runCatching { freelancerRepository.reviews(id) }.getOrNull() }
                    }
                    val p = profile.await()
                    val exps = experiences.await()
                    val items = projects.await()
                    val completed = items.count { it.project.status == ProjectStatusCode.SELESAI }
                    val cancelled = items.count { it.project.status == ProjectStatusCode.DIBATALKAN }
                    val skillNames = masterDataRepository.skillNames(skillIds.await())
                    val r = reviews.await()
                    FreelancerProfileStore.replace(
                        FreelancerProfile(
                            fullName = me?.name.orEmpty(),
                            initials = initialsOf(me?.name.orEmpty()),
                            headline = p?.professionalTitle.orEmpty(),
                            bio = p?.bio.orEmpty(),
                            city = p?.city.orEmpty(),
                            primaryField = FreelancerProfileStore.profile.primaryField,
                            startingRate = p?.servicePriceMin ?: 0,
                            rating = r?.averageRating ?: 0.0,
                            reviewCount = r?.reviewCount ?: 0,
                            isAvailableForWork = p?.isAvailable ?: true,
                            acceptsRemoteWork = FreelancerProfileStore.profile.acceptsRemoteWork,
                            completedProjects = completed,
                            // BE belum punya metrik "Amanah"; dipakai rasio project selesai vs dibatalkan.
                            trustPercent = if (completed + cancelled > 0) completed * 100 / (completed + cancelled) else 0,
                            experienceYearsLabel = "${totalYears(exps)} thn",
                            skills = skillNames,
                            photoUri = FreelancerProfileStore.profile.photoUri
                        ),
                        hasProfile = p != null
                    )
                    FreelancerProfileStore.replaceExperiences(exps.map { it.toWorkExperience() })
                    val portfolioItems = portfolios.await().map { portfolio ->
                        async {
                            val extras = FreelancerProfileStore.portfolioExtras[portfolio.id]
                            PortfolioItem(
                                id = portfolio.id,
                                title = portfolio.title,
                                category = extras?.first ?: PortfolioCategory.LOGO,
                                description = portfolio.description,
                                link = portfolio.projectUrl.orEmpty(),
                                skills = extras?.second.orEmpty(),
                                coverUri = coverUriOf(portfolio.id)
                            )
                        }
                    }.awaitAll()
                    FreelancerProfileStore.replacePortfolio(portfolioItems)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            availableBalance = wallet.await()?.availableBalance ?: 0,
                            skillOptions = allSkills.await().map { s -> s.name }
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

    private suspend fun coverUriOf(portfolioId: String): String? {
        val media = runCatching { profileRepository.portfolioMedia(portfolioId) }.getOrNull() ?: return null
        val cover = media.firstOrNull { it.isCover } ?: media.minByOrNull { it.sortOrder } ?: return null
        return runCatching {
            fileRepository.downloadToCache(
                "profiles/freelancer/portfolios/$portfolioId/media/${cover.id}/file",
                cover.id,
                "cover.${cover.mimeType.substringAfter('/')}"
            )
        }.getOrNull()
    }

    private fun totalYears(items: List<Experience>): Int {
        val now = Calendar.getInstance()
        val months = items.sumOf { exp ->
            val start = parseMonth(exp.startDate) ?: return@sumOf 0
            val end = exp.endDate?.let(::parseMonth) ?: MonthYear(now.get(Calendar.MONTH) + 1, now.get(Calendar.YEAR))
            (end.totalMonths - start.totalMonths).coerceAtLeast(0)
        }
        return months / 12
    }

    private fun parseMonth(date: String): MonthYear? {
        val parts = date.split("-")
        val year = parts.getOrNull(0)?.toIntOrNull() ?: return null
        val month = parts.getOrNull(1)?.toIntOrNull() ?: return null
        return MonthYear(month, year)
    }

    private fun Experience.toWorkExperience() = WorkExperience(
        id = id,
        position = jobTitle,
        organization = organizationName,
        employmentType = FreelancerProfileStore.experienceTypes[id] ?: EmploymentType.FREELANCE,
        start = parseMonth(startDate) ?: MonthYear(1, 2020),
        end = endDate?.let(::parseMonth),
        description = description
    )

    private fun MonthYear.toDate() = "%04d-%02d-01".format(year, month)

    fun saveProfile(
        headline: String,
        bio: String,
        city: String,
        rate: Long?,
        isAvailable: Boolean,
        skills: List<String>,
        primaryField: String,
        acceptsRemote: Boolean,
        photoUri: String?
    ) = runAction("Profil profesional disimpan") {
        profileRepository.saveFreelancerProfile(
            title = headline,
            city = city,
            bio = bio,
            priceMin = rate,
            priceMax = null,
            availabilityStatus = if (isAvailable) AvailabilityStatus.AVAILABLE else AvailabilityStatus.BUSY
        )
        val known = masterDataRepository.skills()
        val ids = skills.mapNotNull { name -> known.firstOrNull { it.name.equals(name, ignoreCase = true) }?.id }
        if (ids.isNotEmpty()) profileRepository.setMySkills(ids.distinct())
        FreelancerProfileStore.replace(
            FreelancerProfileStore.profile.copy(primaryField = primaryField, acceptsRemoteWork = acceptsRemote, photoUri = photoUri),
            hasProfile = true
        )
    }

    fun savePortfolio(existingId: String?, item: PortfolioItem) = runAction("Portofolio disimpan") {
        val saved = profileRepository.savePortfolio(existingId, item.title, item.description, item.link)
        FreelancerProfileStore.portfolioExtras[saved.id] = item.category to item.skills
        val cover = item.coverUri
        if (cover != null && cover.startsWith("content://")) {
            val media = profileRepository.uploadPortfolioMedia(saved.id, UploadFile(cover, "cover.jpg", null))
            profileRepository.setPortfolioCover(saved.id, media.id)
        }
    }

    fun deletePortfolio(id: String) = runAction("Portofolio dihapus") {
        profileRepository.deletePortfolio(id)
        FreelancerProfileStore.portfolioExtras.remove(id)
    }

    fun saveExperience(item: WorkExperience) = runAction("Pengalaman kerja disimpan") {
        val saved = profileRepository.saveExperience(
            id = item.id.ifBlank { null },
            title = item.position,
            organization = item.organization,
            description = item.description,
            startDate = item.start.toDate(),
            endDate = item.end?.toDate()
        )
        FreelancerProfileStore.experienceTypes[saved.id] = item.employmentType
    }

    fun deleteExperience(id: String) = runAction("Pengalaman kerja dihapus") {
        profileRepository.deleteExperience(id)
    }

    private fun runAction(successMessage: String, block: suspend () -> Unit) {
        if (_uiState.value.isSaving) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                block()
                _uiState.update { it.copy(isSaving = false, message = successMessage, savedCount = it.savedCount + 1) }
                load()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val field = (e as? ApiException)?.fieldErrors?.values?.firstOrNull()
                _uiState.update { it.copy(isSaving = false, message = field ?: e.userMessage()) }
            }
        }
    }

    fun onMessageShown() = _uiState.update { it.copy(message = null) }

    companion object {
        fun factory(
            authRepository: AuthRepository,
            profileRepository: ProfileRepository,
            masterDataRepository: MasterDataRepository,
            freelancerRepository: FreelancerRepository,
            workflowRepository: WorkflowRepository,
            paymentRepository: PaymentRepository,
            fileRepository: FileRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                FreelancerProfileViewModel(
                    authRepository, profileRepository, masterDataRepository, freelancerRepository,
                    workflowRepository, paymentRepository, fileRepository
                )
            }
        }
    }
}

/** Factory bawaan untuk semua layar profil freelancer. */
fun freelancerProfileViewModelFactory(): ViewModelProvider.Factory {
    val c = com.example.proyek_papb_fe.core.di.AppContainer
    return FreelancerProfileViewModel.factory(
        c.authRepository, c.profileRepository, c.masterDataRepository, c.freelancerRepository,
        c.workflowRepository, c.paymentRepository, c.fileRepository
    )
}
