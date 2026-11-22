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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.domain.model.RevisionItem
import com.example.proyek_papb_fe.core.ui.components.ErrorContent
import com.example.proyek_papb_fe.core.ui.components.LoadingContent
import com.example.proyek_papb_fe.core.ui.components.LocalImage
import com.example.proyek_papb_fe.core.util.formatShortDate
import com.example.proyek_papb_fe.core.util.formatTime
import com.example.proyek_papb_fe.feature.umkm.payment.component.formatRupiah
import com.example.proyek_papb_fe.feature.umkm.payment.container.shortRole
import com.example.proyek_papb_fe.feature.umkm.review.component.ApproveCompletionSheet
import com.example.proyek_papb_fe.feature.umkm.review.component.ApproveUi
import com.example.proyek_papb_fe.feature.umkm.review.component.ResultReviewScreen
import com.example.proyek_papb_fe.feature.umkm.review.component.ResultReviewUiState
import com.example.proyek_papb_fe.feature.umkm.review.component.ReviewFileUi
import com.example.proyek_papb_fe.feature.umkm.review.component.RevisionFormUi
import com.example.proyek_papb_fe.feature.umkm.review.component.RevisionPointInput
import com.example.proyek_papb_fe.feature.umkm.review.component.RevisionRequestSheet
import com.example.proyek_papb_fe.feature.umkm.review.component.formatFileSize
import com.example.proyek_papb_fe.feature.umkm.review.viewmodel.ProjectResultViewModel
import com.example.proyek_papb_fe.feature.umkm.review.viewmodel.ResultStage
import java.util.Locale

private val RevisionAspects = listOf("Tipografi & Teks", "Warna", "Tata Letak", "Ilustrasi & Motif", "Ukuran & Format Berkas")

private enum class ReviewSheet { NONE, REVISION, APPROVE }

