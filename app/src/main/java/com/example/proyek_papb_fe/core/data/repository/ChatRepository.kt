package com.example.proyek_papb_fe.core.data.repository

import com.example.proyek_papb_fe.core.data.remote.ChatRealtimeClient
import com.example.proyek_papb_fe.core.data.remote.ChatRealtimeSession
import com.example.proyek_papb_fe.core.data.remote.dto.toDomain
import com.example.proyek_papb_fe.core.util.DateTimeParser.parseUtcMillis
import com.example.proyek_papb_fe.core.data.source.ChatAttachmentReader
import com.example.proyek_papb_fe.core.data.source.ChatRemoteDataSource
import com.example.proyek_papb_fe.core.data.source.PickedChatFile
import com.example.proyek_papb_fe.core.domain.model.OutgoingAttachment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.example.proyek_papb_fe.core.domain.model.Conversation
import com.example.proyek_papb_fe.core.domain.model.Message
import com.example.proyek_papb_fe.core.domain.model.MessagePage

interface ChatRepository {
    suspend fun conversations(): List<Conversation>
    suspend fun conversation(conversationId: String): Conversation
    suspend fun openConversation(counterpartUserId: String, projectId: String?): Conversation
    suspend fun messages(conversationId: String, beforeAt: String? = null, beforeId: String? = null): MessagePage
    suspend fun send(conversationId: String, text: String): Message
    suspend fun markRead(conversationId: String)

    suspend fun deleteMessages(conversationId: String, messageIds: List<String>, forEveryone: Boolean): Long

    suspend fun sendAttachment(conversationId: String, attachment: OutgoingAttachment): Message

    fun describeFile(uri: String): PickedChatFile

    fun realtime(conversationId: String): ChatRealtimeSession
}

class DefaultChatRepository(
    private val remote: ChatRemoteDataSource,
    private val realtimeClient: ChatRealtimeClient,
    private val attachmentReader: ChatAttachmentReader
) : ChatRepository {
    override fun describeFile(uri: String): PickedChatFile = attachmentReader.describe(uri)

    override suspend fun sendAttachment(conversationId: String, attachment: OutgoingAttachment): Message {
        val (bytes, fileName, mime) = withContext(Dispatchers.Default) { attachmentReader.read(attachment) }
        val part = MultipartBody.Part.createFormData("file", fileName, bytes.toRequestBody(mime.toMediaTypeOrNull()))
        val caption = attachment.caption.trim().takeIf { it.isNotEmpty() }?.toRequestBody("text/plain".toMediaType())
        return remote.sendAttachment(conversationId, part, caption).toDomain()
    }

    override fun realtime(conversationId: String): ChatRealtimeSession = realtimeClient.open(conversationId)

    override suspend fun conversations(): List<Conversation> = remote.list().items.orEmpty().map { it.toDomain() }

    override suspend fun conversation(conversationId: String): Conversation = remote.detail(conversationId).toDomain()

    override suspend fun openConversation(counterpartUserId: String, projectId: String?): Conversation =
        remote.createOrGet(counterpartUserId, projectId).toDomain()

    override suspend fun messages(conversationId: String, beforeAt: String?, beforeId: String?): MessagePage {
        val page = remote.messages(conversationId, beforeAt, beforeId)
        return MessagePage(
            items = page.items.orEmpty().map { it.toDomain() }.sortedBy { it.sentAtMillis },
            hasMore = page.hasMore,
            nextBeforeAt = page.nextBefore?.sentAt,
            nextBeforeId = page.nextBefore?.messageId
        )
    }

    override suspend fun send(conversationId: String, text: String): Message =
        remote.send(conversationId, text.trim()).toDomain()

    override suspend fun markRead(conversationId: String) {
        remote.markRead(conversationId)
    }

    override suspend fun deleteMessages(conversationId: String, messageIds: List<String>, forEveryone: Boolean): Long {
        val result = remote.deleteMessages(conversationId, messageIds, if (forEveryone) "EVERYONE" else "ME")
        return parseUtcMillis(result.deletedAt) ?: System.currentTimeMillis()
    }
}
