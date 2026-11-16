package com.example.proyek_papb_fe.feature.freelancer.workplan.container

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyek_papb_fe.core.data.FreelancerProjectDummyData
import com.example.proyek_papb_fe.core.data.SubmittedOfferStore
import com.example.proyek_papb_fe.core.data.WorkPlanDummyData
import com.example.proyek_papb_fe.core.data.WorkPlanStore
import com.example.proyek_papb_fe.core.data.model.SubmittedOffer
import com.example.proyek_papb_fe.core.data.model.WorkPlanMilestone
import com.example.proyek_papb_fe.core.data.model.WorkPlanStatus
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.components.FeaturePlaceholder
import com.example.proyek_papb_fe.feature.freelancer.workplan.component.MaxMilestones
import com.example.proyek_papb_fe.feature.freelancer.workplan.component.MilestoneInput
import com.example.proyek_papb_fe.feature.freelancer.workplan.component.WorkPlanHeader
import com.example.proyek_papb_fe.feature.freelancer.workplan.component.WorkPlanScreen
import com.example.proyek_papb_fe.feature.freelancer.workplan.component.UmkmSimulationEntry
import com.example.proyek_papb_fe.core.data.PaymentStore
import com.example.proyek_papb_fe.core.data.model.PaymentStatus
import com.example.proyek_papb_fe.core.data.model.isLocked
import com.example.proyek_papb_fe.feature.freelancer.workplan.component.validate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val ScreenTitle = "Rencana Pengerjaan"

private val IndonesianLocale = Locale.forLanguageTag("id-ID")

/** Tanggal kirim penawaran + durasi yang disepakati, mis. "7 Nov 2026". */
private fun deadlineFrom(offer: SubmittedOffer): String {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = offer.submittedAtMillis
        add(Calendar.DAY_OF_YEAR, offer.durationDays)
    }
    return SimpleDateFormat("d MMM yyyy", IndonesianLocale).format(calendar.time)
}

/** Simpan list milestone ke Bundle sebagai triplet datar (id, nama, bobot). */
private val MilestoneListSaver: Saver<List<MilestoneInput>, Any> = listSaver(
    save = { list -> list.flatMap { listOf(it.id, it.name, it.weight) } },
    restore = { flat ->
        flat.chunked(3).map { (id, name, weight) ->
            MilestoneInput(id = id as Long, name = name as String, weight = weight as String)
        }
    }
)

private fun WorkPlanMilestone.toInput(id: Long) =
    MilestoneInput(id = id, name = name, weight = weightPercent?.toString().orEmpty())

private fun MilestoneInput.toMilestone() =
    WorkPlanMilestone(name = name.trim(), weightPercent = weight.toIntOrNull())

/**
 * Container "Rencana Pengerjaan": lookup project + penawaran (yang sudah disetujui),
 * memuat draf / rencana terajukan milik projectId ini saja, mengelola state form
 * milestone, validasi total bobot 100%, simpan draf, dan ajukan ke UMKM.
 */
