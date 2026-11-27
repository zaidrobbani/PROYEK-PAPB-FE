package com.example.proyek_papb_fe.feature.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.ChatRealtimeEvent
import com.example.proyek_papb_fe.core.data.remote.ChatRealtimeSession
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.data.repository.ChatOverviewRepository
import com.example.proyek_papb_fe.core.data.repository.ChatRepository
import com.example.proyek_papb_fe.core.data.repository.ConversationOverview
import com.example.proyek_papb_fe.core.data.repository.SubmissionRepository
import com.example.proyek_papb_fe.core.data.repository.WorkflowRepository
import com.example.proyek_papb_fe.core.data.source.PickedChatFile
import com.example.proyek_papb_fe.core.domain.model.Message
import com.example.proyek_papb_fe.core.domain.model.OutgoingAttachment
import com.example.proyek_papb_fe.core.domain.model.SubmissionList
import com.example.proyek_papb_fe.core.domain.model.UploadFile
import com.example.proyek_papb_fe.core.domain.model.WorkflowDetail
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatThread(val conversationId: String, val projectId: String?, val projectTitle: String, val statusLabel: String)

data class ChatRoomVmState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val overview: ConversationOverview? = null,
    val targetConversationId: String = "",
    val threads: List<ChatThread> = emptyList(),
    val messages: List<Message> = emptyList(),
    val workflow: WorkflowDetail? = null,
    val submissions: SubmissionList? = null,
    val isSending: Boolean = false,
    val isSubmittingResult: Boolean = false,
    val message: String? = null,
    val resultSubmitted: Boolean = false,
    val cancelled: Boolean = false
)

