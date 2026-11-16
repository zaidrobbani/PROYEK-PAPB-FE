package com.example.proyek_papb_fe.core.data

import com.example.proyek_papb_fe.core.data.model.OfferDraft

/**
 * Penyimpanan draf penawaran di memori, dipisah per projectId (sementara, sebelum
 * ada backend). Draf bertahan selama sesi aplikasi: tetap ada saat layar form
 * ditutup lalu dibuka lagi, dan hanya dihapus setelah penawaran berhasil dikirim.
 */
object OfferDraftStore {

    private val drafts = mutableMapOf<Int, OfferDraft>()

    fun get(projectId: Int): OfferDraft? = drafts[projectId]

    fun save(projectId: Int, draft: OfferDraft) {
        drafts[projectId] = draft
    }

    fun clear(projectId: Int) {
        drafts.remove(projectId)
    }
}
