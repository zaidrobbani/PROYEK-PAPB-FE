package com.example.proyek_papb_fe.core.data

import androidx.compose.runtime.mutableStateMapOf
import com.example.proyek_papb_fe.core.data.model.WorkPlan
import com.example.proyek_papb_fe.core.data.model.WorkPlanMilestone
import com.example.proyek_papb_fe.core.data.model.WorkPlanStatus
import com.example.proyek_papb_fe.core.data.model.isLocked

/**
 * Rencana pengerjaan di memori, per projectId (sementara, sebelum ada backend).
 * Bertahan selama sesi aplikasi. Berbasis snapshot state agar layar yang membaca
 * [get] ikut recompose saat rencana project itu berubah.
 */
object WorkPlanStore {

    private val plans = mutableStateMapOf<Int, WorkPlan>()

    fun get(projectId: Int): WorkPlan? = plans[projectId]

    /** "Simpan Draf": boleh belum lengkap. Rencana yang sudah diajukan tidak ditimpa. */
    fun saveDraft(
        projectId: Int,
        milestones: List<WorkPlanMilestone>,
        savedAtMillis: Long = System.currentTimeMillis()
    ) {
        if (plans[projectId]?.status?.isLocked == true) return
        plans[projectId] = WorkPlan(projectId, milestones, WorkPlanStatus.DRAFT, savedAtMillis)
    }

    /**
     * UMKM "Setujui Rencana" (Figma 270:663): hanya untuk rencana yang sedang diajukan.
     * Kesepakatan terkunci; project menunggu pembayaran UMKM (lihat [PaymentStore]).
     */
    fun approve(projectId: Int, approvedAtMillis: Long = System.currentTimeMillis()) {
        val plan = plans[projectId] ?: return
        if (plan.status == WorkPlanStatus.SUBMITTED) {
            plans[projectId] = plan.copy(status = WorkPlanStatus.APPROVED, updatedAtMillis = approvedAtMillis)
        }
    }

    /** UMKM "Minta Ubah": rencana yang diajukan kembali menjadi draf agar freelancer bisa merevisi. */
    fun requestChanges(projectId: Int, atMillis: Long = System.currentTimeMillis()) {
        val plan = plans[projectId] ?: return
        if (plan.status == WorkPlanStatus.SUBMITTED) {
            plans[projectId] = plan.copy(status = WorkPlanStatus.DRAFT, updatedAtMillis = atMillis)
        }
    }

    /** "Ajukan ke UMKM": pemanggil wajib sudah memvalidasi (total bobot 100%). */
    fun submit(
        projectId: Int,
        milestones: List<WorkPlanMilestone>,
        submittedAtMillis: Long = System.currentTimeMillis()
    ) {
        plans[projectId] = WorkPlan(projectId, milestones, WorkPlanStatus.SUBMITTED, submittedAtMillis)
    }
}
