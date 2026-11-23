package com.example.proyek_papb_fe.feature.freelancer.offerform.container

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.ui.components.ErrorContent
import com.example.proyek_papb_fe.core.ui.components.LoadingContent
import com.example.proyek_papb_fe.feature.freelancer.offerform.viewmodel.OfferFormViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyek_papb_fe.core.data.OfferDraftStore
import com.example.proyek_papb_fe.core.data.model.OfferDraft
import com.example.proyek_papb_fe.core.data.model.SubmittedOffer
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.components.FeaturePlaceholder
import com.example.proyek_papb_fe.feature.freelancer.offerform.component.DurationOption
import com.example.proyek_papb_fe.feature.freelancer.offerform.component.OfferFormScreen
import com.example.proyek_papb_fe.feature.freelancer.offerform.component.netAmount
import com.example.proyek_papb_fe.feature.freelancer.offerform.component.parseRupiah
import com.example.proyek_papb_fe.feature.freelancer.offerform.component.platformFee
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val IndonesianLocale = Locale.forLanguageTag("id-ID")

private fun dueDateAfter(days: Int): String {
    val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, days) }
    return SimpleDateFormat("d MMMM yyyy", IndonesianLocale).format(calendar.time)
}

@Composable
fun OfferFormContainer(
    projectId: String,
    onBack: () -> Unit,
    onOfferSubmitted: (projectId: String) -> Unit,
    viewModel: OfferFormViewModel = viewModel(
        factory = OfferFormViewModel.factory(projectId, AppContainer.projectRepository, AppContainer.masterDataRepository)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val project = uiState.project

    LaunchedEffect(uiState.submitted) {
        if (uiState.submitted) {
            viewModel.onSubmittedHandled()
            OfferDraftStore.clear(projectId)
            onOfferSubmitted(projectId)
        }
    }

    if (project == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailTopBar(title = "Ajukan Penawaran", onBack = onBack)
            if (uiState.isLoading) LoadingContent() else ErrorContent(uiState.errorMessage.orEmpty(), onRetry = viewModel::load)
        }
        return
    }

    val context = LocalContext.current
    val offerAmount = uiState.budget
    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onMessageShown()
        }
    }

    val savedDraft = remember(projectId) { OfferDraftStore.get(projectId) }
    var selectedDuration by rememberSaveable(projectId) {
        mutableStateOf(
            savedDraft?.let { DurationOption.fromDays(it.selectedDuration) } ?: DurationOption.Default
        )
    }
    var message by rememberSaveable(projectId) { mutableStateOf(savedDraft?.message.orEmpty()) }

    val draftDueDate = remember(selectedDuration) { dueDateAfter(selectedDuration.days) }

    OfferFormScreen(
        project = project,
        offerAmount = offerAmount,
        selectedDuration = selectedDuration,
        onDurationSelect = { selectedDuration = it },
        draftDueDate = draftDueDate,
        message = message,
        onMessageChange = { message = it },
        onSnippetClick = { snippet ->
            message = if (message.isBlank()) snippet.text else "${message.trimEnd()} ${snippet.text}"
        },
        onSaveDraftClick = {
            OfferDraftStore.save(
                projectId,
                OfferDraft(selectedDuration = selectedDuration.days, message = message)
            )
            Toast.makeText(context, "Draf penawaran disimpan", Toast.LENGTH_SHORT).show()
        },
        onSubmitClick = {
            if (message.isBlank()) {
                Toast.makeText(context, "Tulis pesan proposal terlebih dahulu", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.submit(price = offerAmount, durationDays = selectedDuration.days, message = message.trim())
            }
        },
        onBack = onBack
    )
}
