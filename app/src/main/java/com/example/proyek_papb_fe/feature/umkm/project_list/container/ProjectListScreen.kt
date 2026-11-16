package com.example.proyek_papb_fe.feature.umkm.project_list.container

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.umkm.component.shared.UmkmBottomNav
import com.example.proyek_papb_fe.feature.umkm.component.shared.UmkmTab
import com.example.proyek_papb_fe.feature.umkm.component.shared.UmkmTopBar
import com.example.proyek_papb_fe.feature.umkm.data.ProjectStatus
import com.example.proyek_papb_fe.feature.umkm.data.UmkmProject
import com.example.proyek_papb_fe.feature.umkm.data.UmkmProjectStore
import com.example.proyek_papb_fe.feature.umkm.project_list.component.CreateProjectFab
import com.example.proyek_papb_fe.feature.umkm.project_list.component.ProjectAttentionBanner
import com.example.proyek_papb_fe.feature.umkm.project_list.component.ProjectCard
import com.example.proyek_papb_fe.feature.umkm.project_list.component.ProjectCardActions
import com.example.proyek_papb_fe.feature.umkm.project_list.component.ProjectStatusTabs
import com.example.proyek_papb_fe.feature.umkm.project_list.component.ProjectTab

private val PageGutter = 16.903.dp

/** Jarak FAB dari Bottom Nav dan ruang bawah agar kartu terakhir bisa digulir melewati FAB. */
private val FabBottomGap = 35.893.dp
private val ListBottomPadding = 29.415.dp + 50.708.dp + 8.451.dp

/**
 * Project Saya (Blueprint TK-F-U-03). Data dari [UmkmProjectStore] (lokal, dummy);
 * hapus hanya untuk project yang belum memiliki kesepakatan (TK-F-U-05).
 */
@Composable
fun ProjectListScreen(
    onNotificationsClick: () -> Unit,
    onCreateProject: () -> Unit,
    onEditProject: (projectId: String) -> Unit,
    onViewProgress: (projectId: String) -> Unit,
    onReviewResult: (projectId: String) -> Unit,
    onViewApplications: (projectId: String) -> Unit,
    onFinalFiles: (projectId: String) -> Unit,
    onOrderAgain: (projectId: String) -> Unit,
    onTabSelected: (UmkmTab) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableStateOf(ProjectTab.Semua) }
    var pendingDeleteId by rememberSaveable { mutableStateOf<String?>(null) }

    val projects = UmkmProjectStore.projects
    val counts = ProjectTab.entries.associateWith { tab ->
        if (tab.status == null) projects.size else projects.count { it.status == tab.status }
    }
    val visible = projects.filter { selectedTab.status == null || it.status == selectedTab.status }
    val reviewCount = projects.count { it.status == ProjectStatus.MenungguReview }
    // Penawaran perlu keputusan selama belum ada freelancer terpilih.
    val offerProjects = projects.filter {
        it.status == ProjectStatus.MenungguKesepakatan && UmkmProjectStore.needsAction(it)
    }
    val actionCount = projects.count { UmkmProjectStore.needsAction(it) }

    val actions = ProjectCardActions(
        onViewProgress = { onViewProgress(it.id) },
        onReviewResult = { onReviewResult(it.id) },
        onEdit = { onEditProject(it.id) },
        onDelete = { pendingDeleteId = it.id },
        onViewApplications = { onViewApplications(it.id) },
        onFinalFiles = { onFinalFiles(it.id) },
        onOrderAgain = { onOrderAgain(it.id) }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        UmkmTopBar(onNotificationsClick = onNotificationsClick, title = "Project Saya")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 3.61.dp, bottom = ListBottomPadding),
                verticalArrangement = Arrangement.spacedBy(12.677.dp)
            ) {
                if (actionCount > 0) {
                    item(key = "attention") {
                        ProjectAttentionBanner(
                            reviewCount = reviewCount,
                            offerCount = offerProjects.sumOf { it.offersCount },
                            actionCount = actionCount,
                            modifier = Modifier.padding(horizontal = PageGutter)
                        )
                    }
                }
                item(key = "tabs") {
                    ProjectStatusTabs(
                        selected = selectedTab,
                        counts = counts,
                        onSelect = { selectedTab = it },
                        horizontalPadding = PageGutter
                    )
                }
                if (visible.isEmpty()) {
                    item(key = "empty") { EmptyProjects(selectedTab) }
                } else {
                    items(visible, key = { it.id }) { project ->
                        ProjectCard(
                            project = project,
                            actions = actions,
                            modifier = Modifier.padding(horizontal = PageGutter)
                        )
                    }
                }
            }
            CreateProjectFab(
                onClick = onCreateProject,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = PageGutter, bottom = FabBottomGap)
            )
        }
        UmkmBottomNav(
            selectedTab = UmkmTab.Project,
            onTabSelected = { tab -> if (tab != UmkmTab.Project) onTabSelected(tab) }
        )
    }

    projects.firstOrNull { it.id == pendingDeleteId }?.let { target ->
        DeleteProjectDialog(
            project = target,
            onConfirm = {
                UmkmProjectStore.delete(target.id)
                pendingDeleteId = null
            },
            onDismiss = { pendingDeleteId = null }
        )
    }
}

/** Figma tidak punya keadaan kosong; teks sederhana ini tambahan. */
@Composable
private fun EmptyProjects(tab: ProjectTab) {
    Text(
        text = if (tab == ProjectTab.Semua) {
            "Belum ada project. Ketuk Buat Project Baru untuk memulai."
        } else {
            "Belum ada project dengan status ${tab.label}."
        },
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

/** Konfirmasi sebelum menghapus; Figma tidak mendesain dialog ini. */
@Composable
private fun DeleteProjectDialog(
    project: UmkmProject,
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
                text = "Hapus project?",
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
                text = "\"${project.title}\" akan dihapus dari daftar. Ini hanya simulasi di perangkat ini.",
                color = colors.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium.withFrameMetrics(
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.79.sp,
                    lineHeight = 21.128.sp
                )
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Hapus", color = colors.error) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = colors.onSurfaceVariant) }
        }
    )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun ProjectListScreenPreview() {
    PROYEKPAPBFETheme {
        ProjectListScreen(
            onNotificationsClick = {},
            onCreateProject = {},
            onEditProject = {},
            onViewProgress = {},
            onReviewResult = {},
            onViewApplications = {},
            onFinalFiles = {},
            onOrderAgain = {},
            onTabSelected = {}
        )
    }
}
