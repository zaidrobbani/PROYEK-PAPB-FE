package com.example.proyek_papb_fe.feature.freelancer.submission.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.data.model.ProjectFilter
import com.example.proyek_papb_fe.core.data.model.ProjectStatus
import com.example.proyek_papb_fe.core.ui.components.RootTopBar
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography

internal val SubmissionListHorizontalPadding = 16.903.dp

/**
 * Label chip kartu di daftar "Project". Status & filternya memakai [ProjectStatus] bersama
 * (sama dengan tab Chat); [PLAN_SUBMITTED] hanya varian chip dari status Negosiasi.
 * - [UNDER_REVIEW]: Menunggu Kesepakatan (UMKM belum menyetujui penawaran).
 * - [NEGOTIATION]: Negosiasi.
 * - [PLAN_SUBMITTED]: Negosiasi, dan rencana pengerjaan sudah diajukan ke UMKM.
 * - [IN_PROGRESS]: Project Berjalan (rencana pengerjaan disetujui UMKM, sedang dikerjakan).
 * - [COMPLETED]: Selesai.
 * - [CANCELLED]: Dibatalkan.
 */
enum class ProposalStage(val label: String, val status: ProjectStatus) {
    UNDER_REVIEW(ProjectStatus.MENUNGGU_KESEPAKATAN.label, ProjectStatus.MENUNGGU_KESEPAKATAN),
    NEGOTIATION(ProjectStatus.NEGOSIASI.label, ProjectStatus.NEGOSIASI),
    PLAN_SUBMITTED("Rencana diajukan", ProjectStatus.NEGOSIASI),
    IN_PROGRESS(ProjectStatus.PROJECT_BERJALAN.label, ProjectStatus.PROJECT_BERJALAN),
    COMPLETED(ProjectStatus.SELESAI.label, ProjectStatus.SELESAI),
    CANCELLED(ProjectStatus.DIBATALKAN.label, ProjectStatus.DIBATALKAN)
}

/** Data tampilan satu kartu penawaran (sudah diformat). */
data class ProposalListItem(
    val projectId: Int,
    val stage: ProposalStage,
    /** Tanggal kirim absolut, mis. "24 Okt 2026". */
    val submittedDate: String,
    val projectTitle: String,
    /** Nama UMKM + kota, mis. "Kedai Kopi Senja · Bandung". */
    val clientLine: String,
    val offerAmount: String,
    val duration: String
)

data class ProposalFilterOption(
    val filter: ProjectFilter,
    val count: Int
)

/**
 * Tab "Project": project yang diajukan, dinegosiasikan, dikerjakan, atau dibatalkan,
 * satu kartu per project.
 * Top bar dan filter tetap, hanya daftar yang di-scroll.
 */
@Composable
fun SubmissionListScreen(
    filterOptions: List<ProposalFilterOption>,
    selectedFilter: ProjectFilter,
    onFilterSelect: (ProjectFilter) -> Unit,
    proposals: List<ProposalListItem>,
    onProposalClick: (projectId: Int) -> Unit,
    onFindProjectsClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasUnreadNotification: Boolean = true,
    onNotificationClick: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    // Kembali ke atas setiap kali filter diganti. Dijalankan setelah daftar baru
    // dikomposisi; bila dijalankan sebelumnya, LazyColumn menahan posisi pada key kartu
    // teratas daftar lama (mis. Negosiasi -> Semua tampil mulai dari kartu tengah).
    // Filter terakhir disimpan agar kembali dari Status tidak memaksa scroll ke atas.
    var lastScrolledFilter by rememberSaveable { mutableStateOf(selectedFilter) }
    LaunchedEffect(selectedFilter) {
        if (lastScrolledFilter != selectedFilter) {
            lastScrolledFilter = selectedFilter
            listState.scrollToItem(0)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TemuKaryaColors.BgCanvas)
    ) {
        RootTopBar(
            title = "Proyek",
            hasUnreadNotification = hasUnreadNotification,
            onNotificationClick = onNotificationClick
        )

        // Figma: Main pt 8 + Filter Pills pt 12 / pb 4. 4dp atas & bawah sudah ada di
        // contentPadding ProposalFilterRow (ruang bayangan), sisanya di sini.
        ProposalFilterRow(
            options = filterOptions,
            selectedFilter = selectedFilter,
            onFilterSelect = onFilterSelect,
            modifier = Modifier.padding(top = 16.dp)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(
                start = SubmissionListHorizontalPadding,
                end = SubmissionListHorizontalPadding,
                top = 8.dp,
                bottom = 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (proposals.isEmpty()) {
                item(key = "empty") {
                    EmptyProposals(
                        selectedFilter = selectedFilter,
                        onFindProjectsClick = onFindProjectsClick
                    )
                }
            }
            items(items = proposals, key = { it.projectId }) { item ->
                ProposalCard(
                    item = item,
                    onClick = { onProposalClick(item.projectId) }
                )
            }
        }
    }
}

@Composable
private fun EmptyProposals(
    selectedFilter: ProjectFilter,
    onFindProjectsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_nav_project),
            contentDescription = null,
            tint = SubmissionStatusColors.StepUpcomingIcon,
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = "Belum ada proyek",
            style = SubmissionStatusTypography.SectionTitle,
            color = TemuKaryaColors.TextHeading,
            textAlign = TextAlign.Center
        )
        Text(
            text = if (selectedFilter == ProjectFilter.SEMUA) {
                "Ajukan penawaran ke proyek UMKM, lalu pantau statusnya di sini."
            } else {
                "Belum ada proyek dengan status \"${selectedFilter.label}\"."
            },
            style = TemuKaryaTypography.BodyRegular,
            color = TemuKaryaColors.TextBody,
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier
                .padding(top = 12.dp)
                .height(48.dp)
                .clip(StatusButtonShape)
                .background(TemuKaryaColors.ActionPrimary)
                .clickable(onClick = onFindProjectsClick)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search_rounded),
                contentDescription = null,
                tint = TemuKaryaColors.OnAction,
                modifier = Modifier.size(19.dp)
            )
            Text(
                text = "Cari Proyek",
                style = SubmissionStatusTypography.Button,
                color = TemuKaryaColors.OnAction,
                maxLines = 1
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun SubmissionListScreenEmptyPreview() {
    SubmissionListScreen(
        filterOptions = ProjectFilter.entries.map { ProposalFilterOption(it, 0) },
        selectedFilter = ProjectFilter.SEMUA,
        onFilterSelect = {},
        proposals = emptyList(),
        onProposalClick = {},
        onFindProjectsClick = {}
    )
}
