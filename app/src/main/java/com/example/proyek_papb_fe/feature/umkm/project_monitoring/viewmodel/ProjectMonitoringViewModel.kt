package com.example.proyek_papb_fe.feature.umkm.project_monitoring.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.ChatRepository
import com.example.proyek_papb_fe.core.data.repository.MasterDataRepository
import com.example.proyek_papb_fe.core.data.repository.WorkflowRepository
import com.example.proyek_papb_fe.core.domain.model.DelayRisk as BeDelayRisk
import com.example.proyek_papb_fe.core.util.DateTimeParser
import com.example.proyek_papb_fe.feature.umkm.data.DelayRisk
import com.example.proyek_papb_fe.feature.umkm.data.ProjectMonitoringResult
import com.example.proyek_papb_fe.feature.umkm.data.UmkmProject
import com.example.proyek_papb_fe.feature.umkm.data.toUmkmProject
import com.example.proyek_papb_fe.feature.umkm.data.utcMillisToDate
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.roundToInt

data class ProjectMonitoringUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val project: UmkmProject? = null,
    val result: ProjectMonitoringResult? = null,
    val loadedAtMillis: Long = System.currentTimeMillis(),
    val isOpeningChat: Boolean = false,
    val chatConversationId: String? = null,
    val message: String? = null
)

class ProjectMonitoringViewModel(
    private val projectId: String,
    private val workflowRepository: WorkflowRepository,
    private val chatRepository: ChatRepository,
    private val masterDataRepository: MasterDataRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProjectMonitoringUiState())
    val uiState: StateFlow<ProjectMonitoringUiState> = _uiState.asStateFlow()

    private var freelancerId: String? = null

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
                    val plan = async { runCatching { workflowRepository.milestones(projectId) }.getOrNull() }
                    val risk = async { runCatching { workflowRepository.delayRisk(projectId) }.getOrNull() }
                    val d = detail.await()
                    freelancerId = d.freelancer?.id
                    val deadline = d.agreement?.agreedDeadline ?: d.project.deadline
                    val project = d.toUmkmProject(
                        masterDataRepository.categoryName(d.project.categoryId),
                        plan.await()?.items.orEmpty()
                    ).copy(deadlineUtcMillis = DateTimeParser.parseDateUtcMillis(deadline))
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            project = project,
                            result = risk.await()?.let { r -> toResult(project, deadline, r) },
                            loadedAtMillis = System.currentTimeMillis()
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

    private fun toResult(project: UmkmProject, deadline: String, risk: BeDelayRisk): ProjectMonitoringResult? {
        val end = runCatching { LocalDate.parse(deadline) }.getOrNull() ?: return null
        val start = project.startedUtcMillis?.let(::utcMillisToDate) ?: end.minusDays(risk.totalDays.toLong())
        val actual = risk.actualPercent.roundToInt()
        val target = risk.expectedPercent.roundToInt()
        return ProjectMonitoringResult(
            startDate = start,
            deadline = end,
            evaluationDate = LocalDate.now(),
            actualPercent = actual,
            targetPercent = target,
            gapPoints = target - actual,
            risk = when (risk.level) {
                "ON_TRACK" -> DelayRisk.SesuaiTarget
                "AT_RISK" -> DelayRisk.BerisikoTerlambat
                "OVERDUE" -> DelayRisk.Terlambat
                else -> null
            },
            elapsedDays = risk.elapsedDays.toLong(),
            totalDays = risk.totalDays.coerceAtLeast(1).toLong(),
            remainingDays = (risk.totalDays - risk.elapsedDays).toLong(),
            completedMilestones = project.milestones.count { it.isDone },
            totalMilestones = project.milestones.size
        )
    }

    fun openChat() {
        val counterpart = freelancerId
        if (counterpart == null) {
            _uiState.update { it.copy(message = "Belum ada freelancer untuk project ini.") }
            return
        }
        if (_uiState.value.isOpeningChat) return
        viewModelScope.launch {
            _uiState.update { it.copy(isOpeningChat = true) }
            try {
                val conversation = chatRepository.openConversation(counterpart, projectId)
                _uiState.update { it.copy(isOpeningChat = false, chatConversationId = conversation.id) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isOpeningChat = false, message = e.userMessage()) }
            }
        }
    }

    fun onChatOpened() = _uiState.update { it.copy(chatConversationId = null) }

    fun onMessageShown() = _uiState.update { it.copy(message = null) }

    companion object {
        fun factory(
            projectId: String,
            workflowRepository: WorkflowRepository,
            chatRepository: ChatRepository,
            masterDataRepository: MasterDataRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { ProjectMonitoringViewModel(projectId, workflowRepository, chatRepository, masterDataRepository) }
        }
    }
}
