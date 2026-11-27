package com.example.proyek_papb_fe.feature.umkm.data

import androidx.compose.runtime.mutableStateMapOf
import com.example.proyek_papb_fe.core.data.repository.ConversationOverview
import com.example.proyek_papb_fe.core.domain.model.DeletedMessageLabel
import com.example.proyek_papb_fe.core.domain.model.Message
import com.example.proyek_papb_fe.core.domain.model.MessageAttachment
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
    val freelancerId: String,
    val photoUrl: String?,
    val freelancerName: String,
    val freelancerRole: String,
    val projectId: String?,
    val fallbackProjectTitle: String,
    val stage: ConversationStage,
    val lastMessage: String,
    val lastMessageAt: LocalDateTime,
    val unreadCount: Int,
    val projectStatus: String? = null
) {
    /** Project selesai/dibatalkan: tidak dipilih otomatis sebagai tujuan kirim. */
    val isClosed: Boolean
        get() = projectStatus == ProjectStatusCode.SELESAI || projectStatus == ProjectStatusCode.DIBATALKAN

    val projectTitle: String
        get() = fallbackProjectTitle

    val initials: String
        get() = freelancerName.split(" ").filter { it.isNotBlank() }.take(2)
            .joinToString("") { it.first().uppercase() }
}

/**
 * Satu baris chat per freelancer (seperti WhatsApp). BE menyimpan percakapan per project,
 * jadi semua percakapan dengan freelancer yang sama digabung di sini.
 */
data class UmkmThread(
    val freelancerId: String,
    /** Urut dari aktivitas terbaru. */
    val conversations: List<UmkmConversation>
) {
    private val latest: UmkmConversation get() = conversations.first()
    val freelancerName: String get() = latest.freelancerName
    val freelancerRole: String get() = latest.freelancerRole
    val photoUrl: String? get() = conversations.firstNotNullOfOrNull { it.photoUrl }
    val initials: String get() = latest.initials
    val lastMessage: String get() = latest.lastMessage
    val lastMessageAt: LocalDateTime get() = latest.lastMessageAt
    val unreadCount: Int get() = conversations.sumOf { it.unreadCount }
    val projectTitle: String
        get() = latest.projectTitle + if (conversations.size > 1) " · +${conversations.size - 1} project lain" else ""

    /** Percakapan yang dibuka dari daftar: project aktif dengan pesan terbaru, atau yang terbaru. */
    val primaryConversationId: String
        get() = (conversations.firstOrNull { !it.isClosed } ?: latest).id

    fun hasStage(stage: ConversationStage) = conversations.any { it.stage == stage }
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
    val readByFreelancer: Boolean = false,
    /** Foto/dokumen chat dari server (berbeda dengan `attachment` draf hasil kerja). */
    val file: MessageAttachment? = null,
    /** Dihapus untuk semua orang: tampil sebagai "Pesan ini telah dihapus". */
    val isDeleted: Boolean = false
)

/** Cache percakapan UMKM dari BE (`ChatOverviewRepository`), diperbarui lewat WebSocket saat ruang chat terbuka. */
object UmkmConversationStore {
    private val items = mutableStateListOf<UmkmConversation>()
    private val messagesById = mutableStateMapOf<String, List<ChatMessage>>()

    val conversations: List<UmkmConversation> get() = items.sortedByDescending { it.lastMessageAt }

    fun conversation(id: String): UmkmConversation? = items.firstOrNull { it.id == id }

    fun conversationForProject(projectId: String): UmkmConversation? = items.firstOrNull { it.projectId == projectId }

    val threads: List<UmkmThread>
        get() = items.groupBy { it.freelancerId }
            .map { (freelancerId, list) -> UmkmThread(freelancerId, list.sortedByDescending { it.lastMessageAt }) }
            .sortedByDescending { it.lastMessageAt }

    fun threadOf(conversationId: String): UmkmThread? =
        conversation(conversationId)?.freelancerId?.let { id -> threads.firstOrNull { it.freelancerId == id } }

