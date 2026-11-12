package com.example.proyek_papb_fe.feature.freelancer.projectdetail.component

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

/**
 * Figma: Detail Project Freelancer (17:4195).
 * Konten di-scroll; top bar dan action bar bawah tetap di tempat.
 */
@Composable
fun ProjectDetailScreen(
    project: FreelancerProject,
    onBack: () -> Unit,
    onChatClick: () -> Unit,
    onSubmitOfferClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TemuKaryaColors.BgCanvas)
    ) {
        DetailTopBar(title = "Detail Project", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                // Figma: Main pt 67.61 dikurangi app bar 64, lalu tiap section pt 12.677.
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
        }

        ProjectDetailActionBar(
            onChatClick = onChatClick,
            onSubmitOfferClick = onSubmitOfferClick
        )
    }
}
