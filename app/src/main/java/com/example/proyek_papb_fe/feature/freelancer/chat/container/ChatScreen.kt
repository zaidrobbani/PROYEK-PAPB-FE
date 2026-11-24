package com.example.proyek_papb_fe.feature.freelancer.chat.container

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.data.model.ProjectFilter
import com.example.proyek_papb_fe.core.data.repository.ConversationOverview
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.core.ui.components.initialsOf
import com.example.proyek_papb_fe.feature.chat.viewmodel.ChatListViewModel
import com.example.proyek_papb_fe.feature.freelancer.chat.component.ChatFilterOption
import com.example.proyek_papb_fe.feature.freelancer.chat.component.ChatListItem
import com.example.proyek_papb_fe.feature.freelancer.chat.component.ChatListScreen
import com.example.proyek_papb_fe.feature.freelancer.data.freelancerStatusOf

private fun ConversationOverview.matches(query: String): Boolean {
    val q = query.trim()
    if (q.isEmpty()) return true
    return listOfNotNull(counterpartName, counterpartSubtitle, projectTitle, lastMessage?.previewText)
        .any { it.contains(q, ignoreCase = true) }
}

private fun ConversationOverview.lastActivity(): Long = lastMessage?.sentAtMillis ?: conversation.lastMessageAtMillis ?: 0L

/**
 * Satu baris per UMKM, seperti chat UMKM: semua project dengan UMKM yang sama digabung. Baris membuka percakapan
 * dengan aktivitas terbaru; di ruang chat freelancer memilih project tujuan lewat "Kirim ke".
 */
private fun List<ConversationOverview>.toListItem(now: Long, myId: String?): ChatListItem {
    val latest = maxBy { it.lastActivity() }
    val name = latest.counterpartSubtitle ?: latest.counterpartName
    return ChatListItem(
        conversationId = latest.conversation.id,
        initials = initialsOf(name),
        umkmName = name,
        projectTitle = if (size > 1) "$size proyek · ${latest.projectTitle ?: "Percakapan umum"}" else latest.projectTitle ?: "Percakapan umum",
        messagePreview = latest.lastMessage?.let { message ->
            val body = message.previewText
            if (message.senderUserId == myId) "Anda: $body" else body
        } ?: "Belum ada pesan",
        timeLabel = latest.lastActivity().takeIf { it > 0 }?.let { formatListTime(it, now) }.orEmpty(),
        unreadCount = sumOf { it.unreadCount },
        photoUrl = latest.counterpartPhotoUrl
    )
}

@Composable
fun ChatScreen(
    onConversationClick: (conversationId: String) -> Unit,
    onNotificationClick: () -> Unit,
    viewModel: ChatListViewModel = viewModel(factory = ChatListViewModel.factory(AppContainer.chatOverviewRepository))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var query by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf(ProjectFilter.SEMUA) }
    LaunchedEffect(Unit) { viewModel.load() }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    val now = System.currentTimeMillis()
    val myId = AppContainer.currentUserId
    val statusOf = { overview: ConversationOverview -> freelancerStatusOf(null, overview.projectStatus) }
    val groups = uiState.conversations.groupBy { it.conversation.umkmId }.values
        .sortedByDescending { group -> group.maxOf { it.lastActivity() } }
    val searched = groups.filter { group -> group.any { it.matches(query) } }
    val filterOptions = ProjectFilter.entries.map { filter ->
        ChatFilterOption(filter, searched.count { group -> group.any { filter.matches(statusOf(it)) } })
    }

    ChatListScreen(
        query = query,
        onQueryChange = { query = it },
        filterOptions = filterOptions,
        selectedFilter = selectedFilter,
        onFilterSelect = { selectedFilter = it },
        conversations = searched.filter { group -> group.any { selectedFilter.matches(statusOf(it)) } }.map { it.toListItem(now, myId) },
        onConversationClick = onConversationClick,
        onNotificationClick = onNotificationClick
    )
}