class ChatRoomViewModel(
    private val initialConversationId: String,
    private val chatRepository: ChatRepository,
    private val overviewRepository: ChatOverviewRepository,
    private val workflowRepository: WorkflowRepository,
    private val submissionRepository: SubmissionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatRoomVmState(targetConversationId = initialConversationId))
    val uiState: StateFlow<ChatRoomVmState> = _uiState.asStateFlow()

    private val overviews = mutableMapOf<String, ConversationOverview>()
    private val sessions = mutableMapOf<String, ChatRealtimeSession>()
    private var connectionJob: Job? = null

    private val _isRealtime = MutableStateFlow(false)
    val isRealtime: StateFlow<Boolean> = _isRealtime.asStateFlow()

    private val outgoing = OutgoingAttachmentQueue(viewModelScope, { _uiState.value.targetConversationId }, chatRepository, ::addMessage)
    val pendingAttachments: StateFlow<List<OutgoingAttachment>> = outgoing.items

    fun sendAttachment(file: PickedChatFile, caption: String, asImage: Boolean) = outgoing.enqueue(file, caption, asImage)

    fun retryAttachment(localId: String) = outgoing.retry(localId)

    fun discardAttachment(localId: String) = outgoing.discard(localId)

    private val selection = MessageSelection()
    val selectedMessageIds: StateFlow<Set<String>> = selection.selected
    val isDeletingMessages: StateFlow<Boolean> = selection.isDeleting

    fun toggleMessageSelection(messageId: String) {
        if (_uiState.value.messages.any { it.id == messageId }) selection.toggle(messageId)
    }

    fun clearMessageSelection() = selection.clear()

    fun canDeleteSelectedForEveryone(): Boolean {
        val ids = selection.selected.value
        val myId = AppContainer.currentUserId
        return ids.isNotEmpty() && _uiState.value.messages.filter { it.id in ids }.let { picked ->
            picked.size == ids.size && picked.all { it.canDeleteForEveryone(myId) }
        }
    }

    fun deleteSelectedMessages(forEveryone: Boolean) {
        val conversationOf = _uiState.value.messages.associate { it.id to it.conversationId }
        viewModelScope.launch {
            try {
                selection.delete(chatRepository, conversationOf, forEveryone) { _, ids, deletedAt ->
                    applyDeletion(ids.toSet(), if (forEveryone) deletedAt else null)
                }
                _uiState.update { it.copy(message = if (forEveryone) "Pesan dihapus untuk semua orang" else "Pesan dihapus") }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(message = e.userMessage()) }
            }
        }
    }

    private fun applyDeletion(ids: Set<String>, deletedAtMillis: Long?) = _uiState.update { state ->
        state.copy(
            messages = if (deletedAtMillis == null) state.messages.filterNot { it.id in ids }
            else state.messages.map { if (it.id in ids && !it.isDeleted) it.asDeleted(deletedAtMillis) else it }
        )
    }

    private fun addMessage(message: Message) = _uiState.update { state ->
        if (state.messages.any { it.id == message.id }) state
        else state.copy(messages = (state.messages + message).sortedBy { it.sentAtMillis })
    }

    init {
        load()
    }

    private fun connect(conversationIds: List<String>) {
        conversationIds.filterNot(sessions::containsKey).forEach { id ->
            val session = chatRepository.realtime(id)
            sessions[id] = session
            session.start(viewModelScope)
            viewModelScope.launch { session.events.collect { onRealtimeEvent(id, session, it) } }
        }
        connectionJob?.cancel()
        val flows = sessions.values.map { it.isConnected }
        connectionJob = viewModelScope.launch {
            combine(flows) { states -> states.all { it } }.collect { _isRealtime.value = it }
        }
    }

    private fun onRealtimeEvent(conversationId: String, session: ChatRealtimeSession, event: ChatRealtimeEvent) {
        val myId = AppContainer.currentUserId
        when (event) {
            is ChatRealtimeEvent.NewMessage -> {
                val message = event.message
                addMessage(message)
                if (message.senderUserId != myId && !session.markRead()) {
                    viewModelScope.launch { runCatching { chatRepository.markRead(conversationId) } }
                }
            }
            is ChatRealtimeEvent.Read -> if (event.readerUserId != myId) {
                _uiState.update { state ->
                    state.copy(messages = state.messages.map {
                        if (it.conversationId == conversationId && it.senderUserId == myId && it.readAtMillis == null) {
                            it.copy(readAtMillis = event.readAtMillis)
                        } else it
                    })
                }
            }
            is ChatRealtimeEvent.MessagesDeleted -> applyDeletion(event.messageIds.toSet(), event.deletedAtMillis)
            is ChatRealtimeEvent.Error -> _uiState.update { it.copy(message = event.message) }
        }
    }

    override fun onCleared() {
        sessions.values.forEach { it.close() }
    }

    fun load(): Job {
        return viewModelScope.launch {
            _uiState.update { it.copy(isLoading = it.overview == null, errorMessage = null) }
            try {
                val opened = overviewRepository.overview(initialConversationId)
                overviews[opened.conversation.id] = opened
                val others = runCatching {
                    chatRepository.conversations()
                        .filter { it.umkmId == opened.conversation.umkmId && it.id != opened.conversation.id }
                }.getOrDefault(emptyList())
                overviewRepository.overviews(others).forEach { overviews[it.conversation.id] = it }

                val target = _uiState.value.targetConversationId.takeIf(overviews::containsKey) ?: initialConversationId
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        overview = overviews[target],
                        targetConversationId = target,
                        threads = threadsOf(overviews.values),
                        messages = overviews.values.flatMap { o -> o.messages }.distinctBy { m -> m.id }.sortedBy { m -> m.sentAtMillis }
                    )
                }
                connect(overviews.keys.toList())
                overviews.keys.forEach { id -> runCatching { chatRepository.markRead(id) } }
                loadTargetDetails()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.userMessage()) }
            }
        }
    }

    private fun threadsOf(items: Collection<ConversationOverview>): List<ChatThread> = items
        .sortedByDescending { it.lastMessage?.sentAtMillis ?: it.conversation.lastMessageAtMillis ?: 0L }
        .map {
            ChatThread(
                conversationId = it.conversation.id,
                projectId = it.conversation.projectId,
                projectTitle = it.projectTitle ?: "Percakapan umum",
                statusLabel = it.workflow?.project?.statusLabel
                    ?: it.projectStatus?.lowercase()?.split('_')?.joinToString(" ") { w -> w.replaceFirstChar(Char::uppercase) }
                    ?: "-"
            )
        }

    private suspend fun loadTargetDetails() {
        val projectId = _uiState.value.overview?.conversation?.projectId
        val (workflow, submissions) = coroutineScope {
            val workflow = async { projectId?.let { runCatching { workflowRepository.detail(it) }.getOrNull() } }
            val submissions = async { projectId?.let { runCatching { submissionRepository.submissions(it) }.getOrNull() } }
            workflow.await() to submissions.await()
        }
        if (_uiState.value.overview?.conversation?.projectId == projectId) {
            _uiState.update { it.copy(workflow = workflow, submissions = submissions) }
        }
    }

    fun selectTarget(conversationId: String) {
        val overview = overviews[conversationId] ?: return
        if (conversationId == _uiState.value.targetConversationId) return
        _uiState.update { it.copy(targetConversationId = conversationId, overview = overview, workflow = null, submissions = null) }
        viewModelScope.launch { loadTargetDetails() }
    }

    fun refreshMessages() {
        val ids = overviews.keys.toList()
        viewModelScope.launch {
            ids.forEach { id ->
                val latest = runCatching { chatRepository.messages(id).items }.getOrNull() ?: return@forEach
                val current = _uiState.value.messages.filter { it.conversationId == id }
                if (latest != current) {
                    _uiState.update { state ->
                        state.copy(messages = (state.messages.filterNot { it.conversationId == id } + latest).sortedBy { it.sentAtMillis })
                    }
                    selection.retain(_uiState.value.messages.map { it.id }.toSet())
                    if (latest.lastOrNull()?.id != current.lastOrNull()?.id) runCatching { chatRepository.markRead(id) }
                }
            }
        }
    }

    fun send(text: String) {
        val body = text.trim()
        if (body.isEmpty() || _uiState.value.isSending) return
        val target = _uiState.value.targetConversationId
        if (sessions[target]?.sendMessage(body) == true) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true) }
            try {
                val sent = chatRepository.send(target, body)
                _uiState.update { it.copy(isSending = false) }
                addMessage(sent)
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
