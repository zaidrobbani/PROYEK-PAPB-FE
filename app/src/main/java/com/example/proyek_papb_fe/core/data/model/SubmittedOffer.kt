package com.example.proyek_papb_fe.core.data.model

data class SubmittedOffer(
    val projectId: String,
    val offerAmount: Long,
    val platformFee: Long,
    val netAmount: Long,
    val durationDays: Int,
    val message: String,
    val submittedAtMillis: Long,



    val isApproved: Boolean = false,
    val cancellation: ProjectCancellation? = null,




    val agreedAtMillis: Long? = null,



    val completedAtMillis: Long? = null
) {
    val isCancelled: Boolean get() = cancellation != null

    val isAgreed: Boolean get() = agreedAtMillis != null && !isCancelled

    val isCompleted: Boolean get() = isAgreed && completedAtMillis != null

    val status: ProjectStatus get() = when {
        isCancelled -> ProjectStatus.DIBATALKAN
        !isApproved -> ProjectStatus.MENUNGGU_KESEPAKATAN
        isCompleted -> ProjectStatus.SELESAI
        isAgreed -> ProjectStatus.PROJECT_BERJALAN
        else -> ProjectStatus.NEGOSIASI
    }
}
