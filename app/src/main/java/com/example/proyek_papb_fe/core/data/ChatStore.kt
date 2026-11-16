package com.example.proyek_papb_fe.core.data

import androidx.compose.runtime.mutableStateMapOf
import com.example.proyek_papb_fe.core.data.model.ChatAttachment
import com.example.proyek_papb_fe.core.data.model.ChatConversation
import com.example.proyek_papb_fe.core.data.model.ChatProjectContext
import com.example.proyek_papb_fe.core.data.model.ChatMessage
import com.example.proyek_papb_fe.core.data.model.MessageSender
import com.example.proyek_papb_fe.core.data.model.ProjectStatus

/**
 * Percakapan tab Chat di memori, per id percakapan (sementara, sebelum ada backend).
 * Berbasis snapshot state: daftar Chat ikut ter-update saat percakapan dibaca atau
 * pesan baru dikirim dari ruang chat.
 */
object ChatStore {

    /** Id ruang chat yang dibuat dari Detail Project = offset + projectId (tidak bentrok dengan dummy). */
    private const val NEW_CONVERSATION_ID_OFFSET = 1000

    private val conversations = mutableStateMapOf<Int, ChatConversation>().apply {
        ChatDummyData.seed().forEach { put(it.id, it) }
    }

    fun getAll(): List<ChatConversation> = conversations.values.toList()

    fun get(id: Int): ChatConversation? = conversations[id]

    /**
     * Status project percakapan ini, sumber yang sama dengan tab Project: penawaran project
     * di [SubmittedOfferStore] bila ada; jika belum ada, status bawaan percakapan.
     * Dipakai daftar Chat (filter) dan ruang chat (aksi yang tersedia), dari pintu masuk mana pun.
     */
    fun statusOf(conversation: ChatConversation): ProjectStatus =
        conversation.projectId?.let { SubmittedOfferStore.get(it)?.status } ?: conversation.projectStatus

    /**
     * Ruang chat untuk project ini (dibuka dari tombol Chat di Detail Project).
     * Memakai percakapan yang sudah ada untuk projectId tersebut; bila belum ada, dibuat
     * ruang kosong dari data project (UMKM, pemilik, judul, batas pengajuan) tanpa pesan
     * karangan. Ruang kosong belum tampil di daftar Chat sampai ada pesan pertama.
     * Hasilnya deterministik: projectId yang sama selalu ke percakapan yang sama.
     */
    fun conversationIdForProject(projectId: Int): Int? {
        conversations.values.firstOrNull { it.projectId == projectId }?.let { return it.id }
        val project = FreelancerProjectDummyData.getById(projectId) ?: return null
        val id = NEW_CONVERSATION_ID_OFFSET + projectId
        // Penawaran sudah disetujui UMKM: ruang ini adalah ruang negosiasi project tersebut,
        // berisi percakapan negosiasi yang sudah ada (bukan ruang kosong).
        val offer = SubmittedOfferStore.get(projectId)
        if (offer != null && offer.isApproved && !offer.isCancelled) {
            conversations[id] = ChatDummyData.negotiationConversation(id, project, offer.offerAmount, offer.durationDays)
            return id
        }
        conversations[id] = ChatConversation(
            id = id,
            projectId = projectId,
            umkmName = project.clientName,
            umkmInitials = project.clientInitials,
            contactName = project.ownerName,
            contactInitials = ChatDummyData.initialsOf(project.ownerName),
            projectTitle = project.title,
            // Belum ada penawaran: percakapan sebelum kesepakatan apa pun.
            projectStatus = ProjectStatus.MENUNGGU_KESEPAKATAN,
            context = ChatProjectContext(
                escrowLabel = null,
                note = "Pengajuan ditutup ",
                noteEmphasis = "${project.applicationDaysLeft} hari lagi"
            ),
            messages = emptyList()
        )
        return id
    }

    /** Ruang chat dibuka: semua pesan dianggap sudah dibaca. */
    fun markRead(id: Int) {
        conversations[id]?.takeIf { it.unreadCount > 0 }?.let {
            conversations[id] = it.copy(unreadCount = 0)
        }
    }

    /** Kirim pesan teks freelancer ke percakapan ini saja. */
    fun send(id: Int, text: String, sentAtMillis: Long = System.currentTimeMillis()) {
        append(id, text, null, sentAtMillis)
    }

    /**
     * Kirim lampiran (lokal saja, tanpa unggah ke backend): pesan keluar berisi nama file
     * dari Galeri / Kamera / Dokumen.
     */
    fun sendAttachment(
        id: Int,
        attachment: ChatAttachment,
        caption: String = "",
        sentAtMillis: Long = System.currentTimeMillis()
    ) {
        append(id, caption, attachment, sentAtMillis)
    }

    private fun append(id: Int, text: String, attachment: ChatAttachment?, sentAtMillis: Long) {
        val conversation = conversations[id] ?: return
        val message = ChatMessage(
            id = "$id-${conversation.messages.size + 1}-$sentAtMillis",
            sender = MessageSender.FREELANCER,
            text = text,
            sentAtMillis = sentAtMillis,
            attachment = attachment
        )
        conversations[id] = conversation.copy(messages = conversation.messages + message)
    }
}
