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
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyek_papb_fe.core.data.ChatStore
import com.example.proyek_papb_fe.core.data.FreelancerProjectDummyData
import com.example.proyek_papb_fe.core.data.NegotiationDummyData
import com.example.proyek_papb_fe.core.data.SubmittedOfferStore
import com.example.proyek_papb_fe.core.data.WorkDraftStore
import com.example.proyek_papb_fe.core.data.ResultReviewStage
import com.example.proyek_papb_fe.core.data.ResultReviewStore
import com.example.proyek_papb_fe.feature.freelancer.workplan.component.UmkmSimulationCard
import com.example.proyek_papb_fe.feature.freelancer.workplan.component.UmkmSimulationEntry
import com.example.proyek_papb_fe.core.data.model.CancellationReason
import com.example.proyek_papb_fe.core.data.model.ChatProjectContext
import com.example.proyek_papb_fe.core.data.model.ProjectStatus
import com.example.proyek_papb_fe.core.data.model.SubmittedOffer
import com.example.proyek_papb_fe.feature.freelancer.chat.component.ChatNegotiationUi
import com.example.proyek_papb_fe.feature.freelancer.negotiation.component.CancelProjectBottomSheet
import com.example.proyek_papb_fe.core.data.model.ChatAttachment
import com.example.proyek_papb_fe.core.data.model.ChatConversation
import com.example.proyek_papb_fe.core.data.model.ChatMessage
import com.example.proyek_papb_fe.core.data.model.MessageSender
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.components.FeaturePlaceholder
import com.example.proyek_papb_fe.feature.freelancer.chat.component.ChatBubbleUi
import com.example.proyek_papb_fe.feature.freelancer.chat.component.ChatRoomItem
import com.example.proyek_papb_fe.feature.freelancer.chat.component.ChatRoomScreen
import com.example.proyek_papb_fe.feature.freelancer.chat.component.ChatRoomUiState
import com.example.proyek_papb_fe.feature.freelancer.chat.component.UploadDraftSheet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

/**
 * Menyusun pesan menjadi item tampilan: pemisah tanggal saat hari berganti, grup per
 * pengirim (jarak 12 saat berganti, 4 untuk lanjutan), dan label pengirim di pesan masuk
 * pertama tiap grup ("Nama · UMKM" untuk grup pertama di hari itu).
 */
private fun ChatConversation.toRoomItems(): List<ChatRoomItem> {
    val items = mutableListOf<ChatRoomItem>()
    var previous: ChatMessage? = null
    var labelledToday = false
    for (message in messages) {
        val newDay = previous == null || !isSameDay(previous.sentAtMillis, message.sentAtMillis)
        if (newDay) {
            items += ChatRoomItem.DateSeparator(formatDateSeparator(message.sentAtMillis))
            labelledToday = false
        }
        val mine = message.sender == MessageSender.FREELANCER
        val first = newDay || previous?.sender != message.sender
        val label = if (!mine && first) {
            (if (labelledToday) contactName else "$contactName · $umkmName").also { labelledToday = true }
        } else null
        items += ChatRoomItem.Message(
            ChatBubbleUi(
                id = message.id,
                isMine = mine,
                text = message.text,
                time = formatBubbleTime(message.sentAtMillis),
                isFirstInGroup = first,
                topGap = when { newDay -> 0; first -> 12; else -> 4 },
                senderLabel = label,
                attachmentName = message.attachment?.fileName,
                attachmentIsImage = message.attachment?.isImage ?: true
            )
        )
        previous = message
    }
    return items
}

/**
 * Strip konteks sesuai status project (sumber: penawaran project ini). null = pakai konteks
 * bawaan percakapan (mis. ringkasan negosiasi yang sudah ada).
 */
