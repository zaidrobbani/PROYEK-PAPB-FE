package com.example.proyek_papb_fe.feature.freelancer.workplan.container

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.domain.model.MilestoneDraft
import com.example.proyek_papb_fe.core.domain.model.ProjectStatusCode
import com.example.proyek_papb_fe.core.domain.model.WorkflowActionCode
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.components.ErrorContent
import com.example.proyek_papb_fe.core.ui.components.LoadingContent
import com.example.proyek_papb_fe.core.util.formatShortDate
import com.example.proyek_papb_fe.feature.freelancer.workplan.component.MaxMilestones
import com.example.proyek_papb_fe.feature.freelancer.workplan.component.MilestoneInput
import com.example.proyek_papb_fe.feature.freelancer.workplan.component.UmkmSimulationEntry
import com.example.proyek_papb_fe.feature.freelancer.workplan.component.WorkPlanHeader
import com.example.proyek_papb_fe.feature.freelancer.workplan.component.WorkPlanScreen
import com.example.proyek_papb_fe.feature.freelancer.workplan.component.validate
import com.example.proyek_papb_fe.feature.freelancer.workplan.viewmodel.WorkPlanViewModel

private const val ScreenTitle = "Rencana Pengerjaan"

private val MilestoneListSaver: Saver<List<MilestoneInput>, Any> = listSaver(
    save = { list -> list.flatMap { listOf(it.id, it.name, it.weight) } },
    restore = { flat ->
        flat.chunked(3).map { (id, name, weight) ->
            MilestoneInput(id = id as Long, name = name as String, weight = weight as String)
        }
    }
)

private fun MilestoneInput.toDraft() = MilestoneDraft(name = name.trim(), description = "", weightPercent = weightValue)

/** [onOpenUmkmSimulation] tidak dipakai lagi: aksi status (mulai kerja, tandai milestone) dijalankan langsung ke BE. */
@Composable
fun WorkPlanContainer(
    projectId: String,
    onBack: () -> Unit,
    onOpenUmkmSimulation: () -> Unit = {},
    viewModel: WorkPlanViewModel = viewModel(
        factory = WorkPlanViewModel.factory(projectId, AppContainer.workflowRepository)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onMessageShown()
        }
    }

    val detail = uiState.detail
    val plan = uiState.plan
    if (detail == null || plan == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailTopBar(title = ScreenTitle, onBack = onBack)
            if (uiState.isLoading) LoadingContent() else ErrorContent(uiState.errorMessage.orEmpty(), onRetry = viewModel::load)
        }
        return
    }
    if (detail.project.status == ProjectStatusCode.DIBATALKAN) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailTopBar(title = ScreenTitle, onBack = onBack)
            ErrorContent("Proyek ini sudah dibatalkan sehingga rencana pengerjaan tidak dapat disusun.", onRetry = viewModel::load)
        }
        return
    }

    var milestones by rememberSaveable(projectId, uiState.planVersion, stateSaver = MilestoneListSaver) {
        val initial = plan.items.mapIndexed { index, m ->
            MilestoneInput(id = index.toLong() + 1, name = m.name, weight = m.weightPercent.toInt().takeIf { it > 0 }?.toString().orEmpty())
        }.ifEmpty { listOf(MilestoneInput(id = 1, name = "", weight = "")) }
        mutableStateOf(initial)
    }
    var nextId by rememberSaveable(projectId, uiState.planVersion) { mutableLongStateOf(milestones.size.toLong() + 1) }
    var showErrors by rememberSaveable(projectId) { mutableStateOf(false) }
    var focusMilestoneId by remember { mutableStateOf<Long?>(null) }

    val agreement = detail.agreement
    val clientName = detail.umkm.subtitle ?: detail.umkm.name
    val header = WorkPlanHeader(
        projectTitle = detail.project.title,
        clientAndDeadline = "$clientName, Deadline " +
            (agreement?.agreedDeadlineMillis?.let(::formatShortDate) ?: agreement?.agreedDeadline.orEmpty()) +
            " (${agreement?.durationDays ?: 0} hari)"
    )
    val nextMilestone = plan.items.firstOrNull { !it.isDone }
    val statusEntry: Pair<UmkmSimulationEntry, () -> Unit>? = when {
        detail.can(WorkflowActionCode.START_WORK) -> UmkmSimulationEntry(
            message = "$clientName sudah membayar. Dana ditahan TEMUKARYA dan proyek siap dikerjakan.",
            actionLabel = "Mulai Pengerjaan"
        ) to viewModel::startWork
        detail.can(WorkflowActionCode.COMPLETE_MILESTONE) && nextMilestone != null -> UmkmSimulationEntry(
            message = "Proyek berjalan. Tandai milestone setelah selesai dikerjakan; progres dihitung otomatis.",
            actionLabel = "Tandai \"${nextMilestone.name}\" Selesai"
        ) to viewModel::completeNextMilestone
        detail.project.status == ProjectStatusCode.MENUNGGU_PEMBAYARAN -> UmkmSimulationEntry(
            message = "Rencana sudah disepakati. Menunggu $clientName membayar lewat QRIS.",
            actionLabel = "Muat Ulang Status"
        ) to viewModel::load
        agreement?.approvedByMe == true -> UmkmSimulationEntry(
            message = "Rencana menunggu persetujuan $clientName.",
            actionLabel = "Muat Ulang Status"
        ) to viewModel::load
        agreement?.changeRequestNote != null -> UmkmSimulationEntry(
            message = "$clientName meminta perubahan: ${agreement.changeRequestNote}",
            actionLabel = "Muat Ulang Status"
        ) to viewModel::load
        else -> null
    }

    fun update(id: Long, transform: (MilestoneInput) -> MilestoneInput) {
        milestones = milestones.map { if (it.id == id) transform(it) else it }
    }

    WorkPlanScreen(
        header = header,
        milestones = milestones,
        isSubmitted = !uiState.isEditable,
        umkmEntry = statusEntry?.first,
        onOpenUmkmSimulation = { if (!uiState.isSaving) statusEntry?.second?.invoke() },
        showErrors = showErrors,
        focusMilestoneId = focusMilestoneId,
        onBack = onBack,
        onNameChange = { id, name -> update(id) { it.copy(name = name) } },
        onWeightChange = { id, weight -> update(id) { it.copy(weight = weight) } },
        onDeleteMilestone = { id ->
            if (milestones.size > 1) milestones = milestones.filterNot { it.id == id }
        },
        onAddMilestone = {
            if (milestones.size < MaxMilestones) {
                val id = nextId++
                milestones = milestones + MilestoneInput(id = id, name = "", weight = "")
                focusMilestoneId = id
            }
        },
        onFocusConsumed = { focusMilestoneId = null },
        onSaveDraftClick = {
            val filled = milestones.filter { it.name.isNotBlank() }
            if (filled.isEmpty()) Toast.makeText(context, "Isi minimal satu milestone", Toast.LENGTH_SHORT).show()
            else viewModel.saveDraft(filled.map { it.toDraft() })
        },
        onSubmitClick = {
            val message = milestones.validate().blockingMessage
            if (message != null) {
                showErrors = true
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            } else {
                showErrors = false
                viewModel.submitToUmkm(milestones.map { it.toDraft() })
            }
        }
    )
}
