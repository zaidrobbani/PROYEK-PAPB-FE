package com.example.proyek_papb_fe.core.data.model

data class ChatAttachment(
    val fileName: String,
    val isImage: Boolean = true
)

data class ChatMessage(
    val id: String,
    val sender: MessageSender,
    val text: String,
    val sentAtMillis: Long,
    val attachment: ChatAttachment? = null
)

data class ChatProjectContext(
    val escrowLabel: String?,
    val note: String,
    val noteEmphasis: String? = null,
    val noteSuffix: String = ""
)

data class ChatConversation(
    val id: String,
    val projectId: String?,
    val umkmName: String,
    val umkmInitials: String,
    val contactName: String,
    val contactInitials: String,
    val projectTitle: String,

    val projectStatus: ProjectStatus,
    val context: ChatProjectContext,

    val messages: List<ChatMessage>,
    val unreadCount: Int = 0
) {
    val lastMessage: ChatMessage get() = messages.last()
}
