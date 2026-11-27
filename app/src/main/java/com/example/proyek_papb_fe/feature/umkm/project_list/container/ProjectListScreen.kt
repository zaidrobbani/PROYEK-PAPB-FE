package com.example.proyek_papb_fe.feature.umkm.project_list.container

import com.example.proyek_papb_fe.core.ui.components.RefreshableContent
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
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.ui.components.InlineErrorContent
import com.example.proyek_papb_fe.core.ui.components.LoadingContent
import com.example.proyek_papb_fe.feature.umkm.project_list.viewmodel.ProjectListViewModel
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

private val FabBottomGap = 35.893.dp
private val ListBottomPadding = 29.415.dp + 50.708.dp + 8.451.dp

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
    modifier: Modifier = Modifier,
    onOpenAgreement: (projectId: String) -> Unit = {},
    onPay: (projectId: String) -> Unit = {},
    viewModel: ProjectListViewModel = viewModel(
        factory = ProjectListViewModel.factory(
            AppContainer.workflowRepository,
            AppContainer.projectRepository,
            AppContainer.masterDataRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(Unit) { viewModel.load() }
    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onMessageShown()
        }
    }

    var selectedTab by rememberSaveable { mutableStateOf(ProjectTab.Semua) }
    var pendingDeleteId by rememberSaveable { mutableStateOf<String?>(null) }

    val projects = uiState.projects
    val counts = ProjectTab.entries.associateWith { tab -> projects.count { tab.matches(it.status) } }
    val visible = projects.filter { selectedTab.matches(it.status) }
    val reviewCount = projects.count { it.status == ProjectStatus.MenungguReview }
    val offerProjects = projects.filter { it.status == ProjectStatus.Terbuka && it.id in uiState.actionProjectIds }
    val actionCount = uiState.actionProjectIds.size

    val actions = ProjectCardActions(
        onViewProgress = { onViewProgress(it.id) },
        onReviewResult = { onReviewResult(it.id) },
        onEdit = { onEditProject(it.id) },
        onDelete = { pendingDeleteId = it.id },
        onViewApplications = { onViewApplications(it.id) },
        onFinalFiles = { onFinalFiles(it.id) },
        onOrderAgain = { onOrderAgain(it.id) },
        onOpenAgreement = { onOpenAgreement(it.id) },
        onPay = { onPay(it.id) }
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
            RefreshableContent(onRefresh = viewModel::refresh, modifier = Modifier.fillMaxSize()) {
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
                    if (uiState.isLoading && projects.isEmpty()) {
                        item(key = "loading") { LoadingContent(Modifier.padding(top = 48.dp)) }
                    } else if (uiState.errorMessage != null && projects.isEmpty()) {
                        item(key = "error") { InlineErrorContent(uiState.errorMessage.orEmpty(), onRetry = viewModel::load) }
                    } else if (visible.isEmpty()) {
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
                viewModel.delete(target.id)
                pendingDeleteId = null
            },
            onDismiss = { pendingDeleteId = null }
        )
    }
}

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
                text = "\"${project.title}\" akan dihapus permanen beserta lampirannya.",
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
