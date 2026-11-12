package com.example.proyek_papb_fe.core.data

import androidx.compose.runtime.mutableStateMapOf
import com.example.proyek_papb_fe.core.data.model.ProjectStatus
import com.example.proyek_papb_fe.core.data.model.SubmittedDraft

/** Tahap review hasil UMKM untuk satu project (diturunkan dari store yang sudah ada). */
enum class ResultReviewStage {
    /** Belum ada draf yang dikirim freelancer. */
    NO_SUBMISSION,
    /** Draf terbaru menunggu review UMKM (Figma 17:1468 "Menunggu Review"). */
    WAITING_REVIEW,
    /** Revisi sudah diminta untuk draf terbaru; menunggu draf baru dari freelancer. */
    REVISION_REQUESTED,
    /** Hasil disetujui, project Selesai (Figma 17:6338). */
    COMPLETED
}

/** Ulasan UMKM untuk freelancer (Figma 17:6140 bagian ulasan / 17:6591). */
data class ProjectReview(
    val rating: Int,
    val tags: List<String>,
    val text: String,
    val submittedAtMillis: Long
)

/**
 * Status review hasil per project, diturunkan dari sumber yang sudah ada: penawaran/project
 * ([SubmittedOfferStore]), draf terkirim ([WorkDraftStore]), dan permintaan revisi
 * ([RevisionRequestStore]). Tidak ada status project kedua: penyelesaian memakai
 * [SubmittedOfferStore.markCompleted]. Ulasan UMKM disimpan per project di sini.
 */
object ResultReviewStore {

    private val reviews = mutableStateMapOf<Int, ProjectReview>()

    /** Draf terbaru yang dikirim freelancer untuk project ini (lewat ruang chat project). */
    fun latestDraft(projectId: Int): SubmittedDraft? =
        // Hanya membaca percakapan yang sudah ada (tanpa membuat baru) agar aman dipanggil saat komposisi.
        ChatStore.getAll().firstOrNull { it.projectId == projectId }?.id?.let(WorkDraftStore::latestSubmitted)

    /** null = project tidak dalam tahap pasca-pembayaran (belum dibayar / dibatalkan / tidak ada). */
    fun stageOf(projectId: Int): ResultReviewStage? {
        val offer = SubmittedOfferStore.get(projectId) ?: return null
        return when (offer.status) {
            ProjectStatus.SELESAI -> ResultReviewStage.COMPLETED
            ProjectStatus.PROJECT_BERJALAN -> {
                val draft = latestDraft(projectId) ?: return ResultReviewStage.NO_SUBMISSION
                val revision = RevisionRequestStore.forProject(projectId)
                if (revision != null && revision.draftVersion >= draft.version) ResultReviewStage.REVISION_REQUESTED
                else ResultReviewStage.WAITING_REVIEW
            }
            else -> null
        }
    }

    fun review(projectId: Int): ProjectReview? = reviews[projectId]

    /** Simpan ulasan (sekali per project; kiriman berikutnya mengganti yang lama). */
    fun submitReview(projectId: Int, review: ProjectReview) {
        reviews[projectId] = review
    }
}