@Composable
fun ResultReviewContainer(
    projectId: String,
    onBack: () -> Unit,
    onOpenChat: (projectId: String) -> Unit,
    onCompleted: (projectId: String) -> Unit,
    viewModel: ProjectResultViewModel = viewModel(
        factory = ProjectResultViewModel.factory(
            projectId,
            AppContainer.workflowRepository,
            AppContainer.submissionRepository,
            AppContainer.fileRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val toast: (String) -> Unit = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onMessageShown()
        }
    }
    LaunchedEffect(uiState.completed, uiState.stage, uiState.isLoading) {
        if (uiState.completed || (!uiState.isLoading && uiState.stage == ResultStage.COMPLETED)) {
            viewModel.onCompletedHandled()
            onCompleted(projectId)
        }
    }

    val detail = uiState.detail
    val draft = uiState.latest
    if (detail == null) {
        if (uiState.isLoading) LoadingContent() else ErrorContent(uiState.errorMessage.orEmpty(), onRetry = viewModel::load)
        return
    }
    if (draft == null || uiState.stage != ResultStage.WAITING_REVIEW) {
        ErrorContent(
            message = when (uiState.stage) {
                ResultStage.REVISION_REQUESTED -> "Revisi sudah diminta. Menunggu freelancer mengirim hasil revisi."
                else -> "Belum ada hasil kerja yang perlu ditinjau untuk project ini."
            },
            onRetry = viewModel::load
        )
        return
    }

    val freelancer = detail.freelancer
    val freelancerName = freelancer?.name ?: "Freelancer"
    val files = remember(draft, uiState.localUris) { draft.files.map { it.toReviewFileUi(uiState.localUris[it.id]) } }
    val sentAtMillis = draft.submittedAtMillis ?: System.currentTimeMillis()
    val payment = detail.payment
    val amount = payment?.grossAmount ?: detail.agreement?.agreedPrice ?: detail.project.budget
    var sheet by rememberSaveable { mutableStateOf(ReviewSheet.NONE) }
    var zoomed by remember { mutableStateOf<ReviewFileUi?>(null) }
    var aspects by remember(projectId) { mutableStateOf(emptySet<String>()) }
    var points by remember(projectId) { mutableStateOf(listOf(RevisionPointInput())) }
    var confirmed by remember { mutableStateOf(false) }
    var rating by remember { mutableIntStateOf(0) }
    var reviewText by remember { mutableStateOf("") }

    ResultReviewScreen(
        state = ResultReviewUiState(
            projectTitle = detail.project.title,
            freelancerName = freelancerName,
            freelancerPhotoUri = freelancer?.photoUrl,
            freelancerRating = freelancer?.averageRating?.let { String.format(Locale.US, "%.1f", it) } ?: "-",
            projectValue = formatRupiah(amount),
            note = draft.note.orEmpty().ifBlank { draft.resultUrl?.let { "Tautan hasil: $it" }.orEmpty() },
            draftLabel = "Draf Pengiriman ${draft.versionLabel}",
            sentAt = formatSentAt(sentAtMillis),
            files = files
        ),
        onBack = onBack,
        onChatClick = { onOpenChat(projectId) },
        onZoomClick = {
            zoomed = files.firstOrNull { it.isImage && !it.uri.startsWith(RemoteFilePrefix) }
            if (zoomed == null) toast("Pratinjau besar hanya tersedia untuk berkas gambar yang sudah dimuat")
        },
        onDownloadFile = { file -> file.fileId(draft.files, uiState.localUris)?.let(viewModel::download) },
        onRequestRevision = { sheet = ReviewSheet.REVISION },
        onApprove = { sheet = ReviewSheet.APPROVE }
    )

    when (sheet) {
        ReviewSheet.REVISION -> RevisionRequestSheet(
            form = RevisionFormUi(
                draftLabel = draft.versionLabel,
                projectTitle = detail.project.title,
                reviewedDraft = "${draft.versionLabel}, Kiriman $freelancerName",
                receivedAt = "Diterima " + formatShortDate(sentAtMillis) + ", " + formatTime(sentAtMillis) + " WIB",
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
                // BE tidak menyimpan draf revisi; isian tetap tersimpan selama layar ini terbuka.
                sheet = ReviewSheet.NONE
                toast("Draf revisi disimpan sementara di perangkat")
            },
            onSubmit = submit@{
                val complete = points.filter { it.title.isNotBlank() && it.instruction.isNotBlank() }
                val partial = points.any { (it.title.isNotBlank()) != (it.instruction.isNotBlank()) }
                when {
                    complete.isEmpty() -> { toast("Tulis minimal satu poin revisi (judul dan uraian)"); return@submit }
                    partial -> { toast("Lengkapi judul dan uraian di setiap poin, atau hapus poin yang kosong"); return@submit }
                }
                viewModel.requestRevision(
                    focusAreas = RevisionAspects.filter { it in aspects },
                    items = complete.map {
                        RevisionItem(it.title.trim(), it.instruction.trim(), it.area.trim().ifEmpty { null })
                    }
                )
                sheet = ReviewSheet.NONE
            },
            onDismiss = { sheet = ReviewSheet.NONE }
        )
        ReviewSheet.APPROVE -> ApproveCompletionSheet(
            ui = ApproveUi(
                draftLabel = draft.versionLabel,
                projectTitle = detail.project.title,
                freelancerName = freelancerName,
                freelancerFirstName = firstName(freelancerName),
                freelancerRole = shortRole(freelancer?.subtitle.orEmpty()),
                freelancerPhotoUri = freelancer?.photoUrl,
                files = files,
                total = formatRupiah(amount),
                feeLabel = "Komisi platform (${detail.agreement?.commissionPercent ?: 5}%)",
                fee = formatRupiah(payment?.platformFee ?: detail.agreement?.platformFee ?: 0),
                confirmed = confirmed,
                rating = rating,
                reviewText = reviewText
            ),
            onConfirmedChange = { confirmed = it },
            onRate = { rating = it },
            onReviewTextChange = { reviewText = it },
            onApprove = {
                if (!uiState.isSubmitting) viewModel.approve(rating, reviewText)
                sheet = ReviewSheet.NONE
            },
            onDismiss = { sheet = ReviewSheet.NONE }
        )
        ReviewSheet.NONE -> Unit
    }

    zoomed?.let { file ->
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
