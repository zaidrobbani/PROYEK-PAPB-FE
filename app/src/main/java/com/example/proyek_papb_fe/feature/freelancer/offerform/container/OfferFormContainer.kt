package com.example.proyek_papb_fe.feature.freelancer.offerform.container

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyek_papb_fe.core.data.FreelancerProjectDummyData
import com.example.proyek_papb_fe.core.data.OfferDraftStore
import com.example.proyek_papb_fe.core.data.SubmittedOfferStore
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

/** Tanggal hari ini + [days], mis. "7 November 2026". */
private fun dueDateAfter(days: Int): String {
    val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, days) }
    return SimpleDateFormat("d MMMM yyyy", IndonesianLocale).format(calendar.time)
}

/**
 * Container "Ajukan Penawaran": lookup project, muat/simpan draf per projectId,
 * state durasi + pesan, dan validasi kirim.
 *
 * Nilai penawaran tidak bisa diubah di sini: selalu = pagu klien project.
 * Negosiasi harga dilakukan nanti lewat chat.
 */
@Composable
fun OfferFormContainer(
    projectId: Int,
    onBack: () -> Unit,
    /** Dipanggil setelah validasi lolos, penawaran tersimpan, dan draf dihapus. */
    onOfferSubmitted: (projectId: Int) -> Unit
) {
    val project = remember(projectId) { FreelancerProjectDummyData.getById(projectId) }

    if (project == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailTopBar(title = "Ajukan Penawaran", onBack = onBack)
            FeaturePlaceholder(
                title = "Proyek tidak ditemukan",
                description = "Proyek dengan id $projectId tidak tersedia."
            )
        }
        return
    }

    val context = LocalContext.current
    val offerAmount = remember(project.budget) { parseRupiah(project.budget) }

    // Nilai awal diambil dari draf project ini (jika ada). Di-key dengan projectId
    // agar state project lain tidak pernah terbawa.
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
            // Durasi selalu terisi (bawaan 14 hari), jadi yang divalidasi adalah pesan.
            if (message.isBlank()) {
                Toast.makeText(context, "Tulis pesan proposal terlebih dahulu", Toast.LENGTH_SHORT).show()
            } else {
                // Simpan penawaran terkirim dulu agar Status Pengajuan punya datanya,
                // baru hapus draf dan navigasi.
                SubmittedOfferStore.submit(
                    SubmittedOffer(
                        projectId = projectId,
                        offerAmount = offerAmount,
                        platformFee = platformFee(offerAmount),
                        netAmount = netAmount(offerAmount),
                        durationDays = selectedDuration.days,
                        message = message.trim(),
                        submittedAtMillis = System.currentTimeMillis()
                    )
                )
                OfferDraftStore.clear(projectId)
                onOfferSubmitted(projectId)
            }
        },
        onBack = onBack
    )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun OfferFormContainerPreview() {
    OfferFormContainer(projectId = 101, onBack = {}, onOfferSubmitted = {})
}
