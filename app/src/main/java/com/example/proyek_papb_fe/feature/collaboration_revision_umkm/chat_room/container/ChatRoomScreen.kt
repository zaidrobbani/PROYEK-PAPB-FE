package com.example.proyek_papb_fe.feature.collaboration_revision_umkm.chat_room.container

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthAppBar
import com.example.proyek_papb_fe.feature.auth.component.shared.withFrameMetrics
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.chat_room.component.ChatBubble
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.chat_room.component.ChatComposer
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.chat_room.component.ChatDateSeparator
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.chat_room.component.ChatProjectContext
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.chat_room.component.ChatRoomTopBar
import com.example.proyek_papb_fe.feature.umkm.data.FundStatus
import com.example.proyek_papb_fe.feature.umkm.data.UmkmConversationStore
import com.example.proyek_papb_fe.feature.umkm.data.UmkmProjectStore
import com.example.proyek_papb_fe.feature.umkm.data.UmkmTransactionStore
import com.example.proyek_papb_fe.feature.umkm.data.utcMillisToDate
import com.example.proyek_papb_fe.feature.umkm.project_list.component.formatRupiah
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

private val IndonesianLocale: Locale = Locale.forLanguageTag("id-ID")
private val SeparatorFormat = DateTimeFormatter.ofPattern("EEEE, d MMM yyyy", IndonesianLocale)
private val DeadlineFormat = DateTimeFormatter.ofPattern("d MMM yyyy", IndonesianLocale)
private val BubbleTime = DateTimeFormatter.ofPattern("HH.mm")

/**
 * Ruang Chat UMKM untuk satu percakapan dari [UmkmConversationStore] (DATA DUMMY lokal).
 * Kirim pesan hanya simulasi: pesan ditambahkan ke memori, tidak dikirim ke server/freelancer.
 *
 * [onOpenProject] null = project ini belum punya halaman detail; menu/lampiran/suara belum ada
 * desain lanjutannya, jadi hanya memberi pesan singkat.
 */
@Composable
fun ChatRoomScreen(
    conversationId: String,
    onBack: () -> Unit,
    onOpenProject: ((projectId: String) -> Unit)?,
    onCompareDrafts: ((conversationId: String, draftMessageId: String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    fun notAvailable(feature: String): () -> Unit = {
        Toast.makeText(context, "$feature belum tersedia di versi ini.", Toast.LENGTH_SHORT).show()
    }

    val conversation = UmkmConversationStore.conversation(conversationId)
    if (conversation == null) {
        // Tidak ada di Figma: id percakapan tidak ditemukan.
        Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            AuthAppBar(title = "Chat", onBack = onBack)
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Percakapan tidak ditemukan.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }

    // Membuka ruang chat = membaca pesan baru; lencana di Daftar Percakapan ikut hilang.
    LaunchedEffect(conversationId) { UmkmConversationStore.markRead(conversationId) }

    var draft by rememberSaveable(conversationId) { mutableStateOf("") }
    val messages = UmkmConversationStore.messages(conversationId)
    val listState = rememberLazyListState()
    // Mulai dari pesan terbaru, dan ikut turun setiap ada pesan baru.
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.scrollToItem(listState.layoutInfo.totalItemsCount.coerceAtLeast(1) - 1)
    }

    val project = conversation.projectId?.let { UmkmProjectStore.project(it) }
    val heldFunds = conversation.projectId?.let { id ->
        UmkmTransactionStore.transactions.firstOrNull { it.projectId == id && it.fundStatus == FundStatus.Tertahan }
    }?.let { formatRupiah(it.amount) }
    val today = LocalDate.now()
    val deadline = project?.deadlineUtcMillis?.let(::utcMillisToDate)?.let { date ->
        val days = ChronoUnit.DAYS.between(today, date)
        val left = when {
            days > 0 -> "$days hari lagi"
            days == 0L -> "hari ini"
            else -> "lewat ${-days} hari"
        }
        left to date.format(DeadlineFormat)
    }
    val senderLabel = "${conversation.freelancerName} · Freelancer"


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        ChatRoomTopBar(
            initials = conversation.initials,
            name = conversation.freelancerName,
            role = conversation.freelancerRole,
            onBack = onBack,
            onMore = notAvailable("Menu percakapan")
        )
        // Modal revisi menutupi area di bawah App Bar, seperti Figma.
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Column(modifier = Modifier.fillMaxSize()) {
                ChatProjectContext(
                    projectTitle = conversation.projectTitle,
                    heldFunds = heldFunds,
                    deadline = deadline,
                    // Detail project = Pemantauan Progres, hanya untuk project yang punya data milestone.
                    onOpenProject = if (project != null && project.milestones.isNotEmpty() && onOpenProject != null) {
                        { onOpenProject(project.id) }
                    } else {
                        notAvailable("Detail project")
                    }
                )
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    if (messages.isEmpty()) {
                        item(key = "empty") {
                            Text(
                                text = "Belum ada pesan.",
                                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium.withFrameMetrics(
                                    fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 18.sp
                                )
                            )
                        }
                    }
                    messages.forEachIndexed { index, message ->
                        val previous = messages.getOrNull(index - 1)
                        val newDay = previous == null || previous.sentAt.toLocalDate() != message.sentAt.toLocalDate()
                        if (newDay) {
                            item(key = "date-${message.sentAt.toLocalDate()}") {
                                ChatDateSeparator(message.sentAt.format(SeparatorFormat))
                            }
                        }
                        val sameSender = !newDay && previous?.fromUmkm == message.fromUmkm
                        item(key = message.id) {
                            ChatBubble(
                                message = message,
                                time = "${message.sentAt.format(BubbleTime)} WIB",
                                senderLabel = senderLabel,
                                showSender = !sameSender,
                                topPadding = when {
                                    newDay -> 0.dp
                                    sameSender -> 4.dp
                                    else -> 12.dp
                                },
                                // Draf perbaikan membuka Bandingkan Draf. Minta revisi memakai layar
                                // Review Hasil milik modul Putri (belum digabung ke branch ini).
                                onOpenDraft = if (message.attachment?.previousDraftMessageId != null && onCompareDrafts != null) {
                                    { onCompareDrafts(conversationId, message.id) }
                                } else {
                                    null
                                }
                            )
                        }
                    }
                }
                ChatComposer(
                    value = draft,
                    onValueChange = { draft = it },
                    onSend = {
                        if (UmkmConversationStore.sendMessage(conversationId, draft)) draft = ""
                    },
                    onAdd = notAvailable("Aksi tambahan"),
                    onAttachImage = notAvailable("Lampiran gambar"),
                    onVoice = notAvailable("Pesan suara")
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun ChatRoomScreenPreview() {
    PROYEKPAPBFETheme {
        ChatRoomScreen(conversationId = "dummy-chat-andi", onBack = {}, onOpenProject = null)
    }
}
