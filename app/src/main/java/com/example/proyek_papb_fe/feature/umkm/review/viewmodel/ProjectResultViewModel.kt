package com.example.proyek_papb_fe.feature.umkm.review.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.FileRepository
import com.example.proyek_papb_fe.core.data.repository.SubmissionRepository
import com.example.proyek_papb_fe.core.data.repository.WorkflowRepository
import com.example.proyek_papb_fe.core.domain.model.ProjectStatusCode
import com.example.proyek_papb_fe.core.domain.model.Review
import com.example.proyek_papb_fe.core.domain.model.ReviewHighlightCode
import com.example.proyek_papb_fe.core.domain.model.ReviewInput
import com.example.proyek_papb_fe.core.domain.model.RevisionItem
import com.example.proyek_papb_fe.core.domain.model.Submission
import com.example.proyek_papb_fe.core.domain.model.SubmissionFile
import com.example.proyek_papb_fe.core.domain.model.WorkflowDetail
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ResultStage { WAITING_REVIEW, REVISION_REQUESTED, COMPLETED, NOT_READY }

data class ProjectResultUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val detail: WorkflowDetail? = null,
    val latest: Submission? = null,
    val review: Review? = null,
    /** id file → Uri `file://` hasil unduhan (untuk pratinjau gambar). */
    val localUris: Map<String, String> = emptyMap(),
    val isSubmitting: Boolean = false,
    val message: String? = null,
    val completed: Boolean = false,
    val reviewSent: Boolean = false
) {
    val stage: ResultStage
        get() = when (detail?.project?.status) {
            ProjectStatusCode.SELESAI -> ResultStage.COMPLETED
            ProjectStatusCode.REVISI -> ResultStage.REVISION_REQUESTED
            ProjectStatusCode.MENUNGGU_REVIEW -> if (latest != null) ResultStage.WAITING_REVIEW else ResultStage.NOT_READY
            else -> ResultStage.NOT_READY
        }
}

/** Review hasil (UMKM): minta revisi, setujui hasil (project SELESAI + dana RELEASED dalam satu transaksi BE), ulasan. */
class ProjectResultViewModel(
    private val projectId: String,
    private val workflowRepository: WorkflowRepository,
    private val submissionRepository: SubmissionRepository,
    private val fileRepository: FileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectResultUiState())
    val uiState: StateFlow<ProjectResultUiState> = _uiState.asStateFlow()

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
                    val detail = async { workflowRepository.detail(projectId) }
                    val submissions = async { runCatching { submissionRepository.submissions(projectId) }.getOrNull() }
                    val d = detail.await()
                    val review = if (d.hasReview) runCatching { submissionRepository.review(projectId) }.getOrNull() else null
                    val latest = submissions.await()?.latest
                    _uiState.update { it.copy(isLoading = false, detail = d, latest = latest, review = review) }
                    latest?.files?.filter { it.mimeType.startsWith("image/") }?.forEach { preview(it) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.userMessage()) }
            }
        }
    }

    private suspend fun preview(file: SubmissionFile) {
        val uri = runCatching { fileRepository.downloadToCache(file.downloadPath, file.id, file.fileName) }.getOrNull() ?: return
        _uiState.update { it.copy(localUris = it.localUris + (file.id to uri)) }
    }

    fun download(fileId: String) {
        val file = _uiState.value.latest?.files?.firstOrNull { it.id == fileId } ?: return
        viewModelScope.launch {
            try {
                val cached = fileRepository.downloadToCache(file.downloadPath, file.id, file.fileName)
                val location = fileRepository.saveToDownloads(cached, file.fileName, file.mimeType)
                _uiState.update { it.copy(localUris = it.localUris + (file.id to cached), message = "${file.fileName} tersimpan di $location") }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(message = e.userMessage()) }
            }
        }
    }

    fun downloadAll() {
        val files = _uiState.value.latest?.files.orEmpty()
        if (files.isEmpty()) {
            _uiState.update { it.copy(message = "Tidak ada berkas untuk diunduh") }
            return
        }
        files.forEach { download(it.id) }
    }

    fun requestRevision(focusAreas: List<String>, items: List<RevisionItem>, note: String? = null) {
        val submission = _uiState.value.latest ?: return
        if (_uiState.value.isSubmitting) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            try {
                submissionRepository.requestRevision(projectId, submission.id, note, focusAreas, items)
                _uiState.update { it.copy(isSubmitting = false, message = "Permintaan revisi dikirim ke freelancer") }
                load()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSubmitting = false, message = e.userMessage()) }
            }
        }
    }

    /** Ulasan opsional ikut dikirim bersama persetujuan bila rating diisi. */
    fun approve(rating: Int, reviewText: String) {
        val submission = _uiState.value.latest ?: return
        if (_uiState.value.isSubmitting) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            try {
                val review = if (rating in 1..5) ReviewInput(rating, reviewText, emptyList()) else null
                submissionRepository.approve(projectId, submission.id, review)
                _uiState.update { it.copy(isSubmitting = false, completed = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSubmitting = false, message = e.userMessage()) }
                load()
            }
        }
    }

    fun submitReview(rating: Int, highlightLabels: Set<String>, text: String, highlightOptions: List<String>) {
        if (_uiState.value.isSubmitting || rating !in 1..5) return
        // Urutan opsi Figma sama dengan enum BE review_highlights.
        val codes = highlightLabels.mapNotNull { label ->
            when (highlightOptions.indexOf(label)) {
                0 -> ReviewHighlightCode.RESPONSIF_KOMUNIKATIF
                1 -> ReviewHighlightCode.HASIL_BERKUALITAS
                2 -> ReviewHighlightCode.TEPAT_WAKTU
                3 -> ReviewHighlightCode.PAHAM_KEBUTUHAN
                else -> null
            }
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            try {
                val review = submissionRepository.createReview(projectId, ReviewInput(rating, text, codes))
                _uiState.update { it.copy(isSubmitting = false, review = review, reviewSent = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSubmitting = false, message = e.userMessage()) }
            }
        }
    }

    fun showMessage(message: String) = _uiState.update { it.copy(message = message) }

    fun onMessageShown() = _uiState.update { it.copy(message = null) }

    fun onCompletedHandled() = _uiState.update { it.copy(completed = false) }

    fun onReviewSentHandled() = _uiState.update { it.copy(reviewSent = false) }

    companion object {
        fun factory(
            projectId: String,
            workflowRepository: WorkflowRepository,
            submissionRepository: SubmissionRepository,
            fileRepository: FileRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { ProjectResultViewModel(projectId, workflowRepository, submissionRepository, fileRepository) }
        }
    }
}
