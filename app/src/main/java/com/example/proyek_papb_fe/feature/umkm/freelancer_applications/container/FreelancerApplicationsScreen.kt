package com.example.proyek_papb_fe.feature.umkm.freelancer_applications.container

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthAppBar
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.data.ProjectApplication
import com.example.proyek_papb_fe.feature.umkm.data.SelectFreelancerResult
import com.example.proyek_papb_fe.feature.umkm.data.UmkmProjectStore
import com.example.proyek_papb_fe.feature.umkm.freelancer_applications.component.ApplicantCard
import com.example.proyek_papb_fe.feature.umkm.freelancer_applications.component.ApplicantSelection
import com.example.proyek_papb_fe.feature.umkm.freelancer_applications.component.ApplicationSort
import com.example.proyek_papb_fe.feature.umkm.freelancer_applications.component.ApplicationsListHeader
import com.example.proyek_papb_fe.feature.umkm.freelancer_applications.component.ApplicationsProjectSummary

private val PageGutter = 16.903.dp

/** Jarak antarbagian di Figma: gap 20 ditambah margin atas 16.903 pada tiap bagian. */
private val SectionSpacing = 20.dp + 16.903.dp

/**
 * Pengajuan Freelancer (Blueprint TK-F-U-11 dan aturan "Memilih freelancer" Fungsi 2).
 * Data dan perubahan status hanya simulasi lokal di [UmkmProjectStore].
 */
@Composable
fun FreelancerApplicationsScreen(
    projectId: String,
    onBack: () -> Unit,
    onViewProfile: (applicationId: String) -> Unit,
    onFreelancerSelected: (applicationId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var sort by rememberSaveable { mutableStateOf(ApplicationSort.PalingRelevan) }
    var pendingApproveId by rememberSaveable { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val project = UmkmProjectStore.project(projectId)
    val applications = UmkmProjectStore.applicationsFor(projectId).let { list ->
        when (sort) {
            ApplicationSort.PalingRelevan -> list.sortedByDescending { it.isTopRecommendation }
            ApplicationSort.HargaTerendah -> list.sortedBy { it.bid }
            ApplicationSort.WaktuTercepat -> list.sortedBy { it.workingDays }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AuthAppBar(title = "Pengajuan", onBack = onBack)
        if (project == null) {
            CenteredNote("Project tidak ditemukan.")
            return@Column
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(
                start = PageGutter,
                end = PageGutter,
                top = 3.61.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(SectionSpacing)
        ) {
            item(key = "summary") {
                ApplicationsProjectSummary(project = project, ownerLabel = UmkmProjectStore.OwnerLabel)
            }
            item(key = "header") {
                ApplicationsListHeader(
                    count = applications.size,
                    sort = sort,
                    onSortChange = { sort = it },
                    modifier = Modifier.padding(top = 4.226.dp)
                )
            }
            if (applications.isEmpty()) {
                item(key = "empty") { CenteredNote("Belum ada freelancer yang mengajukan diri.") }
            }
            items(applications, key = { it.id }) { application ->
                ApplicantCard(
                    application = application,
                    selection = when (project.selectedApplicationId) {
                        null -> ApplicantSelection.Open
                        application.id -> ApplicantSelection.Selected
                        else -> ApplicantSelection.Closed
                    },
                    onViewProfile = { onViewProfile(application.id) },
                    onApprove = { pendingApproveId = application.id }
                )
            }
        }
    }

    applications.firstOrNull { it.id == pendingApproveId }?.let { target ->
        ApproveDialog(
            application = target,
            onConfirm = {
                pendingApproveId = null
                val message = when (UmkmProjectStore.selectFreelancer(projectId, target.id)) {
                    SelectFreelancerResult.Selected -> {
                        onFreelancerSelected(target.id)
                        "${target.freelancerName} dipilih. Status project: Menunggu Kesepakatan (simulasi)."
                    }
                    SelectFreelancerResult.AlreadySelected -> "Project ini sudah memiliki freelancer terpilih."
                    SelectFreelancerResult.NotAllowed -> "Freelancer tidak dapat dipilih pada status project ini."
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            },
            onDismiss = { pendingApproveId = null }
        )
    }
}

@Composable
private fun CenteredNote(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PageGutter, vertical = 33.805.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium.withFrameMetrics(
            fontWeight = FontWeight.Normal,
            fontSize = 14.79.sp,
            lineHeight = 21.128.sp
        )
    )
}

/** Konfirmasi karena satu project hanya boleh punya satu freelancer terpilih; tidak ada di Figma. */
@Composable
internal fun ApproveDialog(
    application: ProjectApplication,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surfaceContainerLow,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = "Pilih ${application.freelancerName}?",
                color = colors.onTertiaryContainer,
                style = MaterialTheme.typography.titleMedium.withFrameMetrics(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.903.sp,
                    lineHeight = 23.241.sp
                )
            )
        },
        text = {
            Text(
                text = "Satu project hanya dikerjakan satu freelancer. Setelah dipilih, " +
                    "pengajuan lain tidak bisa dipilih dan project menunggu kesepakatan.",
                color = colors.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium.withFrameMetrics(
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.79.sp,
                    lineHeight = 21.128.sp
                )
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Pilih", color = colors.primary) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = colors.onSurfaceVariant) }
        }
    )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun FreelancerApplicationsScreenPreview() {
    PROYEKPAPBFETheme {
        FreelancerApplicationsScreen(
            projectId = "dummy-project-website",
            onBack = {},
            onViewProfile = {},
            onFreelancerSelected = {}
        )
    }
}
