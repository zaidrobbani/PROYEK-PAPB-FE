package com.example.proyek_papb_fe.core.domain.model

data class Conversation(
    val id: String,
    val umkmId: String,
    val freelancerId: String,
    val projectId: String?,
    val lastMessageAtMillis: Long?
)

/** Batas "Hapus untuk semua orang" (sama dengan BE). */
const val DeleteForEveryoneWindowMillis = 48L * 60 * 60 * 1000

const val DeletedMessageLabel = "🚫 Pesan ini telah dihapus"

data class Message(
    val id: String,
    val conversationId: String,
    val senderUserId: String,
    val text: String,
    val type: String,
    val sentAtMillis: Long,
    val readAtMillis: Long?,
    val attachments: List<MessageAttachment> = emptyList(),
    /** Terisi bila dihapus untuk semua orang. */
    val deletedAtMillis: Long? = null
) {
    val attachmentNames: List<String> get() = attachments.map { it.fileName }

    val isDeleted: Boolean get() = deletedAtMillis != null

    /** Versi tampilan setelah dihapus untuk semua orang: isi & lampiran dibuang. */
    fun asDeleted(atMillis: Long): Message = copy(text = "", attachments = emptyList(), deletedAtMillis = atMillis)

    /** Aturan BE: hanya pesan sendiri yang belum dihapus dan dikirim paling lama 48 jam lalu. */
    fun canDeleteForEveryone(myId: String?, nowMillis: Long = System.currentTimeMillis()): Boolean =
        senderUserId == myId && !isDeleted && nowMillis - sentAtMillis <= DeleteForEveryoneWindowMillis

    /** Teks ringkas untuk daftar percakapan/notifikasi: caption, atau label lampiran seperti WhatsApp. */
    val previewText: String
        get() {
            val first = attachments.firstOrNull()
            return when {
                isDeleted -> DeletedMessageLabel
                text.isNotBlank() -> if (first != null) "${if (first.isImage) "📷" else "📄"} $text" else text
                first == null -> ""
                first.isImage -> "📷 Foto"
                else -> "📄 ${first.fileName}"
            }
        }
}

/** Lampiran pesan chat. Path unduh butuh token; thumbnailPath hanya ada untuk foto. */
data class MessageAttachment(
    val id: String,
    val fileName: String,
    val mimeType: String,
    val sizeBytes: Long,
    val downloadPath: String,
    val thumbnailPath: String?
) {
    val isImage: Boolean get() = mimeType.startsWith("image/")
}

/** Lampiran yang sedang/akan dikirim dari perangkat (tampil di chat sebelum server menyimpannya). */
data class OutgoingAttachment(
    val localId: String,
    val uri: String,
    val fileName: String,
    val mimeType: String?,
    val sizeBytes: Long?,
    val isImage: Boolean,
    val caption: String,
    val createdAtMillis: Long,
    val failedMessage: String? = null
)

data class MessagePage(
    val items: List<Message>,
    val hasMore: Boolean,
    val nextBeforeAt: String?,
    val nextBeforeId: String?
)

object NotificationCategoryCode {
    const val PROJECT = "PROJECT"
    const val PAYMENT = "PAYMENT"
    const val MESSAGE = "MESSAGE"
}

data class AppNotification(
    val id: String,
    val type: String,
    val category: String,
    val title: String,
    val body: String,
    val referenceType: String?,
    val referenceId: String?,
    val isRead: Boolean,
    val createdAtMillis: Long?
)

data class NotificationList(
    val items: List<AppNotification>,
    val unreadCount: Int,
    val counts: Map<String, Int>
)
