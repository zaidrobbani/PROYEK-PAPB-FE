package com.example.proyek_papb_fe.feature.umkm.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import java.time.LocalDateTime

/** Satu poin uraian revisi (Figma: judul poin, uraian, dan area). Hanya uraian yang wajib. */
data class RevisionPoint(val title: String = "", val detail: String = "", val area: String = "") {
    val isEmpty: Boolean get() = title.isBlank() && detail.isBlank() && area.isBlank()
}

/** Isi formulir revisi yang disimpan lewat "Simpan Draf" agar bisa dilanjutkan nanti. */
data class RevisionForm(val aspects: List<String> = emptyList(), val points: List<RevisionPoint> = listOf(RevisionPoint()))

/** Permintaan revisi yang sudah dikirim untuk satu draf kiriman freelancer. */
data class RevisionRequest(
    val id: String,
    val conversationId: String,
    val draftMessageId: String,
    val aspects: List<String>,
    val points: List<RevisionPoint>,
    val sentAt: LocalDateTime
)

/** Hasil "Kirim Permintaan Revisi". */
enum class RevisionSubmitResult { Sent, MissingDetail, NotAllowed }

/**
 * Blueprint (Meminta revisi): catatan perbaikan wajib diisi. Di sini berarti minimal satu poin
 * punya uraian; poin yang kosong seluruhnya diabaikan. Tidak ada batas karakter maupun jumlah revisi.
 */
fun revisionPointsToSend(points: List<RevisionPoint>): List<RevisionPoint>? {
    val filled = points.filterNot { it.isEmpty }
    return if (filled.isNotEmpty() && filled.all { it.detail.isNotBlank() }) filled.map {
        RevisionPoint(it.title.trim(), it.detail.trim(), it.area.trim())
    } else null
}

/**
 * PENYIMPANAN LOKAL SEMENTARA permintaan revisi draf (SIMULASI, tidak tersimpan di server dan
 * freelancer belum benar-benar menerima). Status project tidak diubah: Blueprint hanya mengubah
 * status ke Revisi dari Menunggu Review, sedangkan draf di chat dikirim saat project berjalan.
 */
object UmkmRevisionStore {
    private val requests = mutableStateListOf<RevisionRequest>()
    private val savedForms = mutableStateMapOf<String, RevisionForm>()

    fun requestsFor(draftMessageId: String): List<RevisionRequest> = requests.filter { it.draftMessageId == draftMessageId }

    fun savedForm(draftMessageId: String): RevisionForm? = savedForms[draftMessageId]

    fun saveForm(draftMessageId: String, form: RevisionForm) {
        savedForms[draftMessageId] = form
    }

    /** Draf boleh ditinjau selama project-nya belum Selesai/Dibatalkan. */
    fun canRequestRevision(conversationId: String, draftMessageId: String): Boolean {
        val conversation = UmkmConversationStore.conversation(conversationId) ?: return false
        val message = UmkmConversationStore.messages(conversationId).firstOrNull { it.id == draftMessageId } ?: return false
        if (message.fromUmkm || message.attachment?.draftLabel == null) return false
        val status = conversation.projectId?.let { UmkmProjectStore.project(it)?.status }
        return status != ProjectStatus.Selesai && status != ProjectStatus.Dibatalkan
    }

    fun submit(
        conversationId: String,
        draftMessageId: String,
        aspects: List<String>,
        points: List<RevisionPoint>,
        now: LocalDateTime = LocalDateTime.now()
    ): RevisionSubmitResult {
        if (!canRequestRevision(conversationId, draftMessageId)) return RevisionSubmitResult.NotAllowed
        val toSend = revisionPointsToSend(points) ?: return RevisionSubmitResult.MissingDetail
        requests.add(RevisionRequest("revisi-${requests.size + 1}", conversationId, draftMessageId, aspects, toSend, now))
        savedForms.remove(draftMessageId)
        val draft = UmkmConversationStore.messages(conversationId).first { it.id == draftMessageId }.attachment
        UmkmConversationStore.sendMessage(conversationId, summary(draft?.draftLabel ?: "draf", aspects, toSend), now)
        return RevisionSubmitResult.Sent
    }

    /** Ringkasan yang muncul sebagai pesan UMKM di Ruang Chat. */
    private fun summary(draftLabel: String, aspects: List<String>, points: List<RevisionPoint>): String = buildString {
        append("Permintaan revisi untuk $draftLabel:")
        points.forEachIndexed { index, point ->
            append("\n${index + 1}. ")
            if (point.title.isNotBlank()) append("${point.title}: ")
            append(point.detail)
            if (point.area.isNotBlank()) append(" (Area: ${point.area})")
        }
        if (aspects.isNotEmpty()) append("\nAspek: ${aspects.joinToString(", ")}")
    }

    /** Mengosongkan data (dipakai saat pengujian). */
    fun reset() {
        requests.clear()
        savedForms.clear()
    }
}