    /** Semua pesan dengan satu freelancer dari seluruh project, urut waktu. */
    fun threadMessages(thread: UmkmThread): List<ChatMessage> =
        thread.conversations.flatMap { messages(it.id) }.sortedBy { it.sentAt }

    fun replace(list: List<ConversationOverview>, myId: String?) {
        items.clear()
        list.forEach { upsert(it, myId) }
    }

    fun upsert(overview: ConversationOverview, myId: String?) {
        val status = overview.projectStatus
        val conversation = UmkmConversation(
            id = overview.conversation.id,
            freelancerId = overview.conversation.freelancerId,
            photoUrl = overview.counterpartPhotoUrl,
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
            lastMessage = overview.lastMessage?.previewText.orEmpty(),
            lastMessageAt = toLocal(overview.lastMessage?.sentAtMillis ?: overview.conversation.lastMessageAtMillis ?: 0L),
            unreadCount = overview.unreadCount,
            projectStatus = status
        )
        val index = items.indexOfFirst { it.id == conversation.id }
        if (index >= 0) items[index] = conversation else items.add(conversation)
        replaceMessages(conversation.id, overview.messages, myId)
    }

    fun replaceMessages(conversationId: String, messages: List<Message>, myId: String?) {
        messagesById[conversationId] = messages.map { it.toChatMessage(myId) }
    }

    fun append(message: Message, myId: String?) {
        val current = messages(message.conversationId)
        if (current.any { it.id == message.id }) return
        messagesById[message.conversationId] = current + message.toChatMessage(myId)
        val index = items.indexOfFirst { it.id == message.conversationId }
        if (index >= 0) {
            items[index] = items[index].copy(lastMessage = message.previewText, lastMessageAt = toLocal(message.sentAtMillis))
        }
    }

    fun markReadByFreelancer(conversationId: String) {
        messagesById[conversationId] = messages(conversationId).map { if (it.fromUmkm) it.copy(readByFreelancer = true) else it }
    }

    fun messages(conversationId: String): List<ChatMessage> = messagesById[conversationId].orEmpty()

    /** Hapus untuk saya: pesan hilang dari perangkat ini. */
    fun removeMessages(conversationId: String, ids: Set<String>) {
        messagesById[conversationId] = messages(conversationId).filterNot { it.id in ids }
        refreshPreview(conversationId)
    }

    /** Hapus untuk semua orang: isi & lampiran dibuang, pesan tetap ada sebagai penanda. */
    fun markDeleted(conversationId: String, ids: Set<String>) {
        messagesById[conversationId] = messages(conversationId).map {
            if (it.id in ids) it.copy(text = "", file = null, attachment = null, isDeleted = true) else it
        }
        refreshPreview(conversationId)
    }

    private fun refreshPreview(conversationId: String) {
        val index = items.indexOfFirst { it.id == conversationId }
        if (index < 0) return
        val last = messages(conversationId).lastOrNull()
        items[index] = items[index].copy(
            lastMessage = when {
                last == null -> ""
                last.isDeleted -> DeletedMessageLabel
                last.file != null && last.text.isBlank() -> if (last.file.isImage) "📷 Foto" else "📄 ${last.file.fileName}"
                else -> last.text
            }
        )
    }

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
        text = text,
        sentAt = toLocal(sentAtMillis),
        readByFreelancer = readAtMillis != null,
        file = attachments.firstOrNull(),
        isDeleted = isDeleted
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
fun searchThreads(source: List<UmkmThread>, query: String, stage: ConversationStage?): List<UmkmThread> {
    val tokens = query.lowercase().split(" ").filter { it.isNotBlank() }
    return source.filter { thread ->
        val haystack = (listOf(thread.freelancerName, thread.lastMessage) + thread.conversations.map { it.projectTitle })
            .joinToString(" ").lowercase()
        (stage == null || thread.hasStage(stage)) && tokens.all { it in haystack }
    }
}

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
