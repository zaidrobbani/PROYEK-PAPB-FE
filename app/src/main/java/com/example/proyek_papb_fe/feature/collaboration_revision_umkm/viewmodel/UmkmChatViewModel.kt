package com.example.proyek_papb_fe.feature.collaboration_revision_umkm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.ChatRealtimeEvent
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.ChatOverviewRepository
import com.example.proyek_papb_fe.core.data.repository.ChatRepository
import com.example.proyek_papb_fe.core.data.repository.ConversationOverview
import com.example.proyek_papb_fe.core.data.repository.MasterDataRepository
import com.example.proyek_papb_fe.core.data.repository.WorkflowRepository
import com.example.proyek_papb_fe.core.data.source.PickedChatFile
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.domain.model.OutgoingAttachment
import com.example.proyek_papb_fe.feature.chat.viewmodel.OutgoingAttachmentQueue
import com.example.proyek_papb_fe.core.domain.model.ProjectStatusCode
import com.example.proyek_papb_fe.core.domain.model.WorkflowActionCode
import com.example.proyek_papb_fe.feature.umkm.data.UmkmCancellationReason
import com.example.proyek_papb_fe.feature.umkm.data.UmkmConversationStore
import com.example.proyek_papb_fe.feature.umkm.data.UmkmNegotiationStore
import com.example.proyek_papb_fe.feature.umkm.data.UmkmProjectStore
import com.example.proyek_papb_fe.feature.umkm.data.toCode
import com.example.proyek_papb_fe.feature.umkm.data.toUmkmProject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UmkmChatUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isBusy: Boolean = false,
    val message: String? = null,
    /** Kedua pihak setuju → project MENUNGGU_PEMBAYARAN, layar lanjut ke pembayaran. */
    val readyToPayProjectId: String? = null,
    val cancelledConversationId: String? = null
)

/**
 * Chat UMKM: isi cache UmkmConversationStore/UmkmNegotiationStore/UmkmProjectStore dari BE,
 * kirim pesan teks, Lanjut Project (approve kesepakatan), dan batalkan project.
 */
