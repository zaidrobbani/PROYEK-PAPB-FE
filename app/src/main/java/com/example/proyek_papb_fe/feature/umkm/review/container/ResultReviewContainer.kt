package com.example.proyek_papb_fe.feature.umkm.review.container

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.proyek_papb_fe.core.data.FreelancerProfileStore
import com.example.proyek_papb_fe.core.data.FreelancerProjectDummyData
import com.example.proyek_papb_fe.core.data.NotificationStore
import com.example.proyek_papb_fe.core.data.PaymentStore
import com.example.proyek_papb_fe.core.data.ProjectReview
import com.example.proyek_papb_fe.core.data.ResultReviewStage
import com.example.proyek_papb_fe.core.data.ResultReviewStore
import com.example.proyek_papb_fe.core.data.RevisionFormDraft
import com.example.proyek_papb_fe.core.data.RevisionRequestStore
import com.example.proyek_papb_fe.core.data.SubmittedOfferStore
import com.example.proyek_papb_fe.core.data.WalletStore
import com.example.proyek_papb_fe.core.data.model.FreelancerNotification
import com.example.proyek_papb_fe.core.data.model.FreelancerProject
import com.example.proyek_papb_fe.core.data.model.NotificationDay
import com.example.proyek_papb_fe.core.data.model.NotificationType
import com.example.proyek_papb_fe.core.data.model.RevisionFocus
import com.example.proyek_papb_fe.core.data.model.RevisionFocusTone
import com.example.proyek_papb_fe.core.data.model.RevisionPoint
import com.example.proyek_papb_fe.core.data.model.RevisionRequest
import com.example.proyek_papb_fe.core.data.model.SubmittedDraft
import com.example.proyek_papb_fe.core.ui.components.LocalImage
import com.example.proyek_papb_fe.feature.freelancer.chat.container.formatBubbleTime
import com.example.proyek_papb_fe.feature.freelancer.chat.container.formatShortDate
import com.example.proyek_papb_fe.feature.freelancer.chat.container.isSameDay
import com.example.proyek_papb_fe.feature.umkm.payment.component.formatRupiah
import com.example.proyek_papb_fe.feature.umkm.payment.container.shortRole
import com.example.proyek_papb_fe.feature.umkm.review.component.ApproveCompletionSheet
import com.example.proyek_papb_fe.feature.umkm.review.component.ApproveUi
import com.example.proyek_papb_fe.feature.umkm.review.component.ResultReviewScreen
import com.example.proyek_papb_fe.feature.umkm.review.component.ResultReviewUiState
import com.example.proyek_papb_fe.feature.umkm.review.component.RevisionFormUi
import com.example.proyek_papb_fe.feature.umkm.review.component.RevisionPointInput
import com.example.proyek_papb_fe.feature.umkm.review.component.RevisionRequestSheet
import com.example.proyek_papb_fe.feature.umkm.review.component.ReviewFileUi
import com.example.proyek_papb_fe.feature.umkm.review.component.formatFileSize
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

/** Kategori aspek perbaikan (Figma 17:5583 menampilkan chip kategori; daftar dibuat umum). */
private val RevisionAspects = listOf("Tipografi & Teks", "Warna", "Tata Letak", "Ilustrasi & Motif", "Ukuran & Format Berkas")

private val ImageExtensions = setOf("jpg", "jpeg", "png", "webp", "gif", "heic")

private enum class ReviewSheet { NONE, REVISION, APPROVE }

/** "KopiSenja.pdf" + 18.4 MB → "18.4 MB, Berkas PDF". */
internal fun SubmittedDraft.fileUis(): List<ReviewFileUi> = files.map { file ->
    val ext = file.extension
    ReviewFileUi(
        name = file.name,
        meta = "${formatFileSize(file.sizeBytes)}, " + when {
            ext in ImageExtensions -> "Gambar ${ext.uppercase()}"
            ext.isEmpty() -> "Berkas"
            else -> "Berkas ${ext.uppercase()}"
        },
        extension = ext,
        uri = file.uri,
        isImage = ext in ImageExtensions
    )
}

/** "Hari ini, 10.45 WIB" / "24 Okt 2026, 10.45 WIB". */
internal fun formatSentAt(millis: Long, now: Long = System.currentTimeMillis()): String =
    (if (isSameDay(millis, now)) "Hari ini" else formatShortDate(millis)) + ", " + formatBubbleTime(millis)

internal fun firstName(fullName: String): String = fullName.trim().substringBefore(' ')

/** Tenggat revisi: [workingDays] hari kerja setelah [fromMillis] (Sabtu/Minggu dilewati). */
private fun addWorkingDays(fromMillis: Long, workingDays: Int): Long {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta")).apply { timeInMillis = fromMillis }
    var added = 0
    while (added < workingDays) {
        cal.add(Calendar.DAY_OF_YEAR, 1)
        val day = cal.get(Calendar.DAY_OF_WEEK)
        if (day != Calendar.SATURDAY && day != Calendar.SUNDAY) added++
    }
    return cal.timeInMillis
}

