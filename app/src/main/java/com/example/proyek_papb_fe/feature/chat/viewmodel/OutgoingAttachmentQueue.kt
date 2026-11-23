package com.example.proyek_papb_fe.feature.chat.viewmodel

import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.ChatRepository
import com.example.proyek_papb_fe.core.data.source.PickedChatFile
import com.example.proyek_papb_fe.core.domain.model.Message
import com.example.proyek_papb_fe.core.domain.model.OutgoingAttachment
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

/**
 * Antrean lampiran keluar untuk satu ruang chat (dipakai VM freelancer & UMKM).
 * Lampiran langsung tampil di chat dengan status "mengirim"; diunggah berurutan agar urutannya terjaga.
 * Gagal → tetap tampil dengan tanda gagal, bisa dikirim ulang atau dibuang.
 */
class OutgoingAttachmentQueue(
    private val scope: CoroutineScope,
    /** Percakapan tujuan, dibaca saat lampiran dipilih (UMKM bisa memilih project tujuan). */
    private val conversationId: () -> String,
    private val chatRepository: ChatRepository,
    private val onSent: (Message) -> Unit
) {
    private val _items = MutableStateFlow<List<OutgoingAttachment>>(emptyList())
    val items: StateFlow<List<OutgoingAttachment>> = _items.asStateFlow()
    private val mutex = Mutex()
    private val targets = mutableMapOf<String, String>()

    fun enqueue(file: PickedChatFile, caption: String, asImage: Boolean) {
        val item = OutgoingAttachment(
            localId = "local-${UUID.randomUUID()}",
            uri = file.uri,
            fileName = file.fileName,
            mimeType = file.mimeType,
            sizeBytes = file.sizeBytes,
            isImage = asImage,
            caption = caption.trim(),
            createdAtMillis = System.currentTimeMillis()
        )
        targets[item.localId] = conversationId()
        _items.update { it + item }
        upload(item)
    }

    fun retry(localId: String) {
        val item = _items.value.firstOrNull { it.localId == localId && it.failedMessage != null } ?: return
        val reset = item.copy(failedMessage = null)
        _items.update { list -> list.map { if (it.localId == localId) reset else it } }
        upload(reset)
    }

    fun discard(localId: String) {
        _items.update { list -> list.filterNot { it.localId == localId } }
        targets.remove(localId)
    }

    private fun upload(item: OutgoingAttachment) {
        scope.launch {
            mutex.withLock {
                try {
                    val target = targets[item.localId] ?: return@withLock
                    val sent = chatRepository.sendAttachment(target, item)
                    // Pesan asli (juga datang lewat WebSocket) menggantikan item lokal; duplikat dibuang berdasar id.
                    onSent(sent)
                    discard(item.localId)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    val reason = e.userMessage()
                    _items.update { list -> list.map { if (it.localId == item.localId) it.copy(failedMessage = reason) else it } }
                }
            }
        }
    }
}