private fun contextFor(status: ProjectStatus, offer: SubmittedOffer, now: Long = System.currentTimeMillis()): ChatProjectContext? =
    when (status) {
        ProjectStatus.MENUNGGU_KESEPAKATAN ->
            ChatProjectContext(escrowLabel = null, note = "Menunggu persetujuan UMKM atas penawaranmu")
        ProjectStatus.NEGOSIASI -> null
        ProjectStatus.PROJECT_BERJALAN -> {
            val deadline = (offer.agreedAtMillis ?: now) + TimeUnit.DAYS.toMillis(offer.durationDays.toLong())
            val daysLeft = ceil((deadline - now) / TimeUnit.DAYS.toMillis(1).toDouble()).toInt().coerceAtLeast(0)
            ChatProjectContext(
                escrowLabel = "Saldo tertahan ${formatRupiah(offer.netAmount)}",
                note = "Tenggat proyek: ",
                noteEmphasis = "$daysLeft hari lagi",
                noteSuffix = " (${formatShortDate(deadline)})"
            )
        }
        ProjectStatus.SELESAI -> ChatProjectContext(
            escrowLabel = null,
            note = "Proyek selesai pada ",
            noteEmphasis = formatShortDate(offer.completedAtMillis ?: now)
        )
        ProjectStatus.DIBATALKAN -> ChatProjectContext(
            escrowLabel = null,
            note = "Proyek dibatalkan · Alasan: ",
            noteEmphasis = offer.cancellation?.reason?.label
        )
    }

/** Nama file dari URI pemilih sistem (Galeri / Dokumen); null bila tidak tersedia. */
private fun Context.displayNameOf(uri: Uri): String? =
    contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) cursor.getString(0) else null
    }

/**
 * Container ruang chat satu percakapan: membaca percakapan dari [ChatStore], menandai
 * terbaca saat dibuka, menyimpan draf + status panel lampiran, mengirim pesan/lampiran
 * secara lokal, dan selalu menampilkan pesan terbaru.
 *
 * Aksi yang tersedia mengikuti status project ([ChatStore.statusOf], sama dengan tab
 * Project), sehingga sama dari pintu masuk mana pun (tab Chat, Detail Project, Status):
 * - Negosiasi → Ringkasan Kesepakatan + aksi Batalkan / Lanjut Project.
 * - Project Berjalan → "Unggah Draf Pekerjaan" di panel lampiran (hanya di status ini),
 *   membuka bottom sheet Unggah Draf (Figma 17:5174) di atas ruang ini.
 * - Status lain → ruang chat biasa (pesan, Galeri/Kamera/Dokumen).
 */
