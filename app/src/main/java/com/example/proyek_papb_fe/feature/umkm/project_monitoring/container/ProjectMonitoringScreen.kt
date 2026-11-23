package com.example.proyek_papb_fe.feature.umkm.project_monitoring.container

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.ui.components.ErrorContent
import com.example.proyek_papb_fe.core.ui.components.LoadingContent
import com.example.proyek_papb_fe.feature.umkm.project_monitoring.viewmodel.ProjectMonitoringViewModel
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import com.example.proyek_papb_fe.feature.umkm.data.ProjectStatus
import com.example.proyek_papb_fe.feature.umkm.data.UmkmConversationStore
import com.example.proyek_papb_fe.feature.umkm.data.UmkmProjectStore
import com.example.proyek_papb_fe.feature.umkm.data.evaluateProject
import com.example.proyek_papb_fe.feature.umkm.data.utcMillisToDate
import com.example.proyek_papb_fe.feature.umkm.project_monitoring.component.MilestoneList
import com.example.proyek_papb_fe.feature.umkm.project_monitoring.component.MilestoneSectionHeader
import com.example.proyek_papb_fe.feature.umkm.project_monitoring.component.MonitoringBottomBar
import com.example.proyek_papb_fe.feature.umkm.project_monitoring.component.MonitoringHeaderCard
import com.example.proyek_papb_fe.feature.umkm.project_monitoring.component.MonitoringInfoNote
import com.example.proyek_papb_fe.feature.umkm.project_monitoring.component.MonitoringProgressCard
import com.example.proyek_papb_fe.feature.umkm.project_monitoring.component.MonitoringRiskAlert
import com.example.proyek_papb_fe.feature.umkm.project_monitoring.component.MonitoringTimeline
import com.example.proyek_papb_fe.feature.umkm.project_monitoring.component.MonitoringUpdatedAt
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val UpdatedFormat = DateTimeFormatter.ofPattern("d MMM yyyy, HH.mm", Locale.forLanguageTag("id-ID"))

@Composable
fun ProjectMonitoringScreen(
    projectId: String,
    onBack: () -> Unit,
    onChatFreelancer: ((conversationId: String) -> Unit)?,
    onOpenWorkPlan: ((planId: String) -> Unit)?,
    modifier: Modifier = Modifier,
    viewModel: ProjectMonitoringViewModel = viewModel(
        factory = ProjectMonitoringViewModel.factory(
            projectId,
            AppContainer.workflowRepository,
            AppContainer.chatRepository,
            AppContainer.masterDataRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val project = uiState.project
    val evaluatedAt = remember(uiState.loadedAtMillis) { LocalDateTime.now() }
    val result = uiState.result

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onMessageShown()
        }
    }
    LaunchedEffect(uiState.chatConversationId) {
        uiState.chatConversationId?.let { conversationId ->
            viewModel.onChatOpened()
            onChatFreelancer?.invoke(conversationId)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AuthAppBar(title = "Progres Proyek", onBack = onBack)
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            if (project == null && uiState.isLoading) {
                LoadingContent()
                return@Box
            }
            if (project == null && uiState.errorMessage != null) {
                ErrorContent(uiState.errorMessage.orEmpty(), onRetry = viewModel::load)
                return@Box
            }
            if (project == null || result == null) {
                Text(
                    text = "Data pemantauan tersedia setelah freelancer memulai pengerjaan dengan rencana milestone lengkap.",
                    modifier = Modifier.align(Alignment.Center).padding(horizontal = 17.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium.withFrameMetrics(
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.7.sp,
                        lineHeight = 17.78.sp
                    )
                )
                return@Box
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 17.dp, end = 17.dp, top = 84.dp - 64.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(key = "header") {
                    MonitoringHeaderCard(
                        title = project.title,
                        status = project.status,
                        risk = result.risk,
                        freelancerName = project.freelancer?.name
                    )
                }
                item(key = "progress") { MonitoringProgressCard(result) }
                item(key = "risk") {
                    MonitoringRiskAlert(result, nextMilestone = project.milestones.firstOrNull { !it.isDone })
                }
                item(key = "timeline") { MonitoringTimeline(result) }
                item(key = "milestone-header") {
                    MilestoneSectionHeader(
                        agreedDate = project.agreedUtcMillis?.let(::utcMillisToDate),
                        onOpenPlan = onOpenWorkPlan?.let { open -> { open(project.id) } }
                    )
                }
                item(key = "milestones") {
                    MilestoneList(
                        project.milestones,
                        inProgress = project.status == ProjectStatus.SedangDikerjakan || project.status == ProjectStatus.Revisi
                    )
                }
                item(key = "info") { MonitoringInfoNote() }
                item(key = "updated") {
                    MonitoringUpdatedAt("Terakhir diperbarui ${evaluatedAt.format(UpdatedFormat)} WIB")
                }
            }
            MonitoringBottomBar(
                onChat = {
                    if (onChatFreelancer == null) {
                        Toast.makeText(context, "Chat belum tersedia di versi ini.", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.openChat()
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
