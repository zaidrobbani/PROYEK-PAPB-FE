package com.example.proyek_papb_fe.core.domain.model

object WorkflowActionCode {
    const val SELECT_FREELANCER = "SELECT_FREELANCER"
    const val UPDATE_AGREEMENT = "UPDATE_AGREEMENT"
    const val EDIT_MILESTONE_PLAN = "EDIT_MILESTONE_PLAN"
    const val APPROVE_AGREEMENT = "APPROVE_AGREEMENT"
    const val REQUEST_AGREEMENT_CHANGES = "REQUEST_AGREEMENT_CHANGES"
    const val PAY = "PAY"
    const val START_WORK = "START_WORK"
    const val COMPLETE_MILESTONE = "COMPLETE_MILESTONE"
    const val SUBMIT_RESULT = "SUBMIT_RESULT"
    const val REQUEST_REVISION = "REQUEST_REVISION"
    const val APPROVE_RESULT = "APPROVE_RESULT"
    const val GIVE_REVIEW = "GIVE_REVIEW"
    const val CANCEL_PROJECT = "CANCEL_PROJECT"
}

object CancelReasonCode {
    const val HARGA_TIDAK_SESUAI = "HARGA_TIDAK_SESUAI"
    const val JADWAL_TIDAK_COCOK = "JADWAL_TIDAK_COCOK"
    const val KEBUTUHAN_BERUBAH = "KEBUTUHAN_BERUBAH"
    const val LAINNYA = "LAINNYA"
}

data class ProjectSummary(
    val id: String,
    val title: String,
    val description: String,
    val categoryId: String,
    val budget: Long,
    val deadline: String,
    val deadlineMillis: Long?,
    val status: String,
    val statusLabel: String,
    val publishedAtMillis: Long?,
    val startedAtMillis: Long?,
    val completedAtMillis: Long?
)

data class PaymentSummary(
    val id: String,
    val status: String,
    val statusLabel: String,
    val fundStatus: String?,
    val fundStatusLabel: String?,
    val grossAmount: Long,
    val platformFee: Long,
    val freelancerAmount: Long,
    val transactionNumber: String?,
    val paidAtMillis: Long?,
    val releasedAtMillis: Long?,
    val expiresAtMillis: Long?
)

data class MyProjectAgreement(
    val agreedPrice: Long,
    val agreedDeadline: String,
    val durationDays: Int,
    val status: String
)

data class MyProjectItem(
    val project: ProjectSummary,
    val counterpart: UserSummary?,
    val agreement: MyProjectAgreement?,
    val payment: PaymentSummary?,
    val applicationCount: Int,
    val progressPercent: Double?
)

data class MyProjects(
    val items: List<MyProjectItem>,
    val statusCounts: Map<String, Int>
)

data class Agreement(
    val id: String,
    val freelancerId: String,
    val applicationId: String?,
    val scopeOfWork: String,
    val agreedPrice: Long,
    val agreedDeadline: String,
    val agreedDeadlineMillis: Long?,
    val durationDays: Int,
    val commissionPercent: Int,
    val platformFee: Long,
    val freelancerAmount: Long,
    val paymentMethodLabel: String?,
    val status: String,
    val statusLabel: String,
    val version: Int,
    val umkmApprovedAtMillis: Long?,
    val freelancerApprovedAtMillis: Long?,
    val approvedByMe: Boolean,
    val waitingFor: List<String>,
    val changeRequestNote: String?,
    val changeRequestedAtMillis: Long?,
    val createdAtMillis: Long?
)

data class LatestSubmission(
    val id: String,
    val versionNumber: Int,
    val versionLabel: String,
    val submittedAtMillis: Long?,
    val decision: String?,
    val decisionLabel: String?
)

data class WorkflowHistory(
    val id: String,
    val newStatus: String,
    val newStatusLabel: String,
    val action: String,
    val actionLabel: String,
    val changedByUserId: String?,
    val reason: String?,
    val changedAtMillis: Long?
)

data class Cancellation(
    val reason: String?,
    val reasonLabel: String?,
    val note: String?,
    val cancelledBy: String?,
    val cancelledAtMillis: Long?
)

data class WorkflowDetail(
    val project: ProjectSummary,
    val myRole: String,
    val umkm: UserSummary,
    val freelancer: UserSummary?,
    val agreement: Agreement?,
    val payment: PaymentSummary?,
    val latestSubmission: LatestSubmission?,
    val revisionCount: Int,
    val hasReview: Boolean,
    val availableActions: List<String>,
    val history: List<WorkflowHistory>,
    val cancellation: Cancellation?
) {
    fun can(action: String): Boolean = action in availableActions
}

data class Milestone(
    val id: String,
    val name: String,
    val description: String,
    val weightPercent: Double,
    val isDone: Boolean,
    val sortOrder: Int,
    val completedAtMillis: Long?
)

data class MilestonePlan(
    val items: List<Milestone>,
    val totalWeight: Double,
    val isComplete: Boolean
)

data class MilestoneDraft(val name: String, val description: String, val weightPercent: Int)

data class MilestoneProgress(
    val progressPercent: Double,
    val totalMilestones: Int,
    val completedMilestones: Int,
    val isPlanComplete: Boolean
)

data class DelayRisk(
    val level: String,
    val elapsedDays: Int,
    val totalDays: Int,
    val expectedPercent: Double,
    val actualPercent: Double
)
