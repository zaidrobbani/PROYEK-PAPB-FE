package com.example.proyek_papb_fe.core.data.model

/** Pilihan "Alasan pembatalan" di Figma "Modal Batalkan Project (Freelancer)" (26:87). */
enum class CancellationReason(val label: String) {
    PRICE_MISMATCH("Harga belum sesuai"),
    SCHEDULE_MISMATCH("Jadwal pengerjaan tidak cocok"),
    REQUIREMENT_CHANGED("Kebutuhan project berubah"),
    OTHER("Alasan lainnya")
}

/** Pembatalan project sebelum pembayaran (tanpa biaya), dicatat per penawaran. */
data class ProjectCancellation(
    val reason: CancellationReason,
    /** Waktu pembatalan (epoch millis). */
    val cancelledAtMillis: Long
)
