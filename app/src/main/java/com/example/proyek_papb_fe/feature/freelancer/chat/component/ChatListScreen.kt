package com.example.proyek_papb_fe.feature.freelancer.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.R
import com.example.proyek_papb_fe.core.data.model.ProjectFilter
import com.example.proyek_papb_fe.core.ui.components.RootTopBar
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaTypography
import kotlinx.coroutines.launch

/**
 * Jumlah percakapan per pill filter. Filter = [ProjectFilter] yang sama dengan tab Project
 * (Semua · Menunggu Kesepakatan · Negosiasi · Project Berjalan · Selesai · Dibatalkan).
 */
data class ChatFilterOption(val filter: ProjectFilter, val count: Int)

/** Data tampilan satu baris percakapan (sudah diformat). */
data class ChatListItem(
    val conversationId: Int,
    val initials: String,
    val umkmName: String,
    val projectTitle: String,
    /** Pesan terakhir; diawali "Anda: " bila dikirim freelancer. */
    val messagePreview: String,
    /** "09.48", "Kemarin", atau "5 Okt". */
    val timeLabel: String,
    val unreadCount: Int
)

/**
 * Figma: Daftar Percakapan (Freelancer) (273:415).
 * Header "Chat" → pencarian → pill filter tetap; hanya daftar yang di-scroll.
 * "Semua" adalah satu daftar utuh (tanpa bagian per status), urut aktivitas terbaru.
 */
@Composable
fun ChatListScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    filterOptions: List<ChatFilterOption>,
    selectedFilter: ProjectFilter,
    onFilterSelect: (ProjectFilter) -> Unit,
    conversations: List<ChatListItem>,
    onConversationClick: (conversationId: Int) -> Unit,
    modifier: Modifier = Modifier,
    hasUnreadNotification: Boolean = true,
    onNotificationClick: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TemuKaryaColors.BgCanvas)
    ) {
        RootTopBar(
            title = "Chat",
            hasUnreadNotification = hasUnreadNotification,
            onNotificationClick = onNotificationClick
        )

        ChatSearchField(
            query = query,
            onQueryChange = { value ->
                onQueryChange(value)
                scope.launch { listState.scrollToItem(0) }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = ChatHorizontalPadding, end = ChatHorizontalPadding, top = 8.dp)
        )

        ChatFilterRow(
            options = filterOptions,
            selectedFilter = selectedFilter,
            onFilterSelect = { filter ->
                onFilterSelect(filter)
                scope.launch { listState.scrollToItem(0) }
            },
            modifier = Modifier.padding(top = 8.dp)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(
                start = ChatHorizontalPadding,
                end = ChatHorizontalPadding,
                top = 4.dp,
                bottom = 24.dp
            )
        ) {
            if (conversations.isEmpty()) {
                item(key = "empty") { ChatEmptyState(selectedFilter = selectedFilter, query = query) }
            }
            items(items = conversations, key = { it.conversationId }) { item ->
                ConversationRow(item = item, onClick = { onConversationClick(item.conversationId) })
            }
            item(key = "footnote") { ChatFootnote() }
        }
    }
}

/** Catatan kecil di akhir daftar (Figma: Footnote). */
@Composable
private fun ChatFootnote() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_info_outline),
            contentDescription = null,
            tint = TemuKaryaColors.TextPlaceholder,
            modifier = Modifier
                .padding(top = 1.dp)
                .size(15.dp)
        )
        Text(
            text = "Percakapan terbuka setelah UMKM memilih pengajuanmu pada sebuah proyek.",
            style = TemuKaryaTypography.BodyRegular,
            color = TemuKaryaColors.TextPlaceholder,
            modifier = Modifier.widthIn(max = 300.dp)
        )
    }
}

/** Empty state ringan (Figma: Empty State (filter kosong)), tanpa kartu. */
@Composable
private fun ChatEmptyState(selectedFilter: ProjectFilter, query: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_forum),
            contentDescription = null,
            tint = TemuKaryaColors.TextPlaceholder,
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = "Belum ada percakapan",
            style = TemuKaryaTypography.SectionTitle,
            color = TemuKaryaColors.TextHeading,
            textAlign = TextAlign.Center
        )
        Text(
            text = when {
                query.isNotBlank() -> "Tidak ada percakapan yang cocok dengan \"${query.trim()}\"."
                selectedFilter == ProjectFilter.SEMUA -> "Percakapan dengan UMKM akan muncul di sini setelah pengajuanmu dipilih."
                else -> "Belum ada percakapan dengan status \"${selectedFilter.label}\"."
            },
            style = TemuKaryaTypography.BodyRegular,
            color = TemuKaryaColors.TextBody,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun ChatListScreenPreview() {
    ChatListScreen(
        query = "",
        onQueryChange = {},
        filterOptions = listOf(5, 0, 2, 2, 1, 0).zip(ProjectFilter.entries) { count, filter ->
            ChatFilterOption(filter, count)
        },
        selectedFilter = ProjectFilter.SEMUA,
        onFilterSelect = {},
        conversations = listOf(
            ChatListItem(1, "KS", "Kedai Kopi Senja", "Desain Rebranding Logo & Kemasan Pouch Kopi 250gr",
                "Anda: Tentu Bu Rina, nanti saya buatkan 2 opsi ketebalan font", "09.48", 0),
            ChatListItem(3, "SM", "Toko Kue Sari Manis", "Desain Kemasan Kue Kering Lebaran",
                "Boleh kirim contoh kemasan yang pernah Mas buat?", "08.50", 2)
        ),
        onConversationClick = {}
    )
}
