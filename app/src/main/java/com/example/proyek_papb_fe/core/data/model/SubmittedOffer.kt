package com.example.proyek_papb_fe.core.data.model

/**
 * Penawaran yang sudah dikirim dari "Ajukan Penawaran" (read-only).
 * Nilai penawaran = pagu klien; biaya & bersih dihitung saat dikirim.
 */
data class SubmittedOffer(
    val projectId: Int,
    val offerAmount: Long,
    val platformFee: Long,
    val netAmount: Long,
    /** Durasi kerja yang diajukan, dalam hari. */
    val durationDays: Int,
    val message: String,
    /** Waktu kirim (epoch millis). */
    val submittedAtMillis: Long,
    /**
     * false = masih ditinjau pemilik UMKM; true = disetujui, lanjut negosiasi.
     * Belum ada backend: persetujuan disimulasikan oleh layar Status Pengajuan.
     */
    val isApproved: Boolean = false,
    /** Terisi jika project dibatalkan dari ruang negosiasi; null = masih aktif. */
    val cancellation: ProjectCancellation? = null,
    /**
     * Waktu kesepakatan final (epoch millis): UMKM menyetujui rencana pengerjaan dan
     * freelancer resmi mengerjakan project. null = belum ada kesepakatan.
     * Belum ada backend: persetujuan rencana disimulasikan oleh layar Rencana Pengerjaan.
     */
    val agreedAtMillis: Long? = null,
    /**
     * Waktu project selesai dikerjakan (epoch millis), hanya bermakna setelah kesepakatan
     * final. null = belum selesai. Belum ada backend: diisi oleh data contoh.
     */
    val completedAtMillis: Long? = null
) {
    val isCancelled: Boolean get() = cancellation != null

    /**
     * Kesepakatan final tercapai (tetap true setelah project selesai), sehingga project
     * yang sedang dikerjakan maupun yang sudah selesai sama-sama tidak bisa dibatalkan.
     */
    val isAgreed: Boolean get() = agreedAtMillis != null && !isCancelled

    /** Project selesai dikerjakan (tab Project "Selesai"), berbeda dari "Project Berjalan" (sedang dikerjakan). */
    val isCompleted: Boolean get() = isAgreed && completedAtMillis != null

    /** Status siklus project (dipakai bersama tab Project dan tab Chat). */
    val status: ProjectStatus get() = when {
        isCancelled -> ProjectStatus.DIBATALKAN
        !isApproved -> ProjectStatus.MENUNGGU_KESEPAKATAN
        isCompleted -> ProjectStatus.SELESAI
        isAgreed -> ProjectStatus.PROJECT_BERJALAN
        else -> ProjectStatus.NEGOSIASI
    }
}
