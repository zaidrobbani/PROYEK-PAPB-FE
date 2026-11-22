package com.example.proyek_papb_fe.feature.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.ChatOverviewRepository
import com.example.proyek_papb_fe.core.data.repository.ChatRepository
import com.example.proyek_papb_fe.core.data.repository.ConversationOverview
import com.example.proyek_papb_fe.core.data.repository.SubmissionRepository
import com.example.proyek_papb_fe.core.data.repository.WorkflowRepository
import com.example.proyek_papb_fe.core.domain.model.Message
import com.example.proyek_papb_fe.core.domain.model.SubmissionList
import com.example.proyek_papb_fe.core.domain.model.UploadFile
import com.example.proyek_papb_fe.core.domain.model.WorkflowDetail
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatRoomVmState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val overview: ConversationOverview? = null,
    val messages: List<Message> = emptyList(),
    /** Detail alur project percakapan (hanya bila user pihak dalam kesepakatan). */
    val workflow: WorkflowDetail? = null,
    val submissions: SubmissionList? = null,
    val isSending: Boolean = false,
    val isSubmittingResult: Boolean = false,
    val message: String? = null,
    val resultSubmitted: Boolean = false,
    val cancelled: Boolean = false
)

/**
 * Ruang chat: pesan teks (`/conversations/{id}/messages`), konteks project dari detail alur,
 * dan kirim hasil kerja freelancer (`POST /projects/{id}/submissions`). BE belum mendukung lampiran chat.
 */
class ChatRoomViewModel(
    private val conversationId: String,
    private val chatRepository: ChatRepository,
    private val overviewRepository: ChatOverviewRepository,
    private val workflowRepository: WorkflowRepository,
    private val submissionRepository: SubmissionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatRoomVmState())
    val uiState: StateFlow<ChatRoomVmState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                coroutineScope {
                    val overview = overviewRepository.overview(conversationId)
                    val projectId = overview.conversation.projectId
                    val workflow = async { projectId?.let { runCatching { workflowRepository.detail(it) }.getOrNull() } }
                    val submissions = async { projectId?.let { runCatching { submissionRepository.submissions(it) }.getOrNull() } }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            overview = overview,
                            messages = overview.messages,
                            workflow = workflow.await(),
                            submissions = submissions.await()
                        )
                    }
                }
                runCatching { chatRepository.markRead(conversationId) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.userMessage()) }
            }
        }
    }

    /** Dipanggil berkala dari layar (BE belum punya websocket/push). */
    fun refreshMessages() {
        viewModelScope.launch {
            val latest = runCatching { chatRepository.messages(conversationId).items }.getOrNull() ?: return@launch
            if (latest.lastOrNull()?.id != _uiState.value.messages.lastOrNull()?.id) {
                _uiState.update { it.copy(messages = latest) }
                runCatching { chatRepository.markRead(conversationId) }
            }
        }
    }

    fun send(text: String) {
        val body = text.trim()
        if (body.isEmpty() || _uiState.value.isSending) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true) }
            try {
                val sent = chatRepository.send(conversationId, body)
                _uiState.update { it.copy(isSending = false, messages = it.messages + sent) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSending = false, message = e.userMessage()) }
            }
        }
    }

    fun submitResult(files: List<UploadFile>, note: String) {
        val projectId = _uiState.value.overview?.conversation?.projectId ?: return
        if (_uiState.value.isSubmittingResult) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingResult = true) }
            try {
                val submission = submissionRepository.submit(projectId, files, note, null)
                _uiState.update {
                    it.copy(
                        isSubmittingResult = false,
                        resultSubmitted = true,
                        message = "${submission.versionLabel} dikirim ke klien untuk ditinjau."
                    )
                }
                load()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isSubmittingResult = false, message = e.userMessage()) }
            }
        }
    }

    fun cancelProject(reasonCode: String, note: String?) {
        val projectId = _uiState.value.overview?.conversation?.projectId ?: return
        viewModelScope.launch {
            try {
                workflowRepository.cancel(projectId, reasonCode, note)
                _uiState.update { it.copy(cancelled = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(message = e.userMessage()) }
            }
        }
    }

    fun showMessage(message: String) = _uiState.update { it.copy(message = message) }

    fun onMessageShown() = _uiState.update { it.copy(message = null) }

    fun onResultSubmittedHandled() = _uiState.update { it.copy(resultSubmitted = false) }

    fun onCancelledHandled() = _uiState.update { it.copy(cancelled = false) }

    companion object {
        fun factory(
            conversationId: String,
            chatRepository: ChatRepository,
            overviewRepository: ChatOverviewRepository,
            workflowRepository: WorkflowRepository,
            submissionRepository: SubmissionRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ChatRoomViewModel(conversationId, chatRepository, overviewRepository, workflowRepository, submissionRepository)
            }
        }
    }
}