@Composable
fun WorkPlanContainer(
    projectId: Int,
    onBack: () -> Unit,
    onOpenUmkmSimulation: () -> Unit = {}
) {
    val project = remember(projectId) { FreelancerProjectDummyData.getById(projectId) }
    val offer = SubmittedOfferStore.get(projectId)

    val blocked: Pair<String, String>? = when {
        project == null || offer == null ->
            "Rencana tidak ditemukan" to "Belum ada penawaran untuk proyek dengan id $projectId."
        offer.isCancelled ->
            "Proyek dibatalkan" to "Proyek ini sudah dibatalkan sehingga rencana pengerjaan tidak dapat disusun."
        !offer.isApproved ->
            "Penawaran belum disetujui" to "Rencana pengerjaan bisa disusun setelah UMKM menyetujui penawaran."
        else -> null
    }
    if (blocked != null || project == null || offer == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailTopBar(title = ScreenTitle, onBack = onBack)
            FeaturePlaceholder(
                title = blocked?.first.orEmpty(),
                description = blocked?.second.orEmpty()
            )
        }
        return
    }

    val context = LocalContext.current
    // Dibaca langsung (snapshot state) agar status terajukan selalu sesuai isi store.
    val storedPlan = WorkPlanStore.get(projectId)
    // Diajukan atau sudah disetujui UMKM: rencana terkunci (tidak bisa diedit lagi).
    val isSubmitted = storedPlan?.status?.isLocked == true

    // Kesepakatan final tidak lagi disimulasikan otomatis: UMKM menyetujui rencana di
    // "Tinjau Rencana Pengerjaan" (Figma 270:663), membayar (17:1267), dan setelah PAID project
    // resmi berjalan lewat SubmittedOfferStore.markAgreed (lihat PaymentStore). Karena belum ada
    // aplikasi UMKM, sisi UMKM dibuka dari kartu simulasi di bawah.
    val payment = PaymentStore.forProject(projectId)
    val umkmEntry = when {
        storedPlan?.status == WorkPlanStatus.SUBMITTED && !offer.isAgreed -> UmkmSimulationEntry(
            message = "Rencana menunggu persetujuan ${project.clientName}. Buka layar UMKM untuk menyetujui dan membayar.",
            actionLabel = "Tinjau sebagai UMKM"
        )
        storedPlan?.status == WorkPlanStatus.APPROVED && payment?.status != PaymentStatus.PAID -> UmkmSimulationEntry(
            message = "${project.clientName} sudah menyetujui rencana. Proyek menunggu pembayaran UMKM.",
            actionLabel = "Lanjut ke Pembayaran"
        )
        payment?.status == PaymentStatus.PAID -> UmkmSimulationEntry(
            message = "${project.clientName} sudah membayar. Dana ditahan TEMUKARYA dan proyek resmi berjalan.",
            actionLabel = "Lihat Bukti Pembayaran"
        )
        else -> null
    }

    // Nilai awal: draf/rencana tersimpan project ini, atau usulan awal. Di-key dengan
    // projectId agar isian project lain tidak pernah terbawa.
    var milestones by rememberSaveable(projectId, stateSaver = MilestoneListSaver) {
        val initial = storedPlan?.milestones ?: WorkPlanDummyData.defaultMilestonesFor(project)
        mutableStateOf(initial.mapIndexed { index, m -> m.toInput(id = index.toLong() + 1) })
    }
    var nextId by rememberSaveable(projectId) { mutableLongStateOf(milestones.size.toLong() + 1) }
    var showErrors by rememberSaveable(projectId) { mutableStateOf(false) }
    // Tidak perlu disimpan: hanya untuk memfokuskan baris yang baru ditambahkan.
    var focusMilestoneId by remember { mutableStateOf<Long?>(null) }

    val header = remember(project, offer) {
        WorkPlanHeader(
            projectTitle = project.title,
            clientAndDeadline = "${project.clientName}, Deadline ${deadlineFrom(offer)} (${offer.durationDays} hari)"
        )
    }

    fun update(id: Long, transform: (MilestoneInput) -> MilestoneInput) {
        milestones = milestones.map { if (it.id == id) transform(it) else it }
    }

    WorkPlanScreen(
        header = header,
        milestones = milestones,
        isSubmitted = isSubmitted,
        umkmEntry = umkmEntry,
        onOpenUmkmSimulation = onOpenUmkmSimulation,
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
            WorkPlanStore.saveDraft(projectId, milestones.map { it.toMilestone() })
            Toast.makeText(context, "Draf rencana pengerjaan disimpan", Toast.LENGTH_SHORT).show()
        },
        onSubmitClick = {
            val message = milestones.validate().blockingMessage
            if (message != null) {
                showErrors = true
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            } else {
                showErrors = false
                WorkPlanStore.submit(projectId, milestones.map { it.toMilestone() })
                Toast.makeText(context, "Rencana pengerjaan diajukan ke UMKM", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun WorkPlanContainerPreview() {
    SubmittedOfferStore.submit(
        SubmittedOffer(
            projectId = 101,
            offerAmount = 1_500_000,
            platformFee = 75_000,
            netAmount = 1_425_000,
            durationDays = 14,
            message = "Contoh pesan proposal.",
            submittedAtMillis = System.currentTimeMillis()
        )
    )
    SubmittedOfferStore.markApproved(101)
    WorkPlanContainer(projectId = 101, onBack = {})
}
