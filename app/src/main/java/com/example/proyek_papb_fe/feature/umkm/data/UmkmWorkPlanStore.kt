package com.example.proyek_papb_fe.feature.umkm.data

import androidx.compose.runtime.mutableStateListOf
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Status rencana pengerjaan dari sisi UMKM (Blueprint Fungsi 4, alur milestone langkah 3-4).
 * Hanya [MenungguPersetujuan] yang tampil di Figma; dua lainnya keadaan sesudah UMKM bertindak.
 */
enum class WorkPlanStatus(val label: String) {
    MenungguPersetujuan("Menunggu persetujuanmu"),
    DimintaUbah("Menunggu revisi freelancer"),
    Disetujui("Sudah disetujui")
}

/**
 * Rencana pengerjaan yang diusulkan freelancer. Rencana project yang sudah berjalan
 * ([projectId] diisi) membaca milestone, deadline, dan freelancer langsung dari
 * [UmkmProjectStore], jadi tidak ada salinan milestone. Rencana dari negosiasi
 * ([conversationId] diisi) menyimpan usulannya sendiri sampai disetujui.
 */
data class UmkmWorkPlan(
    val id: String,
    val projectId: String? = null,
    val conversationId: String? = null,
    val proposedMilestones: List<ProjectMilestone> = emptyList(),
    val proposedStart: LocalDate? = null,
    val proposedDeadline: LocalDate? = null,
    val status: WorkPlanStatus
)

/** Data siap tampil untuk screen Tinjau Rencana Pengerjaan. */
data class WorkPlanDetail(
    val plan: UmkmWorkPlan,
    val projectTitle: String,
    val freelancerName: String,
    val milestones: List<ProjectMilestone>,
    val deadline: LocalDate?,
    val durationDays: Long?
) {
    val totalWeight: Int get() = milestones.sumOf { it.weightPercent }

    /** Blueprint: total bobot = 100% dan setiap bobot > 0% serta tidak lebih dari 100%. */
    val weightsValid: Boolean
        get() = milestones.isNotEmpty() && totalWeight == 100 && milestones.all { it.weightPercent in 1..100 }
}

/** Hasil tindakan UMKM pada rencana, untuk umpan balik di layar. */
enum class WorkPlanReviewResult { Approved, ChangesRequested, InvalidWeights, IncompleteAgreement, NotAllowed }

/**
 * Aturan peninjauan (Blueprint alur milestone langkah 4): UMKM memeriksa rencana; jika belum
 * sesuai, meminta freelancer merevisi; jika sesuai, menyetujui kesepakatan final. Hanya rencana
 * berstatus Menunggu Persetujuan yang bisa ditindak, dan persetujuan wajib bobot valid.
 * UMKM tidak bisa mengubah nama atau bobot milestone.
 */
fun applyWorkPlanReview(detail: WorkPlanDetail, approve: Boolean): Pair<UmkmWorkPlan, WorkPlanReviewResult> {
    val plan = detail.plan
    if (plan.status != WorkPlanStatus.MenungguPersetujuan) return plan to WorkPlanReviewResult.NotAllowed
    if (!approve) return plan.copy(status = WorkPlanStatus.DimintaUbah) to WorkPlanReviewResult.ChangesRequested
    if (!detail.weightsValid) return plan to WorkPlanReviewResult.InvalidWeights
    return plan.copy(status = WorkPlanStatus.Disetujui) to WorkPlanReviewResult.Approved
}

/**
 * PENYIMPANAN LOKAL SEMENTARA rencana pengerjaan (DATA DUMMY, tidak tersimpan di server).
 * Rencana project kopi sudah disetujui karena project-nya Sedang Dikerjakan. Belum ada negosiasi
 * dummy yang punya rencana menunggu persetujuan; [submitFromFreelancer] menerima usulan bila sisi
 * freelancer nanti terhubung.
 */
object UmkmWorkPlanStore {
    private val items = mutableStateListOf<UmkmWorkPlan>().apply { addAll(dummyPlans()) }

    fun plan(id: String): UmkmWorkPlan? = items.firstOrNull { it.id == id }

    fun planForProject(projectId: String): UmkmWorkPlan? = items.firstOrNull { it.projectId == projectId }

    fun planForConversation(conversationId: String): UmkmWorkPlan? =
        items.firstOrNull { it.conversationId == conversationId }

    /** Menggabungkan rencana dengan data project/percakapan agar siap ditampilkan. */
    fun detail(id: String): WorkPlanDetail? {
        val plan = plan(id) ?: return null
        plan.projectId?.let { projectId ->
            val project = UmkmProjectStore.project(projectId) ?: return null
            val start = project.startedUtcMillis?.let(::utcMillisToDate)
            val deadline = project.deadlineUtcMillis?.let(::utcMillisToDate)
            return WorkPlanDetail(
                plan = plan,
                projectTitle = project.title,
                freelancerName = project.freelancer?.name ?: "-",
                milestones = project.milestones,
                deadline = deadline,
                durationDays = if (start != null && deadline != null) ChronoUnit.DAYS.between(start, deadline) else null
            )
        }
        val conversation = plan.conversationId?.let { UmkmConversationStore.conversation(it) } ?: return null
        return WorkPlanDetail(
            plan = plan,
            projectTitle = conversation.projectTitle,
            freelancerName = conversation.freelancerName,
            milestones = plan.proposedMilestones,
            deadline = plan.proposedDeadline,
            durationDays = if (plan.proposedStart != null && plan.proposedDeadline != null) {
                ChronoUnit.DAYS.between(plan.proposedStart, plan.proposedDeadline)
            } else null
        )
    }

    /**
     * Menjalankan aturan [applyWorkPlanReview]. Persetujuan rencana dari negosiasi juga
     * mengunci kesepakatan final, sehingga negosiasi masuk Menunggu Pembayaran.
     */
    fun review(id: String, approve: Boolean): WorkPlanReviewResult {
        val detail = detail(id) ?: return WorkPlanReviewResult.NotAllowed
        // Rencana dari negosiasi hanya bisa disetujui bila nilai project sudah disepakati.
        val negotiation = detail.plan.conversationId?.let { UmkmNegotiationStore.negotiation(it) }
        if (approve && detail.plan.conversationId != null && negotiation?.agreedValue == null &&
            detail.plan.status == WorkPlanStatus.MenungguPersetujuan
        ) {
            return WorkPlanReviewResult.IncompleteAgreement
        }
        val (updated, result) = applyWorkPlanReview(detail, approve)
        val index = items.indexOfFirst { it.id == id }
        items[index] = updated
        if (result == WorkPlanReviewResult.Approved) {
            updated.conversationId?.let { UmkmNegotiationStore.lockFinalAgreement(it) }
        }
        return result
    }

    /** Usulan rencana dari freelancer (belum terhubung ke modul freelancer). */
    fun submitFromFreelancer(plan: UmkmWorkPlan) {
        items.removeAll { it.id == plan.id }
        items.add(plan.copy(status = WorkPlanStatus.MenungguPersetujuan))
    }

    /** Mengembalikan data dummy awal (dipakai saat pengujian). */
    fun reset() {
        items.clear()
        items.addAll(dummyPlans())
    }

    private fun dummyPlans() = listOf(
        UmkmWorkPlan(id = "plan-kopi", projectId = "dummy-project-kopi", status = WorkPlanStatus.Disetujui)
    )
}
