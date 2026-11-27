package com.example.proyek_papb_fe.feature.collaboration_revision_umkm.chat_room.container

import com.example.proyek_papb_fe.core.push.VisibleConversationEffect
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.domain.model.OutgoingAttachment
import com.example.proyek_papb_fe.core.ui.components.ErrorContent
import com.example.proyek_papb_fe.core.ui.components.LoadingContent
import com.example.proyek_papb_fe.core.ui.components.RefreshableContent
import com.example.proyek_papb_fe.core.ui.components.chat.ChatFileUi
import com.example.proyek_papb_fe.core.ui.components.chat.DeleteMessagesDialog
import com.example.proyek_papb_fe.core.ui.components.chat.MessageSelectionBar
import com.example.proyek_papb_fe.core.ui.components.chat.DeliveryStatus
import com.example.proyek_papb_fe.core.ui.components.chat.FailedAttachmentDialog
import com.example.proyek_papb_fe.core.ui.components.chat.rememberChatAttachmentController
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthAppBar
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.chat_room.component.ChatBubble
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.chat_room.component.ChatComposer
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.chat_room.component.ChatDateSeparator
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.chat_room.component.ChatRoomTopBar
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.chat_room.component.NegotiationBanner
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.chat_room.component.ReplyTarget
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.chat_room.component.ReplyTargetSelector
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.viewmodel.UmkmChatThreadViewModel
import com.example.proyek_papb_fe.feature.freelancer.chat.component.AttachmentPanel
import com.example.proyek_papb_fe.feature.umkm.data.ChatMessage
import com.example.proyek_papb_fe.feature.umkm.data.ProjectStatus
import com.example.proyek_papb_fe.feature.umkm.data.UmkmConversationStore
import com.example.proyek_papb_fe.feature.umkm.data.UmkmNegotiationStore
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay

private val IndonesianLocale: Locale = Locale.forLanguageTag("id-ID")
private val SeparatorFormat = DateTimeFormatter.ofPattern("EEEE, d MMM yyyy", IndonesianLocale)
private val BubbleTime = DateTimeFormatter.ofPattern("HH.mm")
private const val PollIntervalMillis = 5_000L

/**
 * Ruang chat UMKM dengan satu freelancer: pesan dari semua project dengan freelancer itu digabung
 * (seperti WhatsApp). Bila ada beberapa project, UMKM memilih project tujuan pesan di atas kolom ketik.
 */