@Composable
fun ChatRoomContainer(
    conversationId: Int,
    onBack: () -> Unit,
    onProjectDetailClick: (projectId: Int) -> Unit,
    /** "Lanjut Project" di tahap negosiasi → Rencana Pengerjaan (alur yang sudah ada). */
    onContinueProject: (projectId: Int) -> Unit = {},
    /** Pembatalan negosiasi sudah tersimpan; navigasi keluar seperti Negosiasi lama. */
    onProjectCancelled: (projectId: Int) -> Unit = {},
    /**
     * Kartu simulasi sisi UMKM setelah pembayaran: buka langkah review hasil sesuai tahap
     * (Review Hasil 17:1468, Permintaan Revisi 17:5737, atau Proyek Selesai 17:6338).
     */
    onOpenResultStage: (projectId: Int, stage: ResultReviewStage) -> Unit = { _, _ -> }
) {
    // Dibaca langsung (snapshot state) agar pesan yang baru dikirim langsung tampil.
    val conversation = ChatStore.get(conversationId)
    if (conversation == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailTopBar(title = "Chat", onBack = onBack)
            FeaturePlaceholder(
                title = "Percakapan tidak ditemukan",
                description = "Percakapan dengan id $conversationId tidak tersedia."
            )
        }
        return
    }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var draft by rememberSaveable(conversationId) { mutableStateOf("") }
    var isAttachmentOpen by rememberSaveable(conversationId) { mutableStateOf(false) }
    var isUploadDraftOpen by rememberSaveable(conversationId) { mutableStateOf(false) }
    LaunchedEffect(conversationId) { ChatStore.markRead(conversationId) }

    // Back menutup panel lampiran dulu, baru keluar dari ruang chat.
    BackHandler(enabled = isAttachmentOpen) { isAttachmentOpen = false }

    // Satu sumber status untuk seluruh ruang ini (dibaca langsung: ikut berubah saat
    // project disetujui / disepakati / dibatalkan).
    val status = ChatStore.statusOf(conversation)
    val projectOffer = conversation.projectId?.let { SubmittedOfferStore.get(it) }
    val state = remember(conversation, projectOffer, status) {
        ChatRoomUiState(
            contactName = conversation.contactName,
            contactInitials = conversation.contactInitials,
            umkmName = conversation.umkmName,
            projectTitle = conversation.projectTitle,
            context = projectOffer?.let { contextFor(status, it) } ?: conversation.context,
            items = conversation.toRoomItems()
        )
    }

    // Buka di pesan terbaru; gulir ke bawah lagi setiap ada pesan baru terkirim.
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = state.items.lastIndex.coerceAtLeast(0))
    var shownCount by remember(conversationId) { mutableIntStateOf(state.items.size) }
    LaunchedEffect(state.items.size) {
        if (state.items.size > shownCount) listState.animateScrollToItem(state.items.lastIndex)
        shownCount = state.items.size
    }

    val toast = { text: String -> Toast.makeText(context, text, Toast.LENGTH_SHORT).show() }
    fun sendAttachment(attachment: ChatAttachment) {
        isAttachmentOpen = false
        ChatStore.sendAttachment(conversationId, attachment)
    }

    // Pemilih bawaan sistem (tanpa library tambahan). Hasilnya hanya dikirim sebagai pesan
    // lampiran lokal; belum ada unggah ke backend.
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { sendAttachment(ChatAttachment(context.displayNameOf(it) ?: "Foto dari Galeri", isImage = true)) }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val stamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.ROOT).format(Date())
            sendAttachment(ChatAttachment("Foto_Kamera_$stamp.jpg", isImage = true))
        }
    }
    val documentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            val isImage = context.contentResolver.getType(it)?.startsWith("image/") == true
            sendAttachment(ChatAttachment(context.displayNameOf(it) ?: "Dokumen", isImage = isImage))
        }
    }
    // Seperti WhatsApp: memilih aksi langsung menutup panel, termasuk bila pemilih dibatalkan.
    fun launchSafely(launch: () -> Unit) {
        isAttachmentOpen = false
        try { launch() } catch (_: ActivityNotFoundException) { toast("Aplikasi untuk aksi ini tidak tersedia") }
    }

    // Detail hanya tersedia bila project-nya ada di data project freelancer.
    val projectId = conversation.projectId?.takeIf { FreelancerProjectDummyData.getById(it) != null }

    // UI negosiasi hanya saat status Negosiasi (bukan setelah kesepakatan final / selesai /
    // batal). Isinya = Ringkasan Kesepakatan Negosiasi 26:2.
    val offer = projectId?.let { SubmittedOfferStore.get(it) }
    val negotiation = offer?.takeIf { status == ProjectStatus.NEGOSIASI }?.let {
        ChatNegotiationUi(
            projectValue = formatRupiah(it.offerAmount),
            duration = "${it.durationDays} hari kalender",
            output = NegotiationDummyData.agreedOutputFor(it.projectId),
            paymentMethod = "QRIS (simulasi)"
        )
    }
    var isCancelSheetVisible by rememberSaveable(conversationId) { mutableStateOf(false) }
    var selectedReason by rememberSaveable(conversationId) { mutableStateOf(CancellationReason.PRICE_MISMATCH) }

    // Satu aturan untuk aksi & modal Unggah Draf: hanya status Project Berjalan.
    val canUploadDraft = status == ProjectStatus.PROJECT_BERJALAN
    // Pilih banyak file dari perangkat (pemilih sistem). Hanya metadata lokal yang disimpan;
    // tidak ada unggahan ke server.
    val draftFileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (uris.isNotEmpty()) {
            val picked = context.readDraftFiles(uris)
            WorkDraftStore.addFiles(conversationId, picked.accepted)
            if (picked.rejected.isNotEmpty()) {
                toast("${picked.rejected.joinToString()} tidak didukung. Gunakan AI, PSD, PDF, ZIP, atau PNG (maks. 50 MB).")
            }
        }
    }

    ChatRoomScreen(
        state = state,
        draft = draft,
        onDraftChange = { draft = it },
        onSendClick = {
            val text = draft.trim()
            if (text.isNotEmpty()) {
                ChatStore.send(conversationId, text)
                draft = ""
            }
        },
        isAttachmentOpen = isAttachmentOpen,
        onAttachmentToggle = {
            // Seperti WhatsApp: panel dan keyboard tidak tampil bersamaan.
            if (!isAttachmentOpen) focusManager.clearFocus()
            isAttachmentOpen = !isAttachmentOpen
        },
        onGalleryClick = {
            launchSafely { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
        },
        onCameraClick = { launchSafely { cameraLauncher.launch(null) } },
        onDocumentClick = { launchSafely { documentLauncher.launch(arrayOf("*/*")) } },
        // Unggah Draf Pekerjaan hanya untuk project yang sedang dikerjakan; status lain
        // tidak menampilkan aksinya sama sekali.
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
            // Pembatalan hanya sebelum kesepakatan final (tab Project "Project Berjalan").
            if (offer?.isAgreed == true) toast("Proyek sudah disepakati dan resmi dikerjakan, tidak dapat dibatalkan.") else isCancelSheetVisible = true
        },
        onContinueProjectClick = { projectId?.let(onContinueProject) },
        topBanner = resultStageEntry(projectId)?.let { (stage, entry) ->
            { UmkmSimulationCard(entry, onClick = { onOpenResultStage(projectId!!, stage) }, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }
        }
    )

    // Modal Unggah Draf (Figma 17:5174) di atas ruang chat; state ruang chat tetap utuh.
    if (isUploadDraftOpen && canUploadDraft) {
        val draft = WorkDraftStore.current(conversationId)
        val draftUi = draft.toUiState(conversation.projectTitle, conversation.contactName)
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
                if (draft.files.isNotEmpty() || draft.note.isNotBlank()) toast("Draf disimpan")
            },
            onSend = {
                isUploadDraftOpen = false
                // Belum ada backend: file "dikirim" sebagai pesan lampiran lokal di ruang ini
                // (sama seperti lampiran Galeri/Dokumen), tanpa unggahan ke server.
                val note = draft.note.trim()
                draft.files.forEachIndexed { index, file ->
                    val caption = if (index == 0) {
                        "${draftUi.versionLabel} untuk ditinjau." + if (note.isNotEmpty()) "\n$note" else ""
                    } else ""
                    ChatStore.sendAttachment(conversationId, ChatAttachment(file.name, isImage = file.extension == "png"), caption)
                }
                WorkDraftStore.markSent(conversationId)
                toast("${draftUi.versionLabel} dikirim ke klien untuk ditinjau.")
            },
            onDismiss = { isUploadDraftOpen = false }
        )
    }

    // Modal "Batalkan Project" (Negosiasi 26:87) dan aturan pembatalannya dipakai ulang apa adanya.
    if (isCancelSheetVisible && projectId != null) {
        CancelProjectBottomSheet(
            selectedReason = selectedReason,
            onReasonSelected = { selectedReason = it },
            onConfirm = {
                SubmittedOfferStore.markCancelled(projectId, selectedReason)
                isCancelSheetVisible = false
                onProjectCancelled(projectId)
            },
            onDismiss = {
                isCancelSheetVisible = false
                selectedReason = CancellationReason.PRICE_MISMATCH
            }
        )
    }
}

