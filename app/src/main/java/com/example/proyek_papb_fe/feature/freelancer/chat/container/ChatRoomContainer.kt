package com.example.proyek_papb_fe.feature.freelancer.chat.container

import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.data.ResultReviewStage
import com.example.proyek_papb_fe.core.data.WorkDraftStore
import com.example.proyek_papb_fe.core.data.model.CancellationReason
import com.example.proyek_papb_fe.core.data.model.ChatAttachment
import com.example.proyek_papb_fe.core.data.model.ChatProjectContext
import com.example.proyek_papb_fe.core.data.model.ProjectStatus
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.domain.model.FundStatusCode
import com.example.proyek_papb_fe.core.domain.model.Message
import com.example.proyek_papb_fe.core.domain.model.ProjectStatusCode
import com.example.proyek_papb_fe.core.domain.model.UploadFile
import com.example.proyek_papb_fe.core.domain.model.WorkflowActionCode
import com.example.proyek_papb_fe.core.domain.model.WorkflowDetail
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.components.ErrorContent
import com.example.proyek_papb_fe.core.ui.components.LoadingContent
import com.example.proyek_papb_fe.core.ui.components.initialsOf
import com.example.proyek_papb_fe.core.util.rupiah
import com.example.proyek_papb_fe.feature.chat.viewmodel.ChatRoomViewModel
import com.example.proyek_papb_fe.feature.freelancer.chat.component.ChatBubbleUi
import com.example.proyek_papb_fe.feature.freelancer.chat.component.ChatNegotiationUi
import com.example.proyek_papb_fe.feature.freelancer.chat.component.ChatRoomItem
import com.example.proyek_papb_fe.feature.freelancer.chat.component.ChatRoomScreen
import com.example.proyek_papb_fe.feature.freelancer.chat.component.ChatRoomUiState
import com.example.proyek_papb_fe.feature.freelancer.chat.component.UploadDraftSheet
import com.example.proyek_papb_fe.feature.freelancer.data.freelancerStatusOf
import com.example.proyek_papb_fe.feature.freelancer.negotiation.component.CancelProjectBottomSheet
import com.example.proyek_papb_fe.feature.freelancer.negotiation.viewmodel.toCode
import com.example.proyek_papb_fe.feature.freelancer.workplan.component.UmkmSimulationCard
import com.example.proyek_papb_fe.feature.freelancer.workplan.component.UmkmSimulationEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlinx.coroutines.delay

private const val PollIntervalMillis = 5_000L

private fun List<Message>.toRoomItems(myId: String?, contactName: String, umkmName: String): List<ChatRoomItem> {
    val items = mutableListOf<ChatRoomItem>()
    var previous: Message? = null
    var labelledToday = false
    for (message in this) {
        val newDay = previous == null || !isSameDay(previous.sentAtMillis, message.sentAtMillis)
        if (newDay) {
            items += ChatRoomItem.DateSeparator(formatDateSeparator(message.sentAtMillis))
            labelledToday = false
        }
        val mine = message.senderUserId == myId
        val first = newDay || previous?.senderUserId != message.senderUserId
        val label = if (!mine && first) {
            (if (labelledToday) contactName else "$contactName · $umkmName").also { labelledToday = true }
        } else null
        val attachment = message.attachmentNames.firstOrNull()
        items += ChatRoomItem.Message(
            ChatBubbleUi(
                id = message.id,
                isMine = mine,
                text = message.text,
                time = formatBubbleTime(message.sentAtMillis),
                isFirstInGroup = first,
                topGap = when { newDay -> 0; first -> 12; else -> 4 },
                senderLabel = label,
                attachmentName = attachment,
                attachmentIsImage = attachment?.substringAfterLast('.')?.lowercase() in setOf("jpg", "jpeg", "png", "webp")
            )
        )
        previous = message
    }
    return items
}

private fun contextFor(detail: WorkflowDetail?, now: Long = System.currentTimeMillis()): ChatProjectContext {
    if (detail == null) return ChatProjectContext(escrowLabel = null, note = "Menunggu persetujuan UMKM atas penawaranmu")
    val agreement = detail.agreement
    return when (freelancerStatusOf(null, detail.project.status)) {
        ProjectStatus.MENUNGGU_KESEPAKATAN, ProjectStatus.NEGOSIASI -> ChatProjectContext(
            escrowLabel = null,
            note = "Status: ",
            noteEmphasis = detail.project.statusLabel
        )
        ProjectStatus.PROJECT_BERJALAN -> {
            val deadline = agreement?.agreedDeadlineMillis ?: detail.project.deadlineMillis ?: now
            val daysLeft = ceil((deadline - now) / TimeUnit.DAYS.toMillis(1).toDouble()).toInt().coerceAtLeast(0)
            ChatProjectContext(
                escrowLabel = detail.payment?.takeIf { it.fundStatus == FundStatusCode.HELD }
                    ?.let { "Saldo tertahan ${rupiah(it.freelancerAmount)}" },
                note = "Tenggat proyek: ",
                noteEmphasis = "$daysLeft hari lagi",
                noteSuffix = " (${formatShortDate(deadline)})"
            )
        }
        ProjectStatus.SELESAI -> ChatProjectContext(
            escrowLabel = null,
            note = "Proyek selesai pada ",
            noteEmphasis = formatShortDate(detail.project.completedAtMillis ?: now)
        )
        ProjectStatus.DIBATALKAN -> ChatProjectContext(
            escrowLabel = null,
            note = "Proyek dibatalkan · Alasan: ",
            noteEmphasis = detail.cancellation?.reasonLabel
        )
    }
}

