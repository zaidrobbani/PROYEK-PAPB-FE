package com.example.proyek_papb_fe.feature.chat.viewmodel

import com.example.proyek_papb_fe.core.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Pesan yang dipilih (tekan lama untuk mulai, ketuk untuk menambah/membuang), ala WhatsApp. */
class MessageSelection {
    private val _selected = MutableStateFlow<Set<String>>(emptySet())
    val selected: StateFlow<Set<String>> = _selected.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    fun toggle(messageId: String) = _selected.update { if (messageId in it) it - messageId else it + messageId }

    fun clear() {
        _selected.value = emptySet()
    }

    /** Buang id yang sudah tidak tampil (mis. dihapus dari perangkat lain). */
    fun retain(visibleIds: Set<String>) = _selected.update { it intersect visibleIds }

    /**
     * Ruang chat menggabungkan beberapa percakapan (project), jadi penghapusan dikirim per percakapan.
     * Mengembalikan waktu hapus per percakapan yang berhasil; percakapan yang gagal melempar setelah
     * yang lain selesai, agar pesan yang sudah terhapus tetap tercermin di layar.
     */
    suspend fun delete(
        repository: ChatRepository,
        conversationOf: Map<String, String>,
        forEveryone: Boolean,
        onDeleted: (conversationId: String, messageIds: List<String>, deletedAtMillis: Long) -> Unit
    ) {
        val ids = _selected.value
        if (ids.isEmpty() || _isDeleting.value) return
        _isDeleting.value = true
        var failure: Exception? = null
        try {
            ids.groupBy { conversationOf[it] }.forEach { (conversationId, messageIds) ->
                if (conversationId == null) return@forEach
                try {
                    val deletedAt = repository.deleteMessages(conversationId, messageIds, forEveryone)
                    onDeleted(conversationId, messageIds, deletedAt)
                    _selected.update { it - messageIds.toSet() }
                } catch (e: kotlinx.coroutines.CancellationException) {
                    throw e
                } catch (e: Exception) {
                    failure = e
                }
            }
        } finally {
            _isDeleting.value = false
        }
        failure?.let { throw it }
    }
}
