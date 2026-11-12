package com.example.proyek_papb_fe.feature.freelancer.chat.container

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyek_papb_fe.core.data.ChatStore
import com.example.proyek_papb_fe.core.data.model.ChatConversation
import com.example.proyek_papb_fe.core.data.model.MessageSender
import com.example.proyek_papb_fe.core.data.model.ProjectFilter
import com.example.proyek_papb_fe.feature.freelancer.chat.component.ChatFilterOption
import com.example.proyek_papb_fe.feature.freelancer.chat.component.ChatListItem
import com.example.proyek_papb_fe.feature.freelancer.chat.component.ChatListScreen

private fun ChatConversation.matches(query: String): Boolean {
    val q = query.trim()
    if (q.isEmpty()) return true
    return listOf(umkmName, contactName, projectTitle, lastMessage.text).any { it.contains(q, ignoreCase = true) }
}

private fun ChatConversation.toListItem(now: Long) = ChatListItem(
    conversationId = id,
    initials = umkmInitials,
    umkmName = umkmName,
    projectTitle = projectTitle,
    messagePreview = lastMessage.let { message ->
        // Lampiran tanpa keterangan: tampilkan jenis + nama file agar pratinjau tidak kosong.
        val body = message.text.ifBlank {
            message.attachment?.let { (if (it.isImage) "Foto: " else "Dokumen: ") + it.fileName }.orEmpty()
        }
        if (message.sender == MessageSender.FREELANCER) "Anda: $body" else body
    },
    timeLabel = formatListTime(lastMessage.sentAtMillis, now),
    unreadCount = unreadCount
)

/**
 * Container tab Chat: pencarian + filter status (keduanya bertahan saat navigasi),
 * lalu satu daftar percakapan urut aktivitas terbaru. Status tiap percakapan = status
 * project-nya ([ChatStore.statusOf], sumber yang sama dengan tab Project); status hanya
 * menjadi filter, tidak ditampilkan per baris.
 */
@Composable
fun ChatScreen(onConversationClick: (conversationId: Int) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf(ProjectFilter.SEMUA) }

    // Dibaca langsung (snapshot state): pesan terkirim / dibaca di ruang chat ikut tampil.
    val now = System.currentTimeMillis()
    val searched = ChatStore.getAll()
        // Ruang kosong dari Detail Project baru masuk daftar setelah ada pesan pertama.
        .filter { it.messages.isNotEmpty() && it.matches(query) }
        .sortedByDescending { it.lastMessage.sentAtMillis }

    val filterOptions = ProjectFilter.entries.map { filter ->
        ChatFilterOption(filter, searched.count { filter.matches(ChatStore.statusOf(it)) })
    }
    val conversations = searched
        .filter { selectedFilter.matches(ChatStore.statusOf(it)) }
        .map { it.toListItem(now) }

    ChatListScreen(
        query = query,
        onQueryChange = { query = it },
        filterOptions = filterOptions,
        selectedFilter = selectedFilter,
        onFilterSelect = { selectedFilter = it },
        conversations = conversations,
        onConversationClick = onConversationClick
    )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun ChatScreenPreview() {
    ChatScreen(onConversationClick = {})
}
