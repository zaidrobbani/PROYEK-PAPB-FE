package com.example.proyek_papb_fe.feature.freelancer.chat.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.ui.platform.LocalDensity
import com.example.proyek_papb_fe.feature.freelancer.negotiation.component.NegotiationActionBar
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.core.data.model.ChatProjectContext
import com.example.proyek_papb_fe.core.ui.theme.TemuKaryaColors

/** Data tampilan ruang chat (sudah diformat). */
data class ChatRoomUiState(
    val contactName: String,
    val contactInitials: String,
    val umkmName: String,
    val projectTitle: String,
    val context: ChatProjectContext,
    val items: List<ChatRoomItem>
)

/**
 * Figma: Ruang Chat (Freelancer) (17:4612).
 * App bar + strip konteks project tetap di atas; daftar pesan di-scroll di tengah;
 * panel lampiran (bila terbuka) + composer menempel di bawah dan ikut naik bersama
 * keyboard lewat [imePadding], sehingga daftar pesan tetap terlihat di atasnya.
 */
@Composable
fun ChatRoomScreen(
    state: ChatRoomUiState,
    draft: String,
    onDraftChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isAttachmentOpen: Boolean,
    onAttachmentToggle: () -> Unit,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onDocumentClick: () -> Unit,
    /** null = "Unggah Draf Pekerjaan" tidak tersedia (status project selain Project Berjalan). */
    onUploadDraftClick: (() -> Unit)?,
    onInputFocused: () -> Unit,
    onBack: () -> Unit,
    onMoreClick: () -> Unit,
    onProjectDetailClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    /** Terisi saat project ruang ini sedang negosiasi; null = ruang chat biasa. */
    negotiation: ChatNegotiationUi? = null,
    onCancelProjectClick: () -> Unit = {},
    onContinueProjectClick: () -> Unit = {},
    /** Kartu tambahan di bawah konteks project (mis. simulasi sisi UMKM); null = tidak ada. */
    topBanner: (@Composable () -> Unit)? = null
) {
    // Saat mengetik (keyboard terbuka) atau memilih lampiran, kartu & aksi negosiasi
    // disembunyikan sementara agar daftar pesan dan composer tetap lega.
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val showNegotiationExtras = negotiation != null && !isImeVisible && !isAttachmentOpen
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TemuKaryaColors.BgCanvas)
            .imePadding()
    ) {
        ChatRoomTopBar(
            contactName = state.contactName,
            contactInitials = state.contactInitials,
            umkmName = state.umkmName,
            onBack = onBack,
            onMoreClick = onMoreClick
        )
        ProjectContextStrip(
            projectTitle = state.projectTitle,
            context = state.context,
            onDetailClick = onProjectDetailClick,
            isNegotiation = negotiation != null
        )
        AnimatedVisibility(
            visible = showNegotiationExtras,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            negotiation?.let { NegotiationSummaryCard(negotiation = it) }
        }
        AnimatedVisibility(
            visible = topBanner != null && !isImeVisible && !isAttachmentOpen,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            topBanner?.invoke()
        }
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(items = state.items, key = { it.key }) { item ->
                when (item) {
                    is ChatRoomItem.DateSeparator -> ChatDateSeparator(label = item.label)
                    is ChatRoomItem.Message -> ChatMessageBubble(bubble = item.bubble)
                }
            }
        }
        // Aksi negosiasi (Negosiasi 26:86, dipakai ulang) di atas composer: mengubah status
        // project, terpisah jelas dari composer yang mengirim pesan.
        AnimatedVisibility(
            visible = showNegotiationExtras,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            NegotiationActionBar(
                onCancelClick = onCancelProjectClick,
                onContinueClick = onContinueProjectClick
            )
        }
        AnimatedVisibility(
            visible = isAttachmentOpen,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            AttachmentPanel(
                onGalleryClick = onGalleryClick,
                onCameraClick = onCameraClick,
                onDocumentClick = onDocumentClick,
                onUploadDraftClick = onUploadDraftClick
            )
        }
        MessageComposer(
            draft = draft,
            onDraftChange = onDraftChange,
            isAttachmentOpen = isAttachmentOpen,
            onAttachmentToggle = onAttachmentToggle,
            onCameraClick = onCameraClick,
            onSendClick = onSendClick,
            onInputFocused = onInputFocused
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun ChatRoomScreenPreview() {
    ChatRoomScreen(
        state = ChatRoomUiState(
            contactName = "Rina Pratiwi",
            contactInitials = "RP",
            umkmName = "Kedai Kopi Senja",
            projectTitle = "Desain Rebranding Logo & Kemasan Pouch Kopi 250gr",
            context = ChatProjectContext("Saldo tertahan Rp 1.425.000", "Tenggat Draf 1: ", "4 hari lagi", " (28 Okt 2026)"),
            items = listOf(
                ChatRoomItem.DateSeparator("Jumat, 24 Okt 2026"),
                ChatRoomItem.Message(ChatBubbleUi("1", false, "Halo Mas Andi! Terima kasih sudah menerima proyek ini.", "09.15 WIB", true, 0, "Rina Pratiwi · Kedai Kopi Senja")),
                ChatRoomItem.Message(ChatBubbleUi("2", true, "Selamat pagi Bu Rina! Siap, ide yang sangat menarik.", "09.22 WIB", true, 12))
            )
        ),
        draft = "",
        onDraftChange = {},
        onSendClick = {},
        isAttachmentOpen = true,
        onAttachmentToggle = {},
        onGalleryClick = {},
        onCameraClick = {},
        onDocumentClick = {},
        onUploadDraftClick = {},
        onInputFocused = {},
        onBack = {},
        onMoreClick = {},
        onProjectDetailClick = {}
    )
}
