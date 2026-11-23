package com.example.proyek_papb_fe.feature.collaboration_revision_umkm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.ChatRealtimeEvent
import com.example.proyek_papb_fe.core.data.remote.ChatRealtimeSession
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.ChatOverviewRepository
import com.example.proyek_papb_fe.core.data.repository.ChatRepository
import com.example.proyek_papb_fe.core.data.repository.ConversationOverview
import com.example.proyek_papb_fe.core.data.repository.MasterDataRepository
import com.example.proyek_papb_fe.core.data.source.PickedChatFile
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.domain.model.OutgoingAttachment
import com.example.proyek_papb_fe.feature.chat.viewmodel.OutgoingAttachmentQueue
import com.example.proyek_papb_fe.feature.umkm.data.UmkmConversationStore
import com.example.proyek_papb_fe.feature.umkm.data.UmkmNegotiationStore
import com.example.proyek_papb_fe.feature.umkm.data.UmkmProjectStore
import com.example.proyek_papb_fe.feature.umkm.data.toUmkmProject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UmkmChatThreadUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val message: String? = null,
    /** Percakapan (project) tujuan pesan yang dikirim UMKM. */
    val targetConversationId: String
)

/**
 * Ruang chat UMKM dengan satu freelancer. BE menyimpan percakapan per project, jadi semua percakapan
 * dengan freelancer itu dimuat & digabung, masing-masing dengan WebSocket sendiri. Pesan baru dikirim ke
 * percakapan tujuan (awalnya yang dibuka; bisa diganti bila ada beberapa project).
 */
class UmkmChatThreadViewModel(
    private val initialConversationId: String,
    private val overviewRepository: ChatOverviewRepository,
    private val chatRepository: ChatRepository,
    private val masterDataRepository: MasterDataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UmkmChatThreadUiState(targetConversationId = initialConversationId))
    val uiState: StateFlow<UmkmChatThreadUiState> = _uiState.asStateFlow()

    private val myId: String? get() = AppContainer.currentUserId
    private val sessions = mutableMapOf<String, ChatRealtimeSession>()

    private val _conversationIds = MutableStateFlow(setOf(initialConversationId))
    /** Semua percakapan (project) dengan freelancer ini yang sedang ditampilkan di ruang chat. */
    val conversationIds: StateFlow<Set<String>> = _conversationIds.asStateFlow()
    private var connectionJob: Job? = null

    private val _isRealtime = MutableStateFlow(false)
    /** true saat semua WebSocket percakapan tersambung; layar hanya polling bila false. */
    val isRealtime: StateFlow<Boolean> = _isRealtime.asStateFlow()

    private val outgoing = OutgoingAttachmentQueue(viewModelScope, { _uiState.value.targetConversationId }, chatRepository) {
        UmkmConversationStore.append(it, myId)
    }
    /** Foto/dokumen yang sedang dikirim atau gagal terkirim (tampil di akhir chat). */
    val pendingAttachments: StateFlow<List<OutgoingAttachment>> = outgoing.items

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null) }
            try {
                val opened = overviewRepository.overview(initialConversationId)
                store(opened)
                _uiState.update { it.copy(isLoading = false) }
                connect(listOf(initialConversationId))
                markRead(initialConversationId)

                // Percakapan project lain dengan freelancer yang sama.
                val others = chatRepository.conversations()
                    .filter { it.freelancerId == opened.conversation.freelancerId && it.id != initialConversationId }
                overviewRepository.overviews(others).forEach { store(it) }
                connect(others.map { it.id })
                others.forEach { markRead(it.id) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.userMessage()) }
            }
        }
    }

    private suspend fun store(overview: ConversationOverview) {
        UmkmConversationStore.upsert(overview, myId)
        val detail = overview.workflow ?: return
        UmkmNegotiationStore.upsert(overview.conversation.id, detail)
        UmkmProjectStore.upsert(detail.toUmkmProject(masterDataRepository.categoryName(detail.project.categoryId)))
    }

    private fun connect(conversationIds: List<String>) {
        _conversationIds.update { it + conversationIds }
        conversationIds.filterNot(sessions::containsKey).forEach { id ->
            val session = chatRepository.realtime(id)
            sessions[id] = session
            session.start(viewModelScope)
            viewModelScope.launch { session.events.collect { onEvent(id, session, it) } }
        }
        connectionJob?.cancel()
        val flows = sessions.values.map { it.isConnected }
        connectionJob = viewModelScope.launch {
            combine(flows) { states -> states.all { it } }.collect { _isRealtime.value = it }
        }
    }

    private fun onEvent(conversationId: String, session: ChatRealtimeSession, event: ChatRealtimeEvent) {
        when (event) {
            is ChatRealtimeEvent.NewMessage -> {
                UmkmConversationStore.append(event.message, myId)
                // Layar sedang terbuka: pesan masuk langsung dianggap dibaca.
                if (event.message.senderUserId != myId) {
                    if (!session.markRead()) viewModelScope.launch { runCatching { chatRepository.markRead(conversationId) } }
                    UmkmConversationStore.markRead(conversationId)
                }
            }
            is ChatRealtimeEvent.Read ->
                if (event.readerUserId != myId) UmkmConversationStore.markReadByFreelancer(conversationId)
            is ChatRealtimeEvent.Error -> _uiState.update { it.copy(message = event.message) }
        }
    }

    private suspend fun markRead(conversationId: String) {
        runCatching { chatRepository.markRead(conversationId) }
        UmkmConversationStore.markRead(conversationId)
    }

    /** Cadangan saat WebSocket terputus: dipanggil berkala dari layar. */
    fun refreshMessages() {
        val ids = sessions.keys.toList()
        viewModelScope.launch {
            ids.forEach { id ->
                val messages = runCatching { chatRepository.messages(id).items }.getOrNull() ?: return@forEach
                val lastBefore = UmkmConversationStore.messages(id).lastOrNull()?.id
                UmkmConversationStore.replaceMessages(id, messages, myId)
                if (messages.lastOrNull()?.id != lastBefore) markRead(id)
            }
        }
    }

    fun selectTarget(conversationId: String) = _uiState.update { it.copy(targetConversationId = conversationId) }

    fun send(text: String) {
        val body = text.trim()
        if (body.isEmpty()) return
        val target = _uiState.value.targetConversationId
        // Lewat WebSocket bila tersambung; pesan kembali sebagai event `message`. REST hanya cadangan.
        if (sessions[target]?.sendMessage(body) == true) return
        viewModelScope.launch {
            try {
                UmkmConversationStore.append(chatRepository.send(target, body), myId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(message = e.userMessage()) }
            }
        }
    }

    fun sendAttachment(file: PickedChatFile, caption: String, asImage: Boolean) = outgoing.enqueue(file, caption, asImage)

    fun retryAttachment(localId: String) = outgoing.retry(localId)

    fun discardAttachment(localId: String) = outgoing.discard(localId)

    fun onMessageShown() = _uiState.update { it.copy(message = null) }

    override fun onCleared() {
        sessions.values.forEach { it.close() }
    }

    companion object {
        fun factory(conversationId: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                UmkmChatThreadViewModel(
                    conversationId,
                    AppContainer.chatOverviewRepository,
                    AppContainer.chatRepository,
                    AppContainer.masterDataRepository
                )
            }
        }
    }
}