/** 1500000 → "Rp 1.500.000". */
private fun formatRupiah(amount: Long): String =
    "Rp " + amount.toString().reversed().chunked(3).joinToString(".").reversed()

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun ChatRoomContainerPreview() {
    ChatRoomContainer(conversationId = 1, onBack = {}, onProjectDetailClick = {})
}

/**
 * Kartu demo sisi UMKM untuk tahap review hasil project ini (aplikasi belum punya sisi UMKM).
 * null = tidak ada langkah UMKM (belum dibayar, belum ada draf terkirim, dsb.).
 */
private fun resultStageEntry(projectId: Int?): Pair<ResultReviewStage, UmkmSimulationEntry>? {
    val stage = projectId?.let(ResultReviewStore::stageOf) ?: return null
    val entry = when (stage) {
        ResultReviewStage.WAITING_REVIEW -> UmkmSimulationEntry(
            "Draf terbaru sudah terkirim. Buka sisi UMKM untuk menyetujui hasil atau meminta revisi.",
            "Review hasil sebagai UMKM"
        )
        ResultReviewStage.REVISION_REQUESTED -> UmkmSimulationEntry(
            "UMKM meminta revisi untuk draf terakhir. Unggah draf baru setelah memperbaikinya.",
            "Lihat permintaan revisi"
        )
        ResultReviewStage.COMPLETED -> UmkmSimulationEntry(
            "Hasil disetujui UMKM. Proyek selesai dan dana sudah diteruskan ke dompetmu.",
            "Lihat ringkasan proyek selesai"
        )
        ResultReviewStage.NO_SUBMISSION -> return null
    }
    return stage to entry
}
