package com.example.proyek_papb_fe.feature.freelancer.offerform.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.core.data.model.FreelancerProject
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

private val ScreenHorizontalPadding = 16.903.dp
private val SectionSpacing = 12.677.dp

private val ContentBottomPadding = 118.318.dp + 12.677.dp

@Composable
fun OfferFormScreen(
    project: FreelancerProject,
    offerAmount: Long,
    selectedDuration: DurationOption,
    onDurationSelect: (DurationOption) -> Unit,
    draftDueDate: String,
    message: String,
    onMessageChange: (String) -> Unit,
    onSnippetClick: (ProposalSnippet) -> Unit,
    onSaveDraftClick: () -> Unit,
    onSubmitClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    snippets: List<ProposalSnippet> = DefaultProposalSnippets
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TemuKaryaColors.BgCanvas)
    ) {
        DetailTopBar(title = "Ajukan Penawaran", onBack = onBack)

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = ScreenHorizontalPadding,
                        end = ScreenHorizontalPadding,
                        top = 3.61.dp,
                        bottom = ContentBottomPadding
                    ),
                verticalArrangement = Arrangement.spacedBy(SectionSpacing)
            ) {
                ProjectSummaryCard(
                    title = project.title,
                    daysLeft = project.applicationDaysLeft,
                    clientName = project.clientName,
                    clientInitials = project.clientInitials,
                    clientRating = project.clientRating,
                    clientLocation = project.clientLocation,
                    budget = project.budget
                )
                OfferAmountCard(amount = offerAmount)
                DurationSelectorCard(
                    selected = selectedDuration,
                    onSelect = onDurationSelect,
                    draftDueDate = draftDueDate
                )
                ProposalMessageCard(
                    clientName = project.clientName,
                    message = message,
                    onMessageChange = onMessageChange,
                    snippets = snippets,
                    onSnippetClick = onSnippetClick
                )
            }

            OfferFormActionBar(
                onSaveDraftClick = onSaveDraftClick,
                onSubmitClick = onSubmitClick,
                isSubmitEnabled = true,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
