package com.example.proyek_papb_fe.core.data.remote.dto

data class PaymentProjectDto(val id: String, val title: String, val status: String, val statusLabel: String)

data class PaymentAgreementDto(
    val scopeOfWork: String,
    val agreedPrice: Long,
    val agreedDeadline: String,
    val durationDays: Int
)

data class PaymentAttemptDto(
    val attemptNumber: Int,
    val status: String,
    val failureReason: String?,
    val attemptedAt: String
)

data class PaymentDetailDto(
    val id: String,
    val transactionNumber: String?,
    val status: String,
    val statusLabel: String,
    val fundStatus: String?,
    val fundStatusLabel: String?,
    val displayStatus: String,
    val displayStatusLabel: String,
    val paymentMethod: String,
    val paymentMethodLabel: String,
    val isSimulation: Boolean?,
    val grossAmount: Long,
    val platformFeeAmount: Long,
    val freelancerAmount: Long,
    val totalAmount: Long,
    val commissionPercent: Int,
    val expiresAt: String?,
    val secondsRemaining: Int?,
    val paidAt: String?,
    val releasedAt: String?,
    val createdAt: String?,
    val project: PaymentProjectDto,
    val agreement: PaymentAgreementDto,
    val umkm: UserSummaryDto,
    val freelancer: UserSummaryDto,
    val attempts: List<PaymentAttemptDto>?
)

data class SimulatePaymentRequest(val outcome: String)

data class PaymentHistoryItemDto(
    val paymentId: String,
    val transactionNumber: String?,
    val projectId: String,
    val projectTitle: String,
    val freelancerId: String,
    val freelancerName: String,
    val amount: Long,
    val status: String,
    val fundStatus: String?,
    val displayStatus: String,
    val displayStatusLabel: String,
    val paidAt: String?,
    val createdAt: String?,
    val occurredAt: String
)

data class PaymentHistoryDto(
    val items: List<PaymentHistoryItemDto>?,
    val limit: Int,
    val offset: Int,
    val hasMore: Boolean,
    /** Key huruf besar: ALL, PENDING, HELD, COMPLETED, CANCELLED. */
    val counts: Map<String, Int>?
)

data class WalletDto(
    val availableBalance: Long,
    val heldBalance: Long,
    val totalBalance: Long
)

data class WalletEntryDto(
    val id: String,
    val type: String,
    val typeLabel: String,
    val direction: String?,
    val amount: Long,
    val paymentId: String?,
    val projectId: String?,
    val projectTitle: String?,
    val stillHeld: Boolean,
    val createdAt: String
)
