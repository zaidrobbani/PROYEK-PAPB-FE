package com.example.proyek_papb_fe.core.domain.model

data class Conversation(
    val id: String,
    val umkmId: String,
    val freelancerId: String,
    val projectId: String?,
    val lastMessageAtMillis: Long?
)

data class Message(
    val id: String,
    val conversationId: String,
    val senderUserId: String,
    val text: String,
    val type: String,
    val sentAtMillis: Long,
    val readAtMillis: Long?,
    val attachments: List<MessageAttachment> = emptyList()
) {
    val attachmentNames: List<String> get() = attachments.map { it.fileName }

    /** Teks ringkas untuk daftar percakapan/notifikasi: caption, atau label lampiran seperti WhatsApp. */
    val previewText: String
        get() {
            val first = attachments.firstOrNull()
            return when {
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
