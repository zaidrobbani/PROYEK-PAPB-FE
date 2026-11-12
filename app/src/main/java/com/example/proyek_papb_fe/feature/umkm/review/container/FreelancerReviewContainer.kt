package com.example.proyek_papb_fe.feature.umkm.review.container

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.proyek_papb_fe.core.data.FreelancerProfileStore
import com.example.proyek_papb_fe.core.data.FreelancerProjectDummyData
import com.example.proyek_papb_fe.core.data.ProjectReview
import com.example.proyek_papb_fe.core.data.ResultReviewStage
import com.example.proyek_papb_fe.core.data.ResultReviewStore
import com.example.proyek_papb_fe.core.data.SubmittedOfferStore
import com.example.proyek_papb_fe.feature.freelancer.chat.container.formatShortDate
import com.example.proyek_papb_fe.feature.umkm.review.component.FreelancerReviewScreen
import com.example.proyek_papb_fe.feature.umkm.review.component.FreelancerReviewUiState

/**
 * Container Beri Ulasan (Figma 17:6591) untuk [projectId] yang sudah Selesai. Ulasan yang
 * sudah ada (mis. bintang dari sheet persetujuan) dipakai sebagai isian awal. "Kirim ulasan"
 * menyimpan ulasan lalu [onDone]; "Lewati untuk Sekarang" langsung [onSkip].
 */
@Composable
fun FreelancerReviewContainer(
    projectId: Int,
    onBack: () -> Unit,
    onDone: () -> Unit,
    onSkip: () -> Unit
) {
    val context = LocalContext.current
    val project = FreelancerProjectDummyData.getById(projectId)
    if (ResultReviewStore.stageOf(projectId) != ResultReviewStage.COMPLETED || project == null) {
        LaunchedEffect(projectId) { onBack() }
        return
    }
    val existing = remember(projectId) { ResultReviewStore.review(projectId) }
    val attachment = remember(projectId) { ResultReviewStore.latestDraft(projectId)?.fileUis()?.firstOrNull { it.isImage } }
    var rating by remember { mutableIntStateOf(existing?.rating ?: 0) }
    var highlights by remember { mutableStateOf(existing?.tags?.toSet() ?: emptySet()) }
    var text by remember { mutableStateOf(existing?.text.orEmpty()) }
    var includeAttachment by remember { mutableStateOf(true) }
    var submitted by remember { mutableStateOf(false) }

    val profile = FreelancerProfileStore.profile
    val completedAt = SubmittedOfferStore.get(projectId)?.completedAtMillis ?: System.currentTimeMillis()

    FreelancerReviewScreen(
        state = FreelancerReviewUiState(
            freelancerName = profile.fullName,
            freelancerFirstName = firstName(profile.fullName),
            freelancerPhotoUri = profile.photoUri,
            projectTitle = project.title,
            completedDate = formatShortDate(completedAt),
            rating = rating,
            highlights = highlights,
            text = text,
            attachment = attachment,
            includeAttachment = includeAttachment
        ),
        onBack = onBack,
        onRate = { rating = it },
        onToggleHighlight = { highlights = if (it in highlights) highlights - it else highlights + it },
        onTextChange = { text = it },
        onToggleAttachment = { includeAttachment = !includeAttachment },
        onSubmit = {
            if (!submitted && rating in 1..5) {
                submitted = true
                ResultReviewStore.submitReview(projectId, ProjectReview(rating, highlights.toList(), text.trim(), System.currentTimeMillis()))
                Toast.makeText(context, "Ulasan untuk ${profile.fullName} terkirim", Toast.LENGTH_SHORT).show()
                onDone()
            }
        },
        onSkip = onSkip
    )
}
