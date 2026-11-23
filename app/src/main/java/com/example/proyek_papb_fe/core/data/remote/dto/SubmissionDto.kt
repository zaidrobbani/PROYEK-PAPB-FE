package com.example.proyek_papb_fe.core.data.remote.dto

data class SubmissionFileDto(
    val id: String,
    val originalFileName: String,
    val mimeType: String,
    val fileSizeBytes: Long,
    val uploadedAt: String?,
    val downloadPath: String?
)

data class RevisionItemDto(val title: String, val detail: String, val area: String?)

data class SubmissionReviewDto(
    val decision: String,
    val decisionLabel: String,
    val reviewNote: String?,
    val focusAreas: List<String>?,
    val items: List<RevisionItemDto>?,
    val reviewedAt: String
)

data class SubmissionDto(
    val id: String,
    val projectId: String,
    val versionNumber: Int,
    val versionLabel: String,
    val submissionNote: String?,
    val resultUrl: String?,
    val submittedAt: String,
    val files: List<SubmissionFileDto>?,
    val review: SubmissionReviewDto?
)

data class SubmissionListDto(
    val projectId: String,
    val projectTitle: String,
    val projectStatus: String,
    val projectStatusLabel: String,
    val freelancer: UserSummaryDto?,
    val revisionCount: Int,
    val items: List<SubmissionDto>?
)

data class RevisionRequestBody(
    val note: String?,
    val focusAreas: List<String>,
    val items: List<RevisionItemDto>
)

data class ReviewRequest(
    val rating: Int,
    val reviewText: String?,
    val highlights: List<String>
)

data class ApproveSubmissionRequest(val review: ReviewRequest?)

data class HighlightDto(val value: String, val label: String)

data class ReviewDto(
    val id: String,
    val projectId: String,
    val rating: Int,
    val reviewText: String?,
    val highlights: List<HighlightDto>?,
    val createdAt: String
)

data class CompletionPaymentDto(
    val id: String,
    val transactionNumber: String?,
    val grossAmount: Long,
    val platformFeeAmount: Long,
    val freelancerAmount: Long,
    val fundStatus: String?,
    val fundStatusLabel: String?,
    val releasedAt: String?
)

data class CompletionDto(
    val projectId: String,
    val projectTitle: String,
    val projectStatus: String,
    val projectStatusLabel: String,
    val completedAt: String?,
    val freelancer: UserSummaryDto?,
    val submission: SubmissionDto,
    val payment: CompletionPaymentDto,
    val review: ReviewDto?
)

data class ReviewerDto(
    val userId: String,
    val name: String,
    val businessName: String?,
    val profileImageUrl: String?
)

data class PublicReviewDto(
    val id: String,
    val projectId: String,
    val rating: Int,
    val reviewText: String?,
    val highlights: List<HighlightDto>?,
    val createdAt: String,
    val projectTitle: String,
    val reviewer: ReviewerDto
)

data class FreelancerReviewsDto(
    val averageRating: Double?,
    val reviewCount: Int,
    val items: List<PublicReviewDto>?,
    val limit: Int,
    val offset: Int,
    val hasMore: Boolean
)
