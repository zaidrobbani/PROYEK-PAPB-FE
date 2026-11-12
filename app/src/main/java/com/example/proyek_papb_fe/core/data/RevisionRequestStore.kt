package com.example.proyek_papb_fe.core.data

import androidx.compose.runtime.mutableStateMapOf
import com.example.proyek_papb_fe.core.data.model.RevisionPoint
import com.example.proyek_papb_fe.core.data.model.RevisionRequest

/** Isian form revisi UMKM yang disimpan lewat "Simpan Draf" (belum dikirim). */
data class RevisionFormDraft(val aspects: Set<String>, val points: List<RevisionPoint>)

/**
 * Permintaan revisi UMKM per project di memori, diawali dari [RevisionRequestDummyData].
 * Satu sumber untuk layar Permintaan Revisi freelancer (Figma 17:5737) dan aksi
 * "Kirim Permintaan Revisi" UMKM (Figma 17:5518). Riwayat per project disimpan agar
 * siklus revisi berulang (draf baru → review lagi) tetap terhubung ke project yang sama.
 */
object RevisionRequestStore {

    private val requests = mutableStateMapOf<Int, List<RevisionRequest>>().apply {
        RevisionRequestDummyData.seed().forEach { put(it.projectId, listOf(it)) }
    }

    /** Permintaan revisi terbaru project ini; null = belum pernah diminta. */
    fun forProject(projectId: Int): RevisionRequest? = requests[projectId]?.lastOrNull()

    /** Jumlah permintaan revisi yang sudah dikirim untuk project ini. */
    fun countFor(projectId: Int): Int = requests[projectId]?.size ?: 0

    /**
     * Tambah permintaan revisi. Satu permintaan per versi draf: permintaan kedua untuk draf
     * yang sama diabaikan (mencegah kiriman ganda). Mengembalikan true bila tersimpan.
     */
    fun submit(request: RevisionRequest): Boolean {
        val existing = requests[request.projectId].orEmpty()
        if (existing.any { it.draftVersion == request.draftVersion }) return false
        requests[request.projectId] = existing + request
        formDrafts.remove(request.projectId)
        return true
    }

    private val formDrafts = mutableMapOf<Int, RevisionFormDraft>()

    /** Draf form revisi yang belum dikirim untuk project ini ("Simpan Draf", Figma 17:5518). */
    fun savedForm(projectId: Int): RevisionFormDraft? = formDrafts[projectId]

    fun saveForm(projectId: Int, draft: RevisionFormDraft) {
        formDrafts[projectId] = draft
    }
}
