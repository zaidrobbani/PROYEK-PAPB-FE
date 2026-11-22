package com.example.proyek_papb_fe.core.data.model

enum class PaymentStatus { READY, PROCESSING, PAID }

enum class PaymentMethod(val label: String) { QRIS("QRIS") }

data class ProjectPayment(
    val paymentId: String,
    val projectId: String,
    val amount: Long,
    val platformFee: Long,
    val freelancerNet: Long,
    val method: PaymentMethod,
    val status: PaymentStatus,
    val createdAtMillis: Long,
    val paidAtMillis: Long? = null
)
