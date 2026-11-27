package com.example.proyek_papb_fe.feature.freelancer.revision.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.model.RevisionFocus
import com.example.proyek_papb_fe.core.data.model.RevisionFocusTone
import com.example.proyek_papb_fe.core.data.model.RevisionPoint
import com.example.proyek_papb_fe.core.data.model.RevisionRequest
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.SubmissionRepository
import com.example.proyek_papb_fe.core.data.repository.WorkflowRepository
import com.example.proyek_papb_fe.core.domain.model.SubmissionDecision
import com.example.proyek_papb_fe.core.ui.components.initialsOf
import com.example.proyek_papb_fe.core.util.formatShortDate
import com.example.proyek_papb_fe.core.util.formatTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

data class RevisionRequestUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val request: RevisionRequest? = null
)

/** Permintaan revisi = review `REVISION_REQUESTED` pada kiriman terbaru (`GET /projects/{id}/submissions`). */
class RevisionRequestViewModel(
    private val projectId: String,
    private val submissionRepository: SubmissionRepository,
    private val workflowRepository: WorkflowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RevisionRequestUiState())
    val uiState: StateFlow<RevisionRequestUiState> = _uiState.asStateFlow()

    init {
        load()
    }

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
                    val submissions = async { submissionRepository.submissions(projectId) }
                    val detail = async { workflowRepository.detail(projectId) }
                    val list = submissions.await()
                    val d = detail.await()
                    val revised = list.items
                        .filter { it.review?.decision == SubmissionDecision.REVISION_REQUESTED }
                        .maxByOrNull { it.versionNumber }
                    val review = revised?.review
                    if (revised == null || review == null) {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Belum ada permintaan revisi untuk project ini.") }
                        return@coroutineScope
                    }
                    val now = System.currentTimeMillis()
                    val requestedAt = review.reviewedAtMillis ?: now
                    val deadline = d.agreement?.agreedDeadlineMillis
                    val start = d.project.startedAtMillis
                    val progress = if (deadline != null && start != null && deadline > start) {
                        ((now - start).toFloat() / (deadline - start)).coerceIn(0f, 1f)
                    } else 0f
                    val ownerName = d.umkm.name
                    val reference = revised.files.firstOrNull()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            request = RevisionRequest(
                                projectId = projectId,
                                receivedLabel = "${formatShortDate(requestedAt)}, ${formatTime(requestedAt)} WIB",
                                clientName = ownerName,
                                clientInitials = initialsOf(ownerName),
                                umkmName = d.umkm.subtitle ?: ownerName,
                                isClientVerified = true,
                                projectTitle = d.project.title,
                                deadlineProgress = progress,
                                deadlineLabel = deadline?.let { dl ->
                                    "${formatShortDate(dl)} (Tersisa ${TimeUnit.MILLISECONDS.toDays(dl - now).coerceAtLeast(0)} hari)"
                                } ?: "-",
                                focus = review.focusAreas.map { area ->
                                    RevisionFocus(
                                        area,
                                        when {
                                            area.contains("Tipografi", true) -> RevisionFocusTone.TYPOGRAPHY
                                            area.contains("Ilustrasi", true) || area.contains("Motif", true) -> RevisionFocusTone.PATTERN
                                            else -> RevisionFocusTone.GENERAL
                                        }
                                    )
                                },
                                points = review.items.map { item ->
                                    RevisionPoint(title = item.title, area = item.area ?: "Keseluruhan", instruction = item.detail)
                                }.ifEmpty {
                                    listOf(RevisionPoint(title = "Catatan revisi", area = "Keseluruhan", instruction = review.note.orEmpty()))
                                },
                                referenceFileName = reference?.fileName ?: revised.versionLabel,
                                referenceFileMeta = revised.versionLabel,
                                nextDraftVersion = "v${revised.versionNumber + 1}.0",
                                clientGreeting = ownerName.substringBefore(' '),
                                draftVersion = revised.versionNumber,
                                requestedAtMillis = requestedAt
                            )
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

    companion object {
        fun factory(
            projectId: String,
            submissionRepository: SubmissionRepository,
            workflowRepository: WorkflowRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { RevisionRequestViewModel(projectId, submissionRepository, workflowRepository) }
        }
    }
}
