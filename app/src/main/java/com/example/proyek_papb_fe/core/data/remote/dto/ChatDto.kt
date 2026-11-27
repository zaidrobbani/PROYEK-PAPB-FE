package com.example.proyek_papb_fe.core.data.remote.dto

data class ConversationDto(
    val id: String,
    val umkmId: String,
    val freelancerId: String,
    val projectId: String?,
    val lastMessageAt: String?,
    val createdAt: String?,
    val updatedAt: String?
)

data class ConversationListDto(
    val items: List<ConversationDto>?,
    val limit: Int,
    val offset: Int
)

data class CreateConversationRequest(
    val counterpartUserId: String,
    val projectId: String?
)

data class MessageDto(
    val id: String,
    val conversationId: String,
    val senderUserId: String,
    val messageText: String?,
    val messageType: String,
    val sentAt: String,
    val createdAt: String?,
    val readAt: String?,
    /** Terisi bila dihapus untuk semua orang (teks null, tanpa lampiran). */
    val deletedAt: String? = null
)

data class MessageAttachmentDto(
    val id: String,
    val messageId: String,
    val originalFileName: String,
    val mimeType: String,
    val fileSizeBytes: Long,
    val uploadedAt: String?,
    val downloadPath: String?,
    val thumbnailPath: String?
)

data class MessageWithAttachmentsDto(
    val message: MessageDto,
    val attachments: List<MessageAttachmentDto>?
)

data class MessageCursorDto(val sentAt: String, val messageId: String)

data class MessagePageDto(
    val conversationId: String,
    val items: List<MessageWithAttachmentsDto>?,
    val hasMore: Boolean,
    val nextBefore: MessageCursorDto?
)

data class SendMessageRequest(val messageText: String)

data class MarkReadDto(val conversationId: String, val markedCount: Int)

/** `scope`: `ME` (hapus untuk saya) atau `EVERYONE` (hapus untuk semua orang). */
data class DeleteMessagesRequest(val messageIds: List<String>, val scope: String)

data class DeleteMessagesDto(
    val conversationId: String,
    val scope: String,
    val messageIds: List<String>?,
    val deletedAt: String?
)