class UmkmChatViewModel(
    private val conversationId: String?,
    private val overviewRepository: ChatOverviewRepository,
    private val chatRepository: ChatRepository,
    private val workflowRepository: WorkflowRepository,
    private val masterDataRepository: MasterDataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UmkmChatUiState())
    val uiState: StateFlow<UmkmChatUiState> = _uiState.asStateFlow()

    private val myId: String? get() = AppContainer.currentUserId

    private val realtime = conversationId?.let(chatRepository::realtime)
    /** true saat WebSocket tersambung; layar hanya polling bila false. */
    val isRealtime: StateFlow<Boolean> = realtime?.isConnected ?: MutableStateFlow(false)

    init {
        load()
        if (realtime != null && conversationId != null) {
            realtime.start(viewModelScope)
            viewModelScope.launch {
                realtime.events.collect { event ->
                    when (event) {
                        is ChatRealtimeEvent.NewMessage -> {
                            UmkmConversationStore.append(event.message, myId)
                            if (event.message.senderUserId != myId && !realtime.markRead()) {
                                runCatching { chatRepository.markRead(conversationId) }
                            }
                        }
                        is ChatRealtimeEvent.Read ->
                            if (event.readerUserId != myId) UmkmConversationStore.markReadByFreelancer(conversationId)
                        is ChatRealtimeEvent.Error -> _uiState.update { it.copy(message = event.message) }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        realtime?.close()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = it.isLoading, errorMessage = null) }
            try {
                if (conversationId == null) {
                    val list = overviewRepository.overviews()
                    UmkmConversationStore.replace(list, myId)
                    list.forEach { cache(it) }
                } else {
                    val overview = overviewRepository.overview(conversationId)
                    UmkmConversationStore.upsert(overview, myId)
                    cache(overview)
                    runCatching { chatRepository.markRead(conversationId) }
                    UmkmConversationStore.markRead(conversationId)
                }
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.userMessage()) }
            }
        }
    }

    private suspend fun cache(overview: ConversationOverview) {
        val detail = overview.workflow ?: return
        UmkmNegotiationStore.upsert(overview.conversation.id, detail)
        UmkmProjectStore.upsert(detail.toUmkmProject(masterDataRepository.categoryName(detail.project.categoryId)))
    }

    fun refreshMessages() {
        val id = conversationId ?: return
        viewModelScope.launch {
            val messages = runCatching { chatRepository.messages(id).items }.getOrNull() ?: return@launch
            val lastBefore = UmkmConversationStore.messages(id).lastOrNull()?.id
            // Selalu ganti agar status dibaca ikut ter-update saat WebSocket terputus.
            UmkmConversationStore.replaceMessages(id, messages, myId)
            if (messages.lastOrNull()?.id != lastBefore) runCatching { chatRepository.markRead(id) }
        }
    }

    fun send(text: String) {
        val id = conversationId ?: return
        if (text.isBlank() || _uiState.value.isBusy) return
        // Lewat WebSocket bila tersambung; pesan kembali sebagai event `message`. REST hanya cadangan.
        if (realtime?.sendMessage(text.trim()) == true) return
        viewModelScope.launch {
            _uiState.update { it.copy(isBusy = true) }
            try {
                UmkmConversationStore.append(chatRepository.send(id, text), myId)
                _uiState.update { it.copy(isBusy = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isBusy = false, message = e.userMessage()) }
            }
        }
    }

    /** "Lanjut Project" UMKM = approve kesepakatan (BE mewajibkan total bobot milestone 100%). */
    fun continueProject() {
        val projectId = UmkmConversationStore.conversation(conversationId ?: return)?.projectId ?: return
        runBusy {
            val detail = workflowRepository.detail(projectId)
            if (!detail.can(WorkflowActionCode.APPROVE_AGREEMENT)) {
                _uiState.update {
                    it.copy(
                        message = if (detail.agreement?.approvedByMe == true) "Kamu sudah setuju. Menunggu freelancer."
                        else "Kesepakatan belum bisa dilanjutkan pada tahap ini."
                    )
                }
                return@runBusy
            }
            val updated = workflowRepository.approveAgreement(projectId, detail.agreement?.version)
            UmkmNegotiationStore.upsert(conversationId, updated)
            if (updated.project.status == ProjectStatusCode.MENUNGGU_PEMBAYARAN) {
                _uiState.update { it.copy(readyToPayProjectId = projectId) }
            } else {
                _uiState.update { it.copy(message = "Kamu setuju. Menunggu persetujuan freelancer.") }
            }
        }
    }

    fun cancel(reason: UmkmCancellationReason) {
        val id = conversationId ?: return
        val projectId = UmkmConversationStore.conversation(id)?.projectId ?: return
        runBusy {
            val note = if (reason == UmkmCancellationReason.Lainnya) reason.label else null
            val updated = workflowRepository.cancel(projectId, reason.toCode(), note)
            UmkmNegotiationStore.upsert(id, updated)
            _uiState.update { it.copy(cancelledConversationId = id) }
        }
    }

    private fun runBusy(block: suspend () -> Unit) {
        if (_uiState.value.isBusy) return
        viewModelScope.launch {
            _uiState.update { it.copy(isBusy = true) }
            try {
                block()
                _uiState.update { it.copy(isBusy = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isBusy = false, message = e.userMessage()) }
            }
        }
    }

    fun onMessageShown() = _uiState.update { it.copy(message = null) }

    fun onReadyToPayHandled() = _uiState.update { it.copy(readyToPayProjectId = null) }

    fun onCancelledHandled() = _uiState.update { it.copy(cancelledConversationId = null) }

    companion object {
        fun factory(conversationId: String?): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                UmkmChatViewModel(
                    conversationId,
                    AppContainer.chatOverviewRepository,
                    AppContainer.chatRepository,
                    AppContainer.workflowRepository,
                    AppContainer.masterDataRepository
                )
            }
        }
    }
}
