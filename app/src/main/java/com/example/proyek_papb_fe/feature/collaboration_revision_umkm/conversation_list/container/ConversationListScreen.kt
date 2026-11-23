package com.example.proyek_papb_fe.feature.collaboration_revision_umkm.conversation_list.container

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.conversation_list.component.ConversationFilterPills
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.conversation_list.component.ConversationFootnote
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.conversation_list.component.ConversationRow
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.conversation_list.component.ConversationSearchField
import com.example.proyek_papb_fe.feature.umkm.component.shared.UmkmBottomNav
import com.example.proyek_papb_fe.feature.umkm.component.shared.UmkmTab
import com.example.proyek_papb_fe.feature.umkm.component.shared.UmkmTopBar
import com.example.proyek_papb_fe.feature.umkm.data.ConversationStage
import com.example.proyek_papb_fe.feature.umkm.data.UmkmConversationStore
import com.example.proyek_papb_fe.feature.umkm.data.formatConversationTime
import com.example.proyek_papb_fe.feature.umkm.data.searchThreads
import java.time.LocalDate
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.viewmodel.UmkmChatViewModel

@Composable
fun ConversationListScreen(
    onNotificationsClick: () -> Unit,
    onOpenConversation: ((conversationId: String) -> Unit)?,
    onTabSelected: (UmkmTab) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UmkmChatViewModel = viewModel(factory = UmkmChatViewModel.factory(null))
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }
    var query by rememberSaveable { mutableStateOf("") }
    var stageName by rememberSaveable { mutableStateOf<String?>(null) }
    val stage = stageName?.let { ConversationStage.valueOf(it) }

    // Satu baris per freelancer: percakapan per project digabung (lihat UmkmThread).
    val all = UmkmConversationStore.threads
    val shown = searchThreads(all, query, stage)
    val today = LocalDate.now()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        UmkmTopBar(onNotificationsClick = onNotificationsClick, title = "Chat")
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .imePadding(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
            item(key = "search") {
                ConversationSearchField(
                    query = query,
                    onQueryChange = { query = it },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item(key = "filters") {
                ConversationFilterPills(
                    selected = stage,
                    totalCount = all.size,
                    countFor = { s -> all.count { it.hasStage(s) } },
                    onSelect = { stageName = it?.name }
                )
            }
            item(key = "list-top") { Spacer(Modifier.height(8.dp)) }
            if (shown.isEmpty()) {
                item(key = "empty") {
                    Text(
                        text = "Tidak ada percakapan yang cocok.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium.withFrameMetrics(
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
            items(shown, key = { it.freelancerId }) { conversation ->
                ConversationRow(
                    conversation = conversation,
                    time = formatConversationTime(conversation.lastMessageAt, today),
                    onClick = {
                        onOpenConversation?.invoke(conversation.primaryConversationId)
                            ?: Toast.makeText(context, "Ruang Chat belum tersedia di versi ini.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item(key = "footnote") { ConversationFootnote(Modifier.padding(horizontal = 16.dp)) }
        }
        UmkmBottomNav(
            selectedTab = UmkmTab.Chat,
            onTabSelected = { tab -> if (tab != UmkmTab.Chat) onTabSelected(tab) }
        )
    }
}

