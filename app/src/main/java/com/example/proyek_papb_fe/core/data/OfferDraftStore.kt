package com.example.proyek_papb_fe.core.data

import com.example.proyek_papb_fe.core.data.model.OfferDraft

object OfferDraftStore {
    private val drafts = mutableMapOf<String, OfferDraft>()

    fun get(projectId: String): OfferDraft? = drafts[projectId]

    fun save(projectId: String, draft: OfferDraft) {
        drafts[projectId] = draft
    }

    fun clear(projectId: String) {
        drafts.remove(projectId)
    }
}
