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
    val readAt: String?
)

data class MessageAttachmentDto(
    val id: String,
    val messageId: String,
    val originalFileName: String,
    val mimeType: String,
    val fileSizeBytes: Long,
    val uploadedAt: String?
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
