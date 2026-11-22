package com.example.proyek_papb_fe.core.data.repository

import com.example.proyek_papb_fe.core.domain.model.Conversation
import com.example.proyek_papb_fe.core.domain.model.Message
import com.example.proyek_papb_fe.core.session.AccountRole
import com.example.proyek_papb_fe.core.session.SessionManager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

data class ConversationOverview(
    val conversation: Conversation,
    val counterpartName: String,
    /** Nama usaha (UMKM) atau jabatan profesional (freelancer). */
    val counterpartSubtitle: String?,
    val projectTitle: String?,
    val projectStatus: String?,
    val messages: List<Message>,
    val unreadCount: Int,
    val workflow: com.example.proyek_papb_fe.core.domain.model.WorkflowDetail? = null
) {
    val lastMessage: Message? get() = messages.lastOrNull()
}

/**
 * `GET /conversations` hanya berisi id. Nama lawan bicara & judul project dirakit dari detail alur
 * (bila freelancer sudah terpilih), project publik, atau profil publik freelancer.
 */
class ChatOverviewRepository(
    private val chatRepository: ChatRepository,
    private val workflowRepository: WorkflowRepository,
    private val projectRepository: ProjectRepository,
    private val freelancerRepository: FreelancerRepository,
    private val sessionManager: SessionManager
) {
    suspend fun overviews(): List<ConversationOverview> = coroutineScope {
        chatRepository.conversations().map { conversation -> async { overview(conversation) } }.awaitAll()
            .sortedByDescending { it.lastMessage?.sentAtMillis ?: it.conversation.lastMessageAtMillis ?: 0L }
    }

    suspend fun overview(conversationId: String): ConversationOverview = overview(chatRepository.conversation(conversationId))

    private suspend fun overview(conversation: Conversation): ConversationOverview = coroutineScope {
        val me = sessionManager.currentUser
        val iAmUmkm = me?.role == AccountRole.UMKM
        val messages = async { runCatching { chatRepository.messages(conversation.id).items }.getOrDefault(emptyList()) }
        val workflow = conversation.projectId?.let { id -> runCatching { workflowRepository.detail(id) }.getOrNull() }
        val publicProject = if (workflow == null && conversation.projectId != null) {
            runCatching { projectRepository.detail(conversation.projectId) }.getOrNull()
        } else null
        val counterpart = if (iAmUmkm) workflow?.freelancer else workflow?.umkm
        val freelancerProfile = if (iAmUmkm && counterpart == null) {
            runCatching { freelancerRepository.detail(conversation.freelancerId).summary }.getOrNull()
        } else null
        val list = messages.await()
        ConversationOverview(
            conversation = conversation,
            counterpartName = counterpart?.name ?: freelancerProfile?.name ?: if (iAmUmkm) "Freelancer" else "UMKM",
            counterpartSubtitle = counterpart?.subtitle ?: freelancerProfile?.professionalTitle,
            projectTitle = workflow?.project?.title ?: publicProject?.title,
            projectStatus = workflow?.project?.status ?: publicProject?.status,
            messages = list,
            unreadCount = list.count { it.senderUserId != me?.id && it.readAtMillis == null },
            workflow = workflow
        )
    }
}
