package com.example.proyek_papb_fe.core.data.model

/** Satu tahap di "Rencana Pengerjaan (Freelancer)" (Figma 88:2). */
data class WorkPlanMilestone(
    val name: String,
    /** Bobot dalam persen; null = belum diisi (hanya mungkin pada draf). */
    val weightPercent: Int?
)

/**
 * DRAFT = masih disusun freelancer (chip "Draft" di Figma 88:18).
 * SUBMITTED = sudah "Ajukan ke UMKM", menunggu persetujuan sebelum pembayaran.
 * APPROVED = UMKM menekan "Setujui Rencana" (Figma 270:663): kedua pihak sepakat, kesepakatan
 *   terkunci dan project menunggu pembayaran UMKM. Project baru resmi berjalan setelah dibayar.
 */
enum class WorkPlanStatus { DRAFT, SUBMITTED, APPROVED }

/** Rencana sudah diajukan ke UMKM (menunggu persetujuan atau sudah disetujui): tidak bisa diedit lagi. */
val WorkPlanStatus.isLocked: Boolean get() = this != WorkPlanStatus.DRAFT

/** Rencana pengerjaan satu project (langkah setelah negosiasi, sebelum pembayaran). */
data class WorkPlan(
    val projectId: Int,
    val milestones: List<WorkPlanMilestone>,
    val status: WorkPlanStatus,
    /** Waktu terakhir disimpan/diajukan (epoch millis). */
    val updatedAtMillis: Long
)
