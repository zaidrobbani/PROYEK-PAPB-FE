package com.example.proyek_papb_fe.core.data

import androidx.compose.runtime.mutableStateMapOf
import com.example.proyek_papb_fe.core.data.model.DraftFile
import com.example.proyek_papb_fe.core.data.model.SubmittedDraft
import com.example.proyek_papb_fe.core.data.model.WorkDraft

/**
 * Draf pekerjaan per ruang chat (= satu project), di memori selama sesi aplikasi
 * (sementara, sebelum ada backend). Berbasis snapshot state agar modal Unggah Draf
 * ikut ter-update saat file dipilih / dihapus.
 *
 * - Draf yang sedang disusun (belum dikirim) tersimpan per percakapan, jadi "Batal / Simpan
 *   Draf" atau menutup modal tidak menghapus file & catatan yang sudah dipilih.
 * - [markSent] hanya menaikkan nomor versi dan mengosongkan draf berjalan. Pengiriman ke
 *   klien saat ini berupa pesan lampiran lokal di ruang chat; unggah file ke server belum ada.
 */
object WorkDraftStore {

    private val pending = mutableStateMapOf<Int, WorkDraft>()
    private val sentCount = mutableStateMapOf<Int, Int>()
    private val submitted = mutableStateMapOf<Int, List<SubmittedDraft>>()

    /** Draf yang sedang disusun untuk percakapan ini (versi berikutnya bila belum ada). */
    fun current(conversationId: Int): WorkDraft =
        pending[conversationId] ?: WorkDraft(version = (sentCount[conversationId] ?: 0) + 1)

    /** Tambah file; file yang sama (uri sama) tidak digandakan. */
    fun addFiles(conversationId: Int, files: List<DraftFile>) {
        val draft = current(conversationId)
        val known = draft.files.map { it.uri }.toSet()
        pending[conversationId] = draft.copy(files = draft.files + files.filterNot { it.uri in known })
    }

    fun removeFile(conversationId: Int, uri: String) {
        val draft = current(conversationId)
        pending[conversationId] = draft.copy(files = draft.files.filterNot { it.uri == uri })
    }

    fun updateNote(conversationId: Int, note: String) {
        pending[conversationId] = current(conversationId).copy(note = note)
    }

    /**
     * Draf dikirim ke klien: draf yang dikirim disimpan (untuk Review Hasil UMKM), lalu versi
     * berikutnya mulai dari draf kosong.
     */
    fun markSent(conversationId: Int, sentAtMillis: Long = System.currentTimeMillis()) {
        val draft = current(conversationId)
        submitted[conversationId] = submitted[conversationId].orEmpty() +
            SubmittedDraft(draft.version, draft.files, draft.note.trim(), sentAtMillis)
        sentCount[conversationId] = draft.version
        pending.remove(conversationId)
    }

    /** Draf terakhir yang dikirim ke klien di percakapan ini; null = belum pernah mengirim. */
    fun latestSubmitted(conversationId: Int): SubmittedDraft? = submitted[conversationId]?.lastOrNull()
}
