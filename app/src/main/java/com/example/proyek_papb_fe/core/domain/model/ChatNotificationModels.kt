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
    val attachmentNames: List<String>
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
