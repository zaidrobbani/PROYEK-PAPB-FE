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

/** Satu project (percakapan) dengan UMKM yang sama, untuk pilihan "Kirim ke". */
data class ChatThread(val conversationId: String, val projectId: String?, val projectTitle: String, val statusLabel: String)

data class ChatRoomVmState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    /** Percakapan tujuan yang sedang dipilih (konteks project, kirim pesan, unggah draf, batal). */
    val overview: ConversationOverview? = null,
    val targetConversationId: String = "",
    /** Semua project dengan UMKM ini; lebih dari satu = tampil penanda project & pilihan "Kirim ke". */
    val threads: List<ChatThread> = emptyList(),
    /** Pesan semua percakapan dengan UMKM ini, urut waktu. */
    val messages: List<Message> = emptyList(),
    /** Detail alur project tujuan (hanya bila user pihak dalam kesepakatan). */
    val workflow: WorkflowDetail? = null,
    val submissions: SubmissionList? = null,
    val isSending: Boolean = false,
    val isSubmittingResult: Boolean = false,
    val message: String? = null,
    val resultSubmitted: Boolean = false,
    val cancelled: Boolean = false
)

/**
 * Ruang chat freelancer dengan satu UMKM, seperti chat UMKM: BE menyimpan percakapan per project, jadi semua
 * percakapan dengan UMKM yang sama digabung (masing-masing WebSocket sendiri). Pesan baru, lampiran, unggah hasil,
 * dan pembatalan memakai percakapan tujuan yang dipilih (awalnya percakapan yang dibuka).
 */
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
    /** true saat semua WebSocket percakapan tersambung; layar hanya polling bila false. */
    val isRealtime: StateFlow<Boolean> = _isRealtime.asStateFlow()

    private val outgoing = OutgoingAttachmentQueue(viewModelScope, { _uiState.value.targetConversationId }, chatRepository, ::addMessage)
    /** Foto/dokumen yang sedang dikirim atau gagal terkirim (tampil di akhir chat). */
    val pendingAttachments: StateFlow<List<OutgoingAttachment>> = outgoing.items

    fun sendAttachment(file: PickedChatFile, caption: String, asImage: Boolean) = outgoing.enqueue(file, caption, asImage)

    fun retryAttachment(localId: String) = outgoing.retry(localId)

    fun discardAttachment(localId: String) = outgoing.discard(localId)

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
                // Layar sedang terbuka: pesan masuk langsung dianggap dibaca.
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
            is ChatRealtimeEvent.Error -> _uiState.update { it.copy(message = event.message) }
        }
    }

    override fun onCleared() {
        sessions.values.forEach { it.close() }
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = it.overview == null, errorMessage = null) }
            try {
                val opened = overviewRepository.overview(initialConversationId)
                overviews[opened.conversation.id] = opened
                // Percakapan project lain dengan UMKM yang sama.
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

    /** Detail alur & kiriman hasil untuk project tujuan saja. */
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

    /** Ganti project tujuan; konteks project (status, negosiasi, unggah draf) ikut berganti. */
    fun selectTarget(conversationId: String) {
        val overview = overviews[conversationId] ?: return
        if (conversationId == _uiState.value.targetConversationId) return
        _uiState.update { it.copy(targetConversationId = conversationId, overview = overview, workflow = null, submissions = null) }
        viewModelScope.launch { loadTargetDetails() }
    }

    /** Cadangan saat WebSocket terputus: dipanggil berkala dari layar. */
    fun refreshMessages() {
        val ids = overviews.keys.toList()
        viewModelScope.launch {
            ids.forEach { id ->
                val latest = runCatching { chatRepository.messages(id).items }.getOrNull() ?: return@forEach
                val current = _uiState.value.messages.filter { it.conversationId == id }
                // Bandingkan seluruh isi agar status dibaca ikut ter-update saat WebSocket terputus.
                if (latest != current) {
                    _uiState.update { state ->
                        state.copy(messages = (state.messages.filterNot { it.conversationId == id } + latest).sortedBy { it.sentAtMillis })
                    }
                    if (latest.lastOrNull()?.id != current.lastOrNull()?.id) runCatching { chatRepository.markRead(id) }
                }
            }
        }
    }

    fun send(text: String) {
        val body = text.trim()
        if (body.isEmpty() || _uiState.value.isSending) return
        val target = _uiState.value.targetConversationId
        // Lewat WebSocket: pesan kembali sebagai event `message` (ke pengirim & lawan bicara). REST hanya cadangan.
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
