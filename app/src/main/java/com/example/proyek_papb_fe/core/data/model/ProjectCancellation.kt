package com.example.proyek_papb_fe.core.data.model

enum class CancellationReason(val label: String) {
    PRICE_MISMATCH("Harga belum sesuai"),
    SCHEDULE_MISMATCH("Jadwal pengerjaan tidak cocok"),
    REQUIREMENT_CHANGED("Kebutuhan proyek berubah"),
    OTHER("Alasan lainnya")
}

data class ProjectCancellation(
    val reason: CancellationReason,
    val cancelledAtMillis: Long
)
