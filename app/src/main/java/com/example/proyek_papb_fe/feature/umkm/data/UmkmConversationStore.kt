package com.example.proyek_papb_fe.feature.umkm.data

import androidx.compose.runtime.mutableStateMapOf
import com.example.proyek_papb_fe.core.data.repository.ConversationOverview
import com.example.proyek_papb_fe.core.domain.model.Message
import com.example.proyek_papb_fe.core.domain.model.ProjectStatusCode
import java.time.Instant
import java.time.ZoneId

import androidx.annotation.DrawableRes
import androidx.compose.runtime.mutableStateListOf
import com.example.proyek_papb_fe.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class ConversationStage(val label: String) {
    ProjectBerjalan("Project berjalan"),
    Negosiasi("Negosiasi"),
    Selesai("Selesai")
}

data class UmkmConversation(
    val id: String,
    val freelancerName: String,
    val freelancerRole: String,
    val projectId: String?,
    val fallbackProjectTitle: String,
    val stage: ConversationStage,
    val lastMessage: String,
    val lastMessageAt: LocalDateTime,
    val unreadCount: Int
) {
    val projectTitle: String
        get() = fallbackProjectTitle

    val initials: String
        get() = freelancerName.split(" ").filter { it.isNotBlank() }.take(2)
            .joinToString("") { it.first().uppercase() }
}

data class ChatAttachment(
    val title: String,
    val draftLabel: String? = null,
    val milestoneNumber: Int? = null,
    val revisionAspects: List<String> = emptyList(),
    val versionLabel: String? = null,
    @param:DrawableRes val previewImage: Int? = null,
    val previousDraftMessageId: String? = null,
    val draftNote: String? = null,
    val previewTag: String? = null,
    val issueMarkers: List<Pair<Float, Float>> = emptyList()
)

data class ChatMessage(
    val id: String,
    val conversationId: String,
    val fromUmkm: Boolean,
    val text: String,
    val sentAt: LocalDateTime,
    val attachment: ChatAttachment? = null,
    val readByFreelancer: Boolean = false
)

/** Cache percakapan UMKM dari BE (`ChatOverviewRepository`); BE baru mendukung pesan teks. */
object UmkmConversationStore {
    private val items = mutableStateListOf<UmkmConversation>()
    private val messagesById = mutableStateMapOf<String, List<ChatMessage>>()

    val conversations: List<UmkmConversation> get() = items.sortedByDescending { it.lastMessageAt }

    fun conversation(id: String): UmkmConversation? = items.firstOrNull { it.id == id }

    fun conversationForProject(projectId: String): UmkmConversation? = items.firstOrNull { it.projectId == projectId }

    fun replace(list: List<ConversationOverview>, myId: String?) {
        items.clear()
        list.forEach { upsert(it, myId) }
    }

    fun upsert(overview: ConversationOverview, myId: String?) {
        val status = overview.projectStatus
        val conversation = UmkmConversation(
            id = overview.conversation.id,
            freelancerName = overview.counterpartName,
            freelancerRole = overview.counterpartSubtitle ?: "Freelancer",
            projectId = overview.conversation.projectId,
            fallbackProjectTitle = overview.projectTitle ?: "Percakapan umum",
            stage = when (status) {
                ProjectStatusCode.SELESAI -> ConversationStage.Selesai
                ProjectStatusCode.SIAP_DIKERJAKAN, ProjectStatusCode.SEDANG_DIKERJAKAN,
                ProjectStatusCode.MENUNGGU_REVIEW, ProjectStatusCode.REVISI -> ConversationStage.ProjectBerjalan
                else -> ConversationStage.Negosiasi
            },
            lastMessage = overview.lastMessage?.text.orEmpty(),
            lastMessageAt = toLocal(overview.lastMessage?.sentAtMillis ?: overview.conversation.lastMessageAtMillis ?: 0L),
            unreadCount = overview.unreadCount
        )
        val index = items.indexOfFirst { it.id == conversation.id }
        if (index >= 0) items[index] = conversation else items.add(conversation)
        replaceMessages(conversation.id, overview.messages, myId)
    }

    fun replaceMessages(conversationId: String, messages: List<Message>, myId: String?) {
        messagesById[conversationId] = messages.map { it.toChatMessage(myId) }
    }

    fun append(message: Message, myId: String?) {
        messagesById[message.conversationId] = messages(message.conversationId) + message.toChatMessage(myId)
    }

    fun messages(conversationId: String): List<ChatMessage> = messagesById[conversationId].orEmpty()

    fun markRead(id: String) {
        val index = items.indexOfFirst { it.id == id }
        if (index >= 0) items[index] = items[index].copy(unreadCount = 0)
    }

    fun reset() {
        items.clear()
        messagesById.clear()
    }

    private fun toLocal(millis: Long): LocalDateTime =
        Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime()

    private fun Message.toChatMessage(myId: String?) = ChatMessage(
        id = id,
        conversationId = conversationId,
        fromUmkm = senderUserId == myId,
        text = text.ifBlank { attachmentNames.joinToString() },
        sentAt = toLocal(sentAtMillis),
        readByFreelancer = readAtMillis != null
    )
}
fun formatConversationTime(time: LocalDateTime, today: LocalDate = LocalDate.now()): String = when (time.toLocalDate()) {
    today -> time.format(ConversationClock)
    today.minusDays(1) -> "Kemarin"
    else -> time.format(ConversationDate)
}

private val ConversationClock = DateTimeFormatter.ofPattern("HH.mm")
private val ConversationDate = DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("id-ID"))

/**
 * Pencarian per kata pada nama freelancer dan judul project (placeholder Figma:
 * "Cari nama freelancer atau project"). [stage] null = semua tahap.
 */
fun searchConversations(
    source: List<UmkmConversation>,
    query: String,
    stage: ConversationStage?
): List<UmkmConversation> {
    val tokens = query.lowercase().split(" ").filter { it.isNotBlank() }
    return source.filter { conversation ->
        val haystack = "${conversation.freelancerName} ${conversation.projectTitle}".lowercase()
        (stage == null || conversation.stage == stage) && tokens.all { it in haystack }
    }
}
