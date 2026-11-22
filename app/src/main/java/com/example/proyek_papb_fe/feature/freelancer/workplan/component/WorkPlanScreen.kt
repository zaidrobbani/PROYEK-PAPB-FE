package com.example.proyek_papb_fe.feature.freelancer.workplan.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

data class WorkPlanHeader(
    val projectTitle: String,
    val clientAndDeadline: String
)

@Composable
fun WorkPlanScreen(
    header: WorkPlanHeader,
    milestones: List<MilestoneInput>,
    isSubmitted: Boolean,
    showErrors: Boolean,
    focusMilestoneId: Long?,
    onBack: () -> Unit,
    onNameChange: (id: Long, name: String) -> Unit,
    onWeightChange: (id: Long, weight: String) -> Unit,
    onDeleteMilestone: (id: Long) -> Unit,
    onAddMilestone: () -> Unit,
    onFocusConsumed: () -> Unit,
    onSaveDraftClick: () -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier,
    umkmEntry: UmkmSimulationEntry? = null,
    onOpenUmkmSimulation: () -> Unit = {}
) {
    val validation = milestones.validate()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TemuKaryaColors.BgCanvas)
    ) {
        DetailTopBar(title = "Rencana Pengerjaan", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 17.dp, end = 17.dp, top = 20.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WorkPlanHeaderCard(
                    projectTitle = header.projectTitle,
                    clientAndDeadline = header.clientAndDeadline,
                    isSubmitted = isSubmitted
                )
                WorkPlanInfoCard()
                umkmEntry?.let { UmkmSimulationCard(it, onOpenUmkmSimulation) }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Milestone proyek",
                        style = WorkPlanTypography.SectionTitle,
                        color = TemuKaryaColors.TextHeading,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${milestones.size} tahap",
                        style = WorkPlanTypography.Count,
                        color = TemuKaryaColors.TextPlaceholder
                    )
                }

                milestones.forEachIndexed { index, milestone ->
                    key(milestone.id) {
                        MilestoneRow(
                            index = index,
                            milestone = milestone,
                            showErrors = showErrors,
                            isLocked = isSubmitted,
                            canDelete = milestones.size > 1,
                            requestFocus = milestone.id == focusMilestoneId,
                            onNameChange = { onNameChange(milestone.id, it) },
                            onWeightChange = { onWeightChange(milestone.id, it) },
                            onDelete = { onDeleteMilestone(milestone.id) },
                            onFocusConsumed = onFocusConsumed
                        )
                    }
                }

                if (!isSubmitted) {
                    AddMilestoneButton(
                        enabled = milestones.size < MaxMilestones,
                        onClick = onAddMilestone
                    )
                }

                WeightSummaryCard(
                    milestones = milestones,
                    validation = validation,
                    isSubmitted = isSubmitted
                )

                Text(
                    text = "Progres proyek nanti dihitung otomatis dari bobot milestone yang sudah ditandai selesai.",
                    style = WorkPlanTypography.Footnote,
                    color = TemuKaryaColors.TextPlaceholder
                )
            }

            WorkPlanActionBar(
                isSubmitted = isSubmitted,
                onSaveDraftClick = onSaveDraftClick,
                onSubmitClick = onSubmitClick
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun WorkPlanScreenPreview() {
    WorkPlanScreen(
        header = WorkPlanHeader(
            projectTitle = "Desain Rebranding Logo & Kemasan Pouch Kopi 250gr",
            clientAndDeadline = "Kedai Kopi Senja, Deadline 7 Nov 2026 (14 hari)"
        ),
        milestones = listOf(
            MilestoneInput(1, "Riset & Moodboard Brand", "20"),
            MilestoneInput(2, "Konsep Logo (2 Alternatif)", "30"),
            MilestoneInput(3, "Desain Kemasan Pouch 250gr", "35"),
            MilestoneInput(4, "Mockup & File Siap Cetak", "15")
        ),
        isSubmitted = false,
        showErrors = false,
        focusMilestoneId = null,
        onBack = {},
        onNameChange = { _, _ -> },
        onWeightChange = { _, _ -> },
        onDeleteMilestone = {},
        onAddMilestone = {},
        onFocusConsumed = {},
        onSaveDraftClick = {},
        onSubmitClick = {}
    )
}