private fun aspectTone(aspect: String): RevisionFocusTone = when (aspect) {
    "Tipografi & Teks" -> RevisionFocusTone.TYPOGRAPHY
    "Ilustrasi & Motif" -> RevisionFocusTone.PATTERN
    else -> RevisionFocusTone.GENERAL
}

/**
 * Container Review Hasil Project (Figma 17:1468) untuk [projectId], sisi UMKM. Menampilkan
 * draf terbaru yang dikirim freelancer. "Minta Revisi" → sheet 17:5518 → permintaan revisi
 * tersimpan per project + notifikasi freelancer, lalu kembali. "Setujui & Selesai" → sheet
 * 17:6140 → project Selesai, dana bersih ke dompet freelancer, lalu [onCompleted].
 * Bila project tidak sedang menunggu review, layar tidak ditampilkan.
 */
@Composable
fun ResultReviewContainer(
    projectId: Int,
    onBack: () -> Unit,
    onOpenChat: (projectId: Int) -> Unit,
    onCompleted: (projectId: Int) -> Unit
) {
    val context = LocalContext.current
    val stage = ResultReviewStore.stageOf(projectId)
    val project = FreelancerProjectDummyData.getById(projectId)
    val draft = ResultReviewStore.latestDraft(projectId)
    val payment = PaymentStore.forProject(projectId)

    when {
        stage == ResultReviewStage.COMPLETED -> {
            LaunchedEffect(projectId) { onCompleted(projectId) }
            return
        }
        stage != ResultReviewStage.WAITING_REVIEW || project == null || draft == null || payment == null -> {
            // Revisi sudah diminta / belum ada draf / project tidak valid: tidak ada yang ditinjau.
            LaunchedEffect(projectId) { onBack() }
            return
        }
    }
    project!!; draft!!; payment!!

    val profile = FreelancerProfileStore.profile
    val files = remember(draft) { draft.fileUis() }
    var sheet by rememberSaveable { mutableStateOf(ReviewSheet.NONE) }
    var zoomed by remember { mutableStateOf<ReviewFileUi?>(null) }

    // Form revisi (dipulihkan dari "Simpan Draf" bila ada).
    val saved = remember(projectId) { RevisionRequestStore.savedForm(projectId) }
    var aspects by remember(projectId) { mutableStateOf(saved?.aspects ?: emptySet()) }
    var points by remember(projectId) {
        mutableStateOf(saved?.points?.map { RevisionPointInput(it.title, it.instruction, it.area) } ?: listOf(RevisionPointInput()))
    }
    // Form persetujuan.
    var confirmed by remember { mutableStateOf(false) }
    var rating by remember { mutableIntStateOf(0) }
    var reviewText by remember { mutableStateOf("") }

    val toast: (String) -> Unit = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }

    ResultReviewScreen(
        state = ResultReviewUiState(
            projectTitle = project.title,
            freelancerName = profile.fullName,
            freelancerPhotoUri = profile.photoUri,
            freelancerRating = String.format(Locale.US, "%.1f", profile.rating),
            projectValue = formatRupiah(payment.amount),
            note = draft.note,
            draftLabel = "Draf Pengiriman v${draft.version}.0",
            sentAt = formatSentAt(draft.sentAtMillis),
            files = files
        ),
        onBack = onBack,
        onChatClick = { onOpenChat(projectId) },
        onZoomClick = {
            zoomed = files.firstOrNull { it.isImage }
            if (zoomed == null) toast("Pratinjau besar hanya tersedia untuk berkas gambar")
        },
        onDownloadFile = { toast("${it.name} diunduh (simulasi)") },
        onRequestRevision = { sheet = ReviewSheet.REVISION },
        onApprove = { sheet = ReviewSheet.APPROVE }
    )

    when (sheet) {
        ReviewSheet.REVISION -> RevisionRequestSheet(
            form = RevisionFormUi(
                draftLabel = draft.versionLabel,
                projectTitle = project.title,
                reviewedDraft = "${draft.versionLabel}, Kiriman ${profile.fullName}",
                receivedAt = "Diterima " + formatShortDate(draft.sentAtMillis) + ", " + formatBubbleTime(draft.sentAtMillis),
                aspects = RevisionAspects,
                selectedAspects = aspects,
                points = points
            ),
            onToggleAspect = { aspects = if (it in aspects) aspects - it else aspects + it },
            onPointChange = { index, value -> points = points.toMutableList().also { list -> list[index] = value } },
            onAddPoint = {
                if (points.size < 10) points = points + RevisionPointInput() else toast("Maksimal 10 poin revisi")
            },
            onRemovePoint = { index -> if (points.size > 1) points = points.filterIndexed { i, _ -> i != index } },
            onSaveDraft = {
                RevisionRequestStore.saveForm(
                    projectId,
                    RevisionFormDraft(aspects, points.map { RevisionPoint(title = it.title, area = it.area, instruction = it.instruction) })
                )
                sheet = ReviewSheet.NONE
                toast("Draf revisi disimpan")
            },
            onSubmit = submit@{
                val complete = points.filter { it.title.isNotBlank() && it.instruction.isNotBlank() }
                val partial = points.any { (it.title.isNotBlank()) != (it.instruction.isNotBlank()) }
                when {
                    complete.isEmpty() -> { toast("Tulis minimal satu poin revisi (judul dan uraian)"); return@submit }
                    partial -> { toast("Lengkapi judul dan uraian di setiap poin, atau hapus poin yang kosong"); return@submit }
                }
                val now = System.currentTimeMillis()
                val sent = RevisionRequestStore.submit(buildRevisionRequest(project, draft, aspects, complete, now))
                if (sent) {
                    NotificationStore.add(
                        FreelancerNotification(
                            id = "nt-revision-$projectId-v${draft.version}",
                            type = NotificationType.REVISION_REQUEST,
                            title = "Permintaan revisi pekerjaan",
                            body = "${project.ownerName} (${project.clientName}) meminta ${complete.size} perbaikan pada ${project.title}. " +
                                "Kirim draf revisi paling lambat ${formatShortDate(addWorkingDays(now, 2)).substringBeforeLast(' ')}.",
                            timeLabel = "Baru saja · ${formatBubbleTime(now).removeSuffix(" WIB")}",
                            day = NotificationDay.TODAY,
                            isRead = false,
                            projectId = projectId
                        )
                    )
                    toast("Permintaan revisi dikirim ke ${profile.fullName}")
                }
                sheet = ReviewSheet.NONE
                // Tahap berubah ke REVISION_REQUESTED → container kembali sekali (cabang di atas).
            },
            onDismiss = { sheet = ReviewSheet.NONE }
        )
        ReviewSheet.APPROVE -> {
            val feePercent = if (payment.amount > 0) (payment.platformFee * 100.0 / payment.amount).roundToInt() else 0
            ApproveCompletionSheet(
                ui = ApproveUi(
                    draftLabel = draft.versionLabel,
                    projectTitle = project.title,
                    freelancerName = profile.fullName,
                    freelancerFirstName = firstName(profile.fullName),
                    freelancerRole = shortRole(profile.headline),
                    freelancerPhotoUri = profile.photoUri,
                    files = files,
                    total = formatRupiah(payment.amount),
                    feeLabel = "Komisi platform ($feePercent%)",
                    fee = formatRupiah(payment.platformFee),
                    confirmed = confirmed,
                    rating = rating,
                    reviewText = reviewText
                ),
                onConfirmedChange = { confirmed = it },
                onRate = { rating = it },
                onReviewTextChange = { reviewText = it },
                onApprove = {
                    if (ResultReviewStore.stageOf(projectId) == ResultReviewStage.WAITING_REVIEW) {
                        val now = System.currentTimeMillis()
                        if (rating > 0) {
                            ResultReviewStore.submitReview(projectId, ProjectReview(rating, emptyList(), reviewText.trim(), now))
                        }
                        SubmittedOfferStore.markCompleted(projectId, now)
                        WalletStore.releaseProjectFunds(projectId, project.title, payment.freelancerNet, now)
                    }
                    sheet = ReviewSheet.NONE
                    // Tahap menjadi COMPLETED → cabang di atas membuka Proyek Selesai.
                },
                onDismiss = { sheet = ReviewSheet.NONE }
            )
        }
        ReviewSheet.NONE -> Unit
    }

    zoomed?.let { file ->
        // Pratinjau penuh gambar hasil (ikon zoom di Figma 17:1468).
        Dialog(onDismissRequest = { zoomed = null }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0xE6000000)).clickable { zoomed = null },
                contentAlignment = Alignment.Center
            ) {
                LocalImage(
                    uri = file.uri,
                    fallbackRes = null,
                    contentDescription = file.name,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

private fun buildRevisionRequest(
    project: FreelancerProject,
    draft: SubmittedDraft,
    aspects: Set<String>,
    points: List<RevisionPointInput>,
    now: Long
): RevisionRequest {
    val deadline = addWorkingDays(now, 2)
    val reference = draft.files.firstOrNull()
    return RevisionRequest(
        projectId = project.id,
        receivedLabel = "Baru saja, ${formatBubbleTime(now)}",
        clientName = project.ownerName,
        clientInitials = project.ownerName.split(' ').filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() },
        umkmName = project.clientName,
        isClientVerified = project.isClientVerified,
        projectTitle = project.title,
        deadlineProgress = 0f,
        deadlineLabel = "${formatShortDate(deadline)} (Tersisa 2 Hari Kerja)",
        focus = RevisionAspects.filter { it in aspects }.map { RevisionFocus(it, aspectTone(it)) },
        points = points.map {
            RevisionPoint(title = it.title.trim(), area = it.area.trim().ifEmpty { "Keseluruhan desain" }, instruction = it.instruction.trim())
        },
        referenceFileName = reference?.name ?: draft.versionLabel,
        referenceFileMeta = "${draft.versionLabel}, ${formatFileSize(reference?.sizeBytes)}",
        nextDraftVersion = "v${draft.version + 1}.0",
        clientGreeting = firstName(project.ownerName),
        draftVersion = draft.version,
        requestedAtMillis = now
    )
}