@Composable
fun ChatRoomScreen(
    conversationId: String,
    onBack: () -> Unit,
    onOpenProject: ((projectId: String) -> Unit)?,
    onCompareDrafts: ((conversationId: String, draftMessageId: String) -> Unit)? = null,
    onOpenNegotiation: (conversationId: String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: UmkmChatThreadViewModel = viewModel(factory = UmkmChatThreadViewModel.factory(conversationId))
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val chatState by viewModel.uiState.collectAsStateWithLifecycle()
    val thread = UmkmConversationStore.threadOf(conversationId)
    val visibleConversations by viewModel.conversationIds.collectAsStateWithLifecycle()
    VisibleConversationEffect(visibleConversations)
    if (thread == null) {
        Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            AuthAppBar(title = "Chat", onBack = onBack)
            when {
                chatState.isLoading -> LoadingContent()
                chatState.errorMessage != null -> ErrorContent(chatState.errorMessage.orEmpty(), onRetry = viewModel::load)
                else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Percakapan tidak ditemukan.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        return
    }

    LaunchedEffect(conversationId) {
        while (true) {
            delay(PollIntervalMillis)
            if (!viewModel.isRealtime.value) viewModel.refreshMessages()
        }
    }
    LaunchedEffect(chatState.message) {
        chatState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onMessageShown()
        }
    }

    var draft by rememberSaveable(conversationId) { mutableStateOf("") }
    var isAttachmentOpen by rememberSaveable(conversationId) { mutableStateOf(false) }
    var failedAttachment by remember(conversationId) { mutableStateOf<OutgoingAttachment?>(null) }
    val messages = UmkmConversationStore.threadMessages(thread)
    val pending by viewModel.pendingAttachments.collectAsStateWithLifecycle()
    val selectedIds by viewModel.selectedMessageIds.collectAsStateWithLifecycle()
    val isDeletingMessages by viewModel.isDeletingMessages.collectAsStateWithLifecycle()
    val selectionMode = selectedIds.isNotEmpty()
    var isDeleteDialogOpen by remember(conversationId) { mutableStateOf(false) }
    val listState = rememberLazyListState()
    LaunchedEffect(messages.size, pending.size) {
        if (messages.isNotEmpty() || pending.isNotEmpty()) {
            listState.scrollToItem(listState.layoutInfo.totalItemsCount.coerceAtLeast(1) - 1)
        }
    }
    BackHandler(enabled = isAttachmentOpen) { isAttachmentOpen = false }
    BackHandler(enabled = selectionMode) { viewModel.clearMessageSelection() }
    val attachments = rememberChatAttachmentController(recipientName = thread.freelancerName) { file, caption, asImage ->
        viewModel.sendAttachment(file, caption, asImage)
    }
    fun launchPicker(open: () -> Unit) {
        isAttachmentOpen = false
        open()
    }

    val multiProject = thread.conversations.size > 1
    val titleOf = thread.conversations.associate { it.id to it.projectTitle }
    val senderLabel = "${thread.freelancerName} · Freelancer"
    // Kesepakatan yang menunggu tindakan UMKM (setujui atau bayar).
    val pendingAgreement = thread.conversations.firstOrNull { conversation ->
        UmkmNegotiationStore.negotiation(conversation.id)?.status.let {
            it == ProjectStatus.MenungguKesepakatan || it == ProjectStatus.MenungguPembayaran
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        if (selectionMode) {
            MessageSelectionBar(
                count = selectedIds.size,
                isDeleting = isDeletingMessages,
                onClose = viewModel::clearMessageSelection,
                onDelete = { isDeleteDialogOpen = true }
            )
        } else {
            ChatRoomTopBar(
                initials = thread.initials,
                photoUrl = thread.photoUrl,
                name = thread.freelancerName,
                role = thread.freelancerRole,
                onBack = onBack,
                onMore = { Toast.makeText(context, "Menu percakapan belum tersedia di versi ini.", Toast.LENGTH_SHORT).show() }
            )
        }
        pendingAgreement?.let { conversation ->
            val status = UmkmNegotiationStore.negotiation(conversation.id)?.status
            NegotiationBanner(
                text = if (status == ProjectStatus.MenungguPembayaran) "Kesepakatan \"${conversation.projectTitle}\" siap dibayar"
                else "Kesepakatan \"${conversation.projectTitle}\" menunggu persetujuanmu",
                onClick = { onOpenNegotiation(conversation.id) }
            )
        }
        RefreshableContent(
            onRefresh = viewModel::load,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                if (messages.isEmpty() && pending.isEmpty()) {
                    item(key = "empty") {
                        Text(
                            text = "Belum ada pesan.",
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium.withFrameMetrics(
                                fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 18.sp
                            )
                        )
                    }
                }
                messages.forEachIndexed { index, message ->
                    val previous = messages.getOrNull(index - 1)
                    val newDay = previous == null || previous.sentAt.toLocalDate() != message.sentAt.toLocalDate()
                    if (newDay) {
                        item(key = "date-${message.sentAt.toLocalDate()}") {
                            ChatDateSeparator(message.sentAt.format(SeparatorFormat))
                        }
                    }
                    // Penanda project saat pesan berpindah ke percakapan project lain.
                    val projectChanged = multiProject && previous?.conversationId != message.conversationId
                    if (projectChanged) {
                        item(key = "project-${message.id}") {
                            ChatDateSeparator("Project · ${titleOf[message.conversationId].orEmpty()}")
                        }
                    }
                    val sameSender = !newDay && !projectChanged && previous?.fromUmkm == message.fromUmkm
                    item(key = message.id) {
                        ChatBubble(
                            message = message,
                            time = "${message.sentAt.format(BubbleTime)} WIB",
                            senderLabel = senderLabel,
                            showSender = !sameSender,
                            topPadding = when {
                                newDay || projectChanged -> 0.dp
                                sameSender -> 4.dp
                                else -> 12.dp
                            },
                            onOpenDraft = if (message.attachment?.previousDraftMessageId != null && onCompareDrafts != null) {
                                { onCompareDrafts(message.conversationId, message.id) }
                            } else {
                                null
                            },
                            isOpening = message.file?.let { attachments.isOpening(ChatFileUi.Remote(it)) } == true,
                            onFileClick = { message.file?.let { attachments.open(ChatFileUi.Remote(it)) } },
                            isSelected = message.id in selectedIds,
                            selectionMode = selectionMode,
                            selectable = true,
                            onToggleSelect = { viewModel.toggleMessageSelection(message.id) }
                        )
                    }
                }
                // Lampiran yang masih dikirim/gagal tampil di akhir, seperti WhatsApp.
                pending.forEachIndexed { index, item ->
                    item(key = item.localId) {
                        val sentAt = Instant.ofEpochMilli(item.createdAtMillis).atZone(ZoneId.systemDefault()).toLocalDateTime()
                        ChatBubble(
                            message = ChatMessage(
                                id = item.localId,
                                conversationId = chatState.targetConversationId,
                                fromUmkm = true,
                                text = item.caption,
                                sentAt = sentAt
                            ),
                            time = "${sentAt.format(BubbleTime)} WIB",
                            senderLabel = senderLabel,
                            showSender = false,
                            topPadding = if (index == 0 && messages.lastOrNull()?.fromUmkm != true) 12.dp else 4.dp,
                            file = ChatFileUi.Local(item),
                            status = if (item.failedMessage != null) DeliveryStatus.FAILED else DeliveryStatus.SENDING,
                            onFileClick = {
                                if (item.failedMessage != null) failedAttachment = item
                                else attachments.open(ChatFileUi.Local(item))
                            }
                        )
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = isAttachmentOpen,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            AttachmentPanel(
                onGalleryClick = { launchPicker(attachments.openGallery) },
                onCameraClick = { launchPicker(attachments.openCamera) },
                onDocumentClick = { launchPicker(attachments.openDocument) },
                onUploadDraftClick = null
            )
        }
        if (multiProject) {
            ReplyTargetSelector(
                selected = titleOf[chatState.targetConversationId].orEmpty(),
                options = thread.conversations.map { conversation ->
                    ReplyTarget(
                        conversationId = conversation.id,
                        title = conversation.projectTitle,
                        status = UmkmNegotiationStore.negotiation(conversation.id)?.status?.label ?: conversation.stage.label
                    )
                },
                onSelect = viewModel::selectTarget
            )
        }
        ChatComposer(
            value = draft,
            onValueChange = { draft = it },
            onSend = {
                if (draft.isNotBlank()) {
                    viewModel.send(draft.trim())
                    draft = ""
                }
            },
            onAdd = {
                if (!isAttachmentOpen) focusManager.clearFocus()
                isAttachmentOpen = !isAttachmentOpen
            },
            onAttachImage = { launchPicker(attachments.openGallery) }
        )
    }

    if (isDeleteDialogOpen && selectionMode) {
        DeleteMessagesDialog(
            count = selectedIds.size,
            canDeleteForEveryone = remember(selectedIds, messages) { viewModel.canDeleteSelectedForEveryone() },
            onDeleteForMe = {
                isDeleteDialogOpen = false
                viewModel.deleteSelectedMessages(forEveryone = false)
            },
            onDeleteForEveryone = {
                isDeleteDialogOpen = false
                viewModel.deleteSelectedMessages(forEveryone = true)
            },
            onDismiss = { isDeleteDialogOpen = false }
        )
    }

    failedAttachment?.let { item ->
        FailedAttachmentDialog(
            item = item,
            onRetry = {
                failedAttachment = null
                viewModel.retryAttachment(item.localId)
            },
            onDiscard = {
                failedAttachment = null
                viewModel.discardAttachment(item.localId)
            },
            onDismiss = { failedAttachment = null }
        )
    }
}
