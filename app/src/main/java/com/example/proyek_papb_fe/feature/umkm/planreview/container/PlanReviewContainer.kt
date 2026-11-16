package com.example.proyek_papb_fe.feature.umkm.planreview.container

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.proyek_papb_fe.core.data.FreelancerProfileStore
import com.example.proyek_papb_fe.core.data.FreelancerProjectDummyData
import com.example.proyek_papb_fe.core.data.PaymentStore
import com.example.proyek_papb_fe.core.data.SubmittedOfferStore
import com.example.proyek_papb_fe.core.data.WorkPlanStore
import com.example.proyek_papb_fe.core.data.model.WorkPlanStatus
import com.example.proyek_papb_fe.feature.umkm.planreview.component.PlanReviewScreen
import com.example.proyek_papb_fe.feature.umkm.planreview.component.PlanReviewUiState
import com.example.proyek_papb_fe.feature.umkm.planreview.component.ReviewMilestoneUi
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Container Tinjau Rencana Pengerjaan (UMKM, Figma 270:663) untuk [projectId]. Hanya untuk
 * rencana yang sedang diajukan freelancer (WorkPlanStatus.SUBMITTED). "Setujui Rencana" =
 * kedua pihak sepakat: rencana APPROVED, pembayaran dibuat (READY), lalu [onApproved] membuka
 * Pembayaran. Rencana yang sudah disetujui langsung diteruskan ke Pembayaran.
 */
@Composable
fun PlanReviewContainer(
    projectId: Int,
    onBack: () -> Unit,
    onApproved: (projectId: Int) -> Unit
) {
    val context = LocalContext.current
    val project = FreelancerProjectDummyData.getById(projectId)
    val offer = SubmittedOfferStore.get(projectId)
    val plan = WorkPlanStore.get(projectId)

    if (project == null || offer == null || plan == null || offer.isCancelled || offer.isAgreed || plan.status == WorkPlanStatus.DRAFT) {
        LaunchedEffect(projectId) { onBack() }
        return
    }
    if (plan.status == WorkPlanStatus.APPROVED) {
        LaunchedEffect(projectId) { onApproved(projectId) }
        return
    }

    val deadline = SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("id-ID")).format(
        Calendar.getInstance().apply { timeInMillis = offer.submittedAtMillis; add(Calendar.DAY_OF_YEAR, offer.durationDays) }.time
    )
    PlanReviewScreen(
        state = PlanReviewUiState(
            projectTitle = project.title,
            submittedBy = "Diajukan ${FreelancerProfileStore.profile.fullName}, deadline $deadline (${offer.durationDays} hari)",
            milestones = plan.milestones.map { ReviewMilestoneUi(it.name, it.weightPercent ?: 0) }
        ),
        onBack = onBack,
        onRequestChanges = {
            // Figma tidak mendefinisikan layar "Minta Ubah": rencana dikembalikan ke freelancer sebagai draf.
            // Cukup ubah state: cabang DRAFT di atas yang kembali (sekali) ke Rencana Pengerjaan.
            WorkPlanStore.requestChanges(projectId)
            Toast.makeText(context, "Permintaan ubah dikirim ke freelancer", Toast.LENGTH_SHORT).show()
        },
        onApprove = {
            // Cukup ubah state: cabang APPROVED di atas yang membuka Pembayaran (satu jalur navigasi,
            // tidak ada navigasi ganda saat tombol ditekan berulang).
            WorkPlanStore.approve(projectId)
            PaymentStore.createForApprovedPlan(projectId)
        }
    )
}
