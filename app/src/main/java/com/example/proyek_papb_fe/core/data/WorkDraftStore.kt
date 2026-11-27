package com.example.proyek_papb_fe.core.data

import androidx.compose.runtime.mutableStateMapOf
import com.example.proyek_papb_fe.core.data.model.DraftFile
import com.example.proyek_papb_fe.core.data.model.SubmittedDraft
import com.example.proyek_papb_fe.core.data.model.WorkDraft

object WorkDraftStore {
    private val pending = mutableStateMapOf<String, WorkDraft>()
    private val sentCount = mutableStateMapOf<String, Int>()
    private val submitted = mutableStateMapOf<String, List<SubmittedDraft>>()

    fun current(conversationId: String): WorkDraft =
        pending[conversationId] ?: WorkDraft(version = (sentCount[conversationId] ?: 0) + 1)

    fun addFiles(conversationId: String, files: List<DraftFile>) {
        val draft = current(conversationId)
        val known = draft.files.map { it.uri }.toSet()
        pending[conversationId] = draft.copy(files = draft.files + files.filterNot { it.uri in known })
    }

    fun removeFile(conversationId: String, uri: String) {
        val draft = current(conversationId)
        pending[conversationId] = draft.copy(files = draft.files.filterNot { it.uri == uri })
    }

    fun updateNote(conversationId: String, note: String) {
        pending[conversationId] = current(conversationId).copy(note = note)
    }

    fun markSent(conversationId: String, sentAtMillis: Long = System.currentTimeMillis()) {
        val draft = current(conversationId)
        submitted[conversationId] = submitted[conversationId].orEmpty() +
            SubmittedDraft(draft.version, draft.files, draft.note.trim(), sentAtMillis)
        sentCount[conversationId] = draft.version
        pending.remove(conversationId)
    }

    fun latestSubmitted(conversationId: String): SubmittedDraft? = submitted[conversationId]?.lastOrNull()
}
