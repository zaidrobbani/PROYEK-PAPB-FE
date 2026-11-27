package com.example.proyek_papb_fe.feature.freelancer.dashboard.viewmodel

import com.example.proyek_papb_fe.core.data.repository.ProfileRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.AuthRepository
import com.example.proyek_papb_fe.core.data.repository.MasterDataRepository
import com.example.proyek_papb_fe.core.data.repository.NotificationRepository
import com.example.proyek_papb_fe.core.data.repository.PaymentRepository
import com.example.proyek_papb_fe.core.data.repository.ProjectRepository
import com.example.proyek_papb_fe.core.data.repository.WorkflowRepository
import com.example.proyek_papb_fe.core.domain.model.ProjectStatusCode
import com.example.proyek_papb_fe.core.util.rupiah
import com.example.proyek_papb_fe.feature.freelancer.dashboard.component.DashboardProject
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
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

data class FreelancerDashboardUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val userName: String = "",
    val availableBalance: Long = 0,
    val activeProject: DashboardProject? = null,
    val recommendations: List<DashboardProject> = emptyList(),
    val hasSkills: Boolean? = null,
    val hasUnreadNotification: Boolean = false
)

class FreelancerDashboardViewModel(
    private val authRepository: AuthRepository,
    private val paymentRepository: PaymentRepository,
    private val workflowRepository: WorkflowRepository,
    private val projectRepository: ProjectRepository,
    private val masterDataRepository: MasterDataRepository,
    private val notificationRepository: NotificationRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        FreelancerDashboardUiState(userName = authRepository.currentUser()?.name.orEmpty().substringBefore(" "))
    )
    val uiState: StateFlow<FreelancerDashboardUiState> = _uiState.asStateFlow()

    fun load() {
        fetch(showLoading = true)
    }

    fun refresh(): Job = fetch(showLoading = false)

    private fun fetch(showLoading: Boolean): Job {
        return viewModelScope.launch {
            _uiState.update { it.copy(isLoading = showLoading || it.isLoading, errorMessage = null) }
            try {
                coroutineScope {
                    val wallet = async { runCatching { paymentRepository.wallet() }.getOrNull() }
                    val projects = async { workflowRepository.myProjects() }
                    val recommended = async { projectRepository.recommendedProjects(limit = RecommendationCount) }
                    val unread = async { runCatching { notificationRepository.unreadCount() }.getOrDefault(0) }
                    val skills = async { runCatching { profileRepository.mySkillIds() }.getOrDefault(emptyList()) }
                    val categories = runCatching { masterDataRepository.categories() }.getOrDefault(emptyList())
                        .associate { it.id to it.name }
                    val now = System.currentTimeMillis()
                    val active = projects.await().items
                        .filter { it.project.status in ActiveStatuses }
                        .maxByOrNull { it.project.startedAtMillis ?: 0L }
                        ?.let { item ->
                            val deadline = item.project.deadlineMillis
                            DashboardProject(
                                id = item.project.id,
                                title = item.project.title,
                                category = categories[item.project.categoryId] ?: item.project.categoryId,
                                budget = rupiah(item.agreement?.agreedPrice ?: item.project.budget),
                                status = item.project.statusLabel,
                                progress = item.progressPercent?.roundToInt() ?: 0,
                                owner = item.counterpart?.let { c -> listOfNotNull(c.subtitle, c.name, c.city).joinToString(", ") }.orEmpty(),
                                daysRemaining = deadline?.let { TimeUnit.MILLISECONDS.toDays(it - now).toInt().coerceAtLeast(0) }
                            )
                        }
                    val recommendations = recommended.await().take(RecommendationCount).map { rec ->
                        async {
                            val project = runCatching { projectRepository.detail(rec.projectId) }.getOrNull()
                            DashboardProject(
                                id = rec.projectId,
                                title = rec.title,
                                description = project?.description.orEmpty(),
                                category = categories[rec.categoryId] ?: rec.categoryId,
                                budget = project?.let { rupiah(it.budget) }.orEmpty(),
                                status = "Terbuka",
                                matchPercentage = rec.match.scorePercent.roundToInt(),
                                imageEmoji = emojiFor(rec.categoryId)
                            )
                        }
                    }.awaitAll()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            availableBalance = wallet.await()?.availableBalance ?: 0,
                            activeProject = active,
                            recommendations = recommendations,
                            hasSkills = skills.await().isNotEmpty(),
                            hasUnreadNotification = unread.await() > 0
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

    private fun emojiFor(categoryId: String): String = when (categoryId) {
        "graphic_design" -> "🎨"
        "product_photography" -> "📷"
        "website", "other_technology" -> "🛒"
        else -> "📱"
    }

    companion object {
        private const val RecommendationCount = 3
        private val ActiveStatuses = setOf(
            ProjectStatusCode.SIAP_DIKERJAKAN,
            ProjectStatusCode.SEDANG_DIKERJAKAN,
            ProjectStatusCode.MENUNGGU_REVIEW,
            ProjectStatusCode.REVISI
        )

        fun factory(
            authRepository: AuthRepository,
            paymentRepository: PaymentRepository,
            workflowRepository: WorkflowRepository,
            projectRepository: ProjectRepository,
            masterDataRepository: MasterDataRepository,
            notificationRepository: NotificationRepository,
            profileRepository: ProfileRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                FreelancerDashboardViewModel(
                    authRepository, paymentRepository, workflowRepository, projectRepository, masterDataRepository,
                    notificationRepository, profileRepository
                )
            }
        }
    }
}
