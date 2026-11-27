package com.example.proyek_papb_fe.feature.freelancer.projectdetail.component

import com.example.proyek_papb_fe.core.ui.components.RefreshableContent
import kotlinx.coroutines.Job
import com.example.proyek_papb_fe.core.ui.components.chat.formatFileSize
import com.example.proyek_papb_fe.core.domain.model.ProjectFile
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.material3.Icon
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.data.model.FreelancerProject
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography

private val ScreenHorizontalPadding = 16.903.dp
private val SectionSpacing = 12.677.dp

@Composable
fun ProjectDetailScreen(
    project: FreelancerProject,
    onBack: () -> Unit,
    onChatClick: () -> Unit,
    onSubmitOfferClick: () -> Unit,
    modifier: Modifier = Modifier,
    onRefresh: (() -> Job)? = null,
    attachments: List<ProjectFile> = emptyList(),
    openingAttachmentId: String? = null,
    onAttachmentClick: (ProjectFile) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TemuKaryaColors.BgCanvas)
    ) {
        DetailTopBar(title = "Detail Proyek", onBack = onBack)

        RefreshableContent(onRefresh = onRefresh, modifier = Modifier.fillMaxWidth().weight(1f)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = ScreenHorizontalPadding,
                        end = ScreenHorizontalPadding,
                        top = 3.61.dp + SectionSpacing,
                        bottom = 16.903.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(SectionSpacing)
            ) {
                ProjectDetailHeader(
                    title = project.title,
                    publishedAgo = project.publishedAgo,
                    match = project.match
                )

                ClientProfileCard(project = project)

                KeyMetricsRow(
                    budget = project.budgetCompact,
                    deadline = project.deadlineCompact
                )

                DetailSectionCard(
                    iconRes = R.drawable.ic_description,
                    title = "Deskripsi Proyek & Kebutuhan",
                    titleStyle = TemuKaryaTypography.SectionTitle,
                    contentSpacing = 8.451.dp
                ) {
                    Text(
                        text = project.description,
                        style = TemuKaryaTypography.BodyLarge,
                        color = TemuKaryaColors.TextPrimary
                    )
                }

                if (project.skills.isNotEmpty()) {
                    DetailSectionCard(
                        iconRes = R.drawable.ic_skill,
                        title = "Keahlian yang Dibutuhkan",
                        titleStyle = TemuKaryaTypography.SubsectionTitle,
                        contentSpacing = 4.226.dp
                    ) {
                        SkillChips(skills = project.skills)
                    }
                }

                if (attachments.isNotEmpty()) {
                    DetailSectionCard(
                        iconRes = R.drawable.ic_document,
                        title = "Lampiran Pendukung",
                        titleStyle = TemuKaryaTypography.SubsectionTitle,
                        contentSpacing = 8.451.dp
                    ) {
                        attachments.forEach { file ->
                            AttachmentRow(
                                file = file,
                                isOpening = file.id == openingAttachmentId,
                                onClick = { onAttachmentClick(file) }
                            )
                        }
                    }
                }
            }
        }

        ProjectDetailActionBar(
            onChatClick = onChatClick,
            onSubmitOfferClick = onSubmitOfferClick
        )
    }
}

@Composable
private fun AttachmentRow(file: ProjectFile, isOpening: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(TemuKaryaColors.SurfaceSand)
            .clickable(enabled = !isOpening, role = Role.Button, onClickLabel = "Buka ${file.fileName}", onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painterResource(R.drawable.ic_document), contentDescription = null, tint = TemuKaryaColors.ActionPrimary, modifier = Modifier.size(22.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(file.fileName, style = TemuKaryaTypography.BodyLarge, color = TemuKaryaColors.TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                if (isOpening) "Membuka…" else listOfNotNull(formatFileSize(file.sizeBytes), "Ketuk untuk membuka").joinToString(" · "),
                style = TemuKaryaTypography.BodyRegular,
                color = TemuKaryaColors.TextPlaceholder
            )
        }
    }
}
