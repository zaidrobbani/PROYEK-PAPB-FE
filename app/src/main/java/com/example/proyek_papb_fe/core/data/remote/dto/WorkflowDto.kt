package com.example.proyek_papb_fe.core.data.remote.dto

data class ProjectSummaryDto(
    val id: String,
    val title: String,
    val description: String,
    val categoryId: String,
    val budget: Long,
    val deadline: String,
    val status: String,
    val statusLabel: String,
    val publishedAt: String?,
    val startedAt: String?,
    val completedAt: String?
)

data class UserSummaryDto(
    val id: String,
    val name: String,
    val subtitle: String?,
    val profileImageUrl: String?,
    val city: String?,
    val averageRating: Double?,
    val reviewCount: Int?
)

data class MyProjectAgreementDto(
    val agreedPrice: Long,
    val agreedDeadline: String,
    val durationDays: Int,
    val status: String
)

data class PaymentSummaryDto(
    val id: String,
    val status: String,
    val statusLabel: String,
    val fundStatus: String?,
    val fundStatusLabel: String?,
    val grossAmount: Long,
    val platformFeeAmount: Long,
    val freelancerAmount: Long,
    val transactionNumber: String?,
    val paidAt: String?,
    val releasedAt: String?,
    val expiresAt: String?
)

data class MyProjectItemDto(
    val project: ProjectSummaryDto,
    val counterpart: UserSummaryDto?,
    val agreement: MyProjectAgreementDto?,
    val payment: PaymentSummaryDto?,
    val applicationCount: Int,
    val progressPercent: Double?
)

data class MyProjectsDto(
    val items: List<MyProjectItemDto>?,
    val limit: Int,
    val offset: Int,
    val hasMore: Boolean,
    val statusCounts: Map<String, Int>?
)

data class AgreementDto(
    val id: String,
    val projectId: String,
    val freelancerId: String,
    val applicationId: String?,
    val selectionSource: String?,
    val scopeOfWork: String,
    val agreedPrice: Long,
    val agreedDeadline: String,
    val durationDays: Int,
    val commissionPercent: Int,
    val platformFeeAmount: Long,
    val freelancerAmount: Long,
    val paymentMethod: String?,
    val paymentMethodLabel: String?,
    val status: String,
    val statusLabel: String,
    val agreementVersion: Int,
    val umkmApprovedAt: String?,
    val freelancerApprovedAt: String?,
    val approvedByMe: Boolean,
    val waitingFor: List<String>?,
    val changeRequestNote: String?,
    val changeRequestedAt: String?,
    val createdAt: String?,
    val updatedAt: String?
)

data class LatestSubmissionDto(
    val id: String,
    val versionNumber: Int,
    val versionLabel: String,
    val submittedAt: String,
    val decision: String?,
    val decisionLabel: String?
)

data class WorkflowActionDto(val action: String, val label: String)

data class HistoryItemDto(
    val id: String,
    val previousStatus: String?,
    val previousStatusLabel: String?,
    val newStatus: String,
    val newStatusLabel: String,
    val action: String,
    val actionLabel: String,
    val changeSource: String?,
    val changedByUserId: String?,
    val reasonCode: String?,
    val changeReason: String?,
    val changedAt: String
)

data class CancellationDto(
    val reason: String?,
    val reasonLabel: String?,
    val note: String?,
    val cancelledBy: String?,
    val cancelledAt: String,
    val hasPayment: Boolean?
)

data class WorkflowDetailDto(
    val project: ProjectSummaryDto,
    val myRole: String,
    val umkm: UserSummaryDto,
    val freelancer: UserSummaryDto?,
    val agreement: AgreementDto?,
    val payment: PaymentSummaryDto?,
    val latestSubmission: LatestSubmissionDto?,
    val revisionCount: Int,
    val hasReview: Boolean,
    val availableActions: List<WorkflowActionDto>?,
    val history: List<HistoryItemDto>?,
    val cancellation: CancellationDto?
)

data class SelectFreelancerRequest(
    val applicationId: String?,
    val freelancerId: String? = null,
    val scopeOfWork: String? = null
)

data class UpdateAgreementRequest(
    val scopeOfWork: String? = null,
    val agreedPrice: Long? = null,
    val agreedDeadline: String? = null,
    val durationDays: Int? = null
)

data class ApproveAgreementRequest(val agreementVersion: Int?)

data class RequestChangesRequest(val note: String)

data class CancelProjectRequest(val reason: String, val note: String?)
