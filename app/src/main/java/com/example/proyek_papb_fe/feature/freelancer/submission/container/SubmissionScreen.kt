package com.example.proyek_papb_fe.feature.freelancer.submission.container

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyek_papb_fe.core.data.FreelancerProjectDummyData
import com.example.proyek_papb_fe.core.data.SubmittedOfferStore
import com.example.proyek_papb_fe.core.data.WorkPlanStore
import com.example.proyek_papb_fe.core.data.model.FreelancerProject
import com.example.proyek_papb_fe.core.data.model.SubmittedOffer
import com.example.proyek_papb_fe.core.data.model.isLocked
import com.example.proyek_papb_fe.core.data.model.ProjectFilter
import com.example.proyek_papb_fe.core.data.model.ProjectStatus
import com.example.proyek_papb_fe.feature.freelancer.submission.component.ProposalFilterOption
import com.example.proyek_papb_fe.feature.freelancer.submission.component.ProposalListItem
import com.example.proyek_papb_fe.feature.freelancer.submission.component.ProposalStage
import com.example.proyek_papb_fe.feature.freelancer.submission.component.SubmissionListScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val IndonesianLocale = Locale.forLanguageTag("id-ID")

/** 1500000 → "Rp 1.500.000". */
private fun formatRupiah(amount: Long): String =
    "Rp " + amount.toString().reversed().chunked(3).joinToString(".").reversed()

/** Tanggal kirim dalam WIB, mis. "24 Okt 2026". */
private fun formatSubmittedDate(millis: Long): String =
    SimpleDateFormat("d MMM yyyy", IndonesianLocale).apply {
        timeZone = TimeZone.getTimeZone("Asia/Jakarta")
    }.format(Date(millis))

/** Chip kartu dari status bersama [SubmittedOffer.status]; tidak menyimpan status baru. */
private fun stageOf(offer: SubmittedOffer, isPlanSubmitted: Boolean): ProposalStage = when (offer.status) {
    ProjectStatus.MENUNGGU_KESEPAKATAN -> ProposalStage.UNDER_REVIEW
    ProjectStatus.NEGOSIASI -> if (isPlanSubmitted) ProposalStage.PLAN_SUBMITTED else ProposalStage.NEGOTIATION
    ProjectStatus.PROJECT_BERJALAN -> ProposalStage.IN_PROGRESS
    ProjectStatus.SELESAI -> ProposalStage.COMPLETED
    ProjectStatus.DIBATALKAN -> ProposalStage.CANCELLED
}

private fun SubmittedOffer.toListItem(project: FreelancerProject, isPlanSubmitted: Boolean) =
    ProposalListItem(
        projectId = projectId,
        stage = stageOf(this, isPlanSubmitted),
        submittedDate = formatSubmittedDate(submittedAtMillis),
        projectTitle = project.title,
        clientLine = listOf(project.clientName, project.clientLocation)
            .filter { it.isNotBlank() }
            .joinToString(" · "),
        offerAmount = formatRupiah(offerAmount),
        duration = "$durationDays hari"
    )

/**
 * Container tab "Project": menggabungkan setiap penawaran terkirim dengan project-nya
 * (berdasarkan projectId), menurunkan tahap kartu, lalu memfilter dan mengurutkan
 * (terbaru dulu). Persetujuan UMKM tetap disimulasikan oleh layar Status Pengajuan,
 * bukan di sini.
 */
@Composable
fun SubmissionScreen(
    onProposalClick: (projectId: Int) -> Unit,
    onFindProjectsClick: () -> Unit
) {
    var selectedFilter by rememberSaveable { mutableStateOf(ProjectFilter.SEMUA) }

    // Dibaca langsung (snapshot state): daftar ikut ter-update saat penawaran dikirim,
    // disetujui, disepakati, atau dibatalkan, maupun saat rencana pengerjaan diajukan.
    val allItems = SubmittedOfferStore.getAll()
        .sortedByDescending { it.submittedAtMillis }
        .mapNotNull { offer ->
            val project = FreelancerProjectDummyData.getById(offer.projectId) ?: return@mapNotNull null
            // Rencana diajukan atau sudah disetujui UMKM (menunggu pembayaran) = tahap rencana terajukan.
            val isPlanSubmitted = WorkPlanStore.get(offer.projectId)?.status?.isLocked == true
            offer.toListItem(project, isPlanSubmitted)
        }

    val filterOptions = ProjectFilter.entries.map { filter ->
        ProposalFilterOption(
            filter = filter,
            count = allItems.count { filter.matches(it.stage.status) }
        )
    }
    // "Semua" = daftar tanpa filter (tidak menyalin data, urutan terbaru dulu tetap).
    val proposals = allItems.filter { selectedFilter.matches(it.stage.status) }

    SubmissionListScreen(
        filterOptions = filterOptions,
        selectedFilter = selectedFilter,
        onFilterSelect = { selectedFilter = it },
        proposals = proposals,
        onProposalClick = onProposalClick,
        onFindProjectsClick = onFindProjectsClick
    )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun SubmissionScreenPreview() {
    SubmissionScreen(onProposalClick = {}, onFindProjectsClick = {})
}
