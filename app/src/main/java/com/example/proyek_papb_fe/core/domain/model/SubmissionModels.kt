package com.example.proyek_papb_fe.core.domain.model

object SubmissionDecision {
    const val APPROVED = "APPROVED"
    const val REVISION_REQUESTED = "REVISION_REQUESTED"
}

data class SubmissionFile(
    val id: String,
    val fileName: String,
    val mimeType: String,
    val sizeBytes: Long,
    val downloadPath: String
)

data class RevisionItem(val title: String, val detail: String, val area: String?)

data class SubmissionReview(
    val decision: String,
    val decisionLabel: String,
    val note: String?,
    val focusAreas: List<String>,
    val items: List<RevisionItem>,
    val reviewedAtMillis: Long?
)

data class Submission(
    val id: String,
    val projectId: String,
    val versionNumber: Int,
    val versionLabel: String,
    val note: String?,
    val resultUrl: String?,
    val submittedAtMillis: Long?,
    val files: List<SubmissionFile>,
    val review: SubmissionReview?
)

data class SubmissionList(
    val projectId: String,
    val projectTitle: String,
    val projectStatus: String,
    val projectStatusLabel: String,
    val freelancer: UserSummary?,
    val revisionCount: Int,
    val items: List<Submission>
) {
    val latest: Submission? get() = items.maxByOrNull { it.versionNumber }
}

object ReviewHighlightCode {
    const val RESPONSIF_KOMUNIKATIF = "RESPONSIF_KOMUNIKATIF"
    const val HASIL_BERKUALITAS = "HASIL_BERKUALITAS"
    const val TEPAT_WAKTU = "TEPAT_WAKTU"
    const val PAHAM_KEBUTUHAN = "PAHAM_KEBUTUHAN"
}

data class ReviewInput(val rating: Int, val text: String?, val highlights: List<String>)

data class Review(
    val id: String,
    val projectId: String,
    val rating: Int,
    val text: String?,
    val highlightLabels: List<String>,
    val createdAtMillis: Long?
)

data class Completion(
    val projectId: String,
    val projectTitle: String,
    val completedAtMillis: Long?,
    val freelancer: UserSummary?,
    val grossAmount: Long,
    val platformFee: Long,
    val freelancerAmount: Long,
    val transactionNumber: String?,
    val fundStatusLabel: String?,
    val review: Review?
)

data class PublicReview(
    val id: String,
    val projectTitle: String,
    val rating: Int,
    val text: String?,
    val highlightLabels: List<String>,
    val reviewerName: String,
    val businessName: String?,
    val createdAtMillis: Long?
)

data class FreelancerReviews(
    val averageRating: Double?,
    val reviewCount: Int,
    val items: List<PublicReview>
)
