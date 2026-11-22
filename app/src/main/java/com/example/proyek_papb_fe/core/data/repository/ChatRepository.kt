package com.example.proyek_papb_fe.core.data.repository

import com.example.proyek_papb_fe.core.data.remote.dto.toDomain
import com.example.proyek_papb_fe.core.data.source.ChatRemoteDataSource
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
}

class DefaultChatRepository(
    private val remote: ChatRemoteDataSource
) : ChatRepository {

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
}