private fun resultStageEntry(detail: WorkflowDetail?): Pair<ResultReviewStage, UmkmSimulationEntry>? {
    val status = detail?.project?.status ?: return null
    return when (status) {
        ProjectStatusCode.MENUNGGU_REVIEW -> ResultReviewStage.WAITING_REVIEW to UmkmSimulationEntry(
            "Draf terbaru sudah terkirim. Menunggu UMKM menyetujui hasil atau meminta revisi.",
            "Muat ulang status"
        )
        ProjectStatusCode.REVISI -> ResultReviewStage.REVISION_REQUESTED to UmkmSimulationEntry(
            "UMKM meminta revisi untuk draf terakhir. Unggah draf baru setelah memperbaikinya.",
            "Lihat permintaan revisi"
        )
        ProjectStatusCode.SELESAI -> ResultReviewStage.COMPLETED to UmkmSimulationEntry(
            "Hasil disetujui UMKM. Proyek selesai dan dana sudah diteruskan ke dompetmu.",
            "Lihat dompet"
        )
        else -> null
    }
}

private fun Context.displayNameOf(uri: Uri): String? =
    contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) cursor.getString(0) else null
    }

@Composable
fun ChatRoomContainer(
    conversationId: String,
    onBack: () -> Unit,
    onProjectDetailClick: (projectId: String) -> Unit,
    onContinueProject: (projectId: String) -> Unit = {},
    onProjectCancelled: (projectId: String) -> Unit = {},
    onOpenResultStage: (projectId: String, stage: ResultReviewStage) -> Unit = { _, _ -> },
    viewModel: ChatRoomViewModel = viewModel(
        factory = ChatRoomViewModel.factory(
            conversationId,
            AppContainer.chatRepository,
            AppContainer.chatOverviewRepository,
            AppContainer.workflowRepository,
            AppContainer.submissionRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var draft by rememberSaveable(conversationId) { mutableStateOf("") }
    var isAttachmentOpen by rememberSaveable(conversationId) { mutableStateOf(false) }
    var isUploadDraftOpen by rememberSaveable(conversationId) { mutableStateOf(false) }
    var isCancelSheetVisible by rememberSaveable(conversationId) { mutableStateOf(false) }
    var selectedReason by rememberSaveable(conversationId) { mutableStateOf(CancellationReason.PRICE_MISMATCH) }
    val toast = { text: String -> Toast.makeText(context, text, Toast.LENGTH_SHORT).show() }

    LaunchedEffect(Unit) {
        while (true) {
            delay(PollIntervalMillis)
            viewModel.refreshMessages()
        }
    }
    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onMessageShown()
        }
    }
    LaunchedEffect(uiState.resultSubmitted) {
        if (uiState.resultSubmitted) {
            viewModel.onResultSubmittedHandled()
            WorkDraftStore.markSent(conversationId)
        }
    }
    val projectId = uiState.overview?.conversation?.projectId
    LaunchedEffect(uiState.cancelled) {
        if (uiState.cancelled) {
            viewModel.onCancelledHandled()
            projectId?.let(onProjectCancelled)
        }
    }
    BackHandler(enabled = isAttachmentOpen) { isAttachmentOpen = false }

    val overview = uiState.overview
    if (overview == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailTopBar(title = "Chat", onBack = onBack)
            if (uiState.isLoading) LoadingContent() else ErrorContent(uiState.errorMessage.orEmpty(), onRetry = viewModel::load)
        }
        return
    }

    val detail = uiState.workflow
    val contactName = overview.counterpartName
    val umkmName = overview.counterpartSubtitle ?: contactName
    val items = remember(uiState.messages) { uiState.messages.toRoomItems(AppContainer.currentUserId, contactName, umkmName) }
    val state = ChatRoomUiState(
        contactName = contactName,
        contactInitials = initialsOf(contactName),
        umkmName = umkmName,
        projectTitle = overview.projectTitle ?: "Percakapan umum",
        context = contextFor(detail),
        items = items
    )

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = state.items.lastIndex.coerceAtLeast(0))
    var shownCount by remember(conversationId) { mutableIntStateOf(state.items.size) }
    LaunchedEffect(state.items.size) {
        if (state.items.size > shownCount) listState.animateScrollToItem(state.items.lastIndex)
        shownCount = state.items.size
    }

    // BE chat baru mendukung pesan teks; lampiran dikirim lewat "Unggah Draf" (hasil kerja) saat project berjalan.
    val attachmentUnsupported = { isAttachmentOpen = false; toast("Kirim lampiran di chat belum didukung server") }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) attachmentUnsupported()
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) attachmentUnsupported()
    }
    val documentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) attachmentUnsupported()
    }
    fun launchSafely(launch: () -> Unit) {
        isAttachmentOpen = false
        try { launch() } catch (_: ActivityNotFoundException) { toast("Aplikasi untuk aksi ini tidak tersedia") }
    }

    val agreement = detail?.agreement
    val negotiation = detail?.takeIf { it.project.status == ProjectStatusCode.MENUNGGU_KESEPAKATAN }?.let {
        ChatNegotiationUi(
            projectValue = rupiah(agreement?.agreedPrice ?: it.project.budget),
            duration = "${agreement?.durationDays ?: 0} hari kalender",
            output = agreement?.scopeOfWork ?: it.project.description,
            paymentMethod = agreement?.paymentMethodLabel ?: "QRIS (simulasi)"
        )
    }
    val canUploadDraft = detail?.can(WorkflowActionCode.SUBMIT_RESULT) == true
    val nextVersion = (uiState.submissions?.latest?.versionNumber ?: 0) + 1
    val draftFileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (uris.isNotEmpty()) {
            val picked = context.readDraftFiles(uris)
            WorkDraftStore.addFiles(conversationId, picked.accepted)
            if (picked.rejected.isNotEmpty()) {
                toast("${picked.rejected.joinToString()} tidak didukung. Gunakan AI, PSD, PDF, ZIP, atau PNG (maks. 50 MB).")
            }
        }
    }
    val stage = resultStageEntry(detail)

    ChatRoomScreen(
        state = state,
        draft = draft,
        onDraftChange = { draft = it },
        onSendClick = {
            if (draft.isNotBlank() && !uiState.isSending) {
                viewModel.send(draft)
                draft = ""
            }
        },
        isAttachmentOpen = isAttachmentOpen,
        onAttachmentToggle = {
            if (!isAttachmentOpen) focusManager.clearFocus()
            isAttachmentOpen = !isAttachmentOpen
        },
        onGalleryClick = {
            launchSafely { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
        },
        onCameraClick = { launchSafely { cameraLauncher.launch(null) } },
        onDocumentClick = { launchSafely { documentLauncher.launch(arrayOf("*/*")) } },
        onUploadDraftClick = if (canUploadDraft) {
            {
                isAttachmentOpen = false
                isUploadDraftOpen = true
            }
        } else null,
        onInputFocused = { isAttachmentOpen = false },
        onBack = onBack,
        onMoreClick = { toast("Menu lainnya segera hadir") },
        onProjectDetailClick = projectId?.let { id -> { onProjectDetailClick(id) } },
        listState = listState,
        negotiation = negotiation,
        onCancelProjectClick = {
            if (detail?.can(WorkflowActionCode.CANCEL_PROJECT) == true) isCancelSheetVisible = true
            else toast("Proyek sudah dibayar dan resmi dikerjakan, tidak dapat dibatalkan.")
        },
        onContinueProjectClick = { projectId?.let(onContinueProject) },
        topBanner = stage?.let { (resultStage, entry) ->
            {
                UmkmSimulationCard(
                    entry,
                    onClick = {
                        if (resultStage == ResultReviewStage.WAITING_REVIEW) viewModel.load()
                        else projectId?.let { onOpenResultStage(it, resultStage) }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    )

    if (isUploadDraftOpen && canUploadDraft) {
        val workDraft = WorkDraftStore.current(conversationId).copy(version = nextVersion)
        val draftUi = workDraft.toUiState(state.projectTitle, contactName)
        UploadDraftSheet(
            state = draftUi,
            onPickFiles = {
                try {
                    draftFileLauncher.launch(arrayOf("*/*"))
                } catch (_: ActivityNotFoundException) {
                    toast("Aplikasi untuk memilih file tidak tersedia")
                }
            },
            onRemoveFile = { uri -> WorkDraftStore.removeFile(conversationId, uri) },
            onNoteChange = { WorkDraftStore.updateNote(conversationId, it) },
            onSaveAndClose = {
                isUploadDraftOpen = false
                if (workDraft.files.isNotEmpty() || workDraft.note.isNotBlank()) toast("Draf disimpan di perangkat")
            },
            onSend = {
                if (workDraft.files.isEmpty() && workDraft.note.isBlank()) {
                    toast("Pilih file hasil atau tulis catatan terlebih dahulu")
                } else {
                    isUploadDraftOpen = false
                    viewModel.submitResult(
                        files = workDraft.files.map { UploadFile(it.uri, it.name, null) },
                        note = workDraft.note
                    )
                }
            },
            onDismiss = { isUploadDraftOpen = false }
        )
    }

    if (isCancelSheetVisible && projectId != null) {
        CancelProjectBottomSheet(
            selectedReason = selectedReason,
            onReasonSelected = { selectedReason = it },
            onConfirm = {
                isCancelSheetVisible = false
                viewModel.cancelProject(
                    selectedReason.toCode(),
                    if (selectedReason == CancellationReason.OTHER) selectedReason.label else null
                )
            },
            onDismiss = {
                isCancelSheetVisible = false
                selectedReason = CancellationReason.PRICE_MISMATCH
            }
        )
    }
}
