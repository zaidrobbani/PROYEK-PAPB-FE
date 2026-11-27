package com.example.proyek_papb_fe.feature.collaboration_revision_umkm.compare_drafts.container

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.auth.component.shared.AuthAppBar
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.compare_drafts.component.CompareContextCard
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.compare_drafts.component.CompareDecisionTray
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.compare_drafts.component.DraftColumn
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.compare_drafts.component.SideBySideComparison
import com.example.proyek_papb_fe.feature.umkm.data.ProjectStatus
import com.example.proyek_papb_fe.feature.umkm.data.UmkmAccount
import com.example.proyek_papb_fe.feature.umkm.data.UmkmConversationStore
import com.example.proyek_papb_fe.feature.umkm.data.UmkmProjectStore
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.feature.collaboration_revision_umkm.viewmodel.UmkmChatViewModel

private val SentTime = DateTimeFormatter.ofPattern("HH.mm")
private val ShortDate = DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("id-ID"))

fun canApproveFinalDraft(status: ProjectStatus?): Boolean = status == ProjectStatus.MenungguReview

@Composable
fun CompareDraftsScreen(
    conversationId: String,
    draftMessageId: String,
    onBack: () -> Unit,
    onOpenChat: () -> Unit,
    onApproveFinal: (() -> Unit)?,
    modifier: Modifier = Modifier,
    viewModel: UmkmChatViewModel = viewModel(factory = UmkmChatViewModel.factory(conversationId))
) {
    val context = LocalContext.current
    fun toast(message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    val conversation = UmkmConversationStore.conversation(conversationId)
    val messages = UmkmConversationStore.messages(conversationId)
    val latest = messages.firstOrNull { it.id == draftMessageId }
    val previous = latest?.attachment?.previousDraftMessageId?.let { id -> messages.firstOrNull { it.id == id } }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            AuthAppBar(title = "Bandingkan Draf", onBack = onBack)
            val latestAttachment = latest?.attachment
            val previousAttachment = previous?.attachment
            if (conversation == null || latestAttachment == null || previousAttachment == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada dua versi draf untuk dibandingkan.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                return@Column
            }
            val status = conversation.projectId?.let { UmkmProjectStore.project(it)?.status }
            val profile = UmkmAccount.profile
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(Modifier.padding(start = 16.903.dp, end = 16.903.dp, top = 12.677.dp, bottom = 4.226.dp)) {
                    CompareContextCard(
                        projectTitle = conversation.projectTitle,
                        ownerLine = "${profile.businessName}, ${profile.ownerName}",
                        statusLabel = status?.label ?: "-",
                        statusIsReview = status == ProjectStatus.MenungguReview,
                        freelancerInitials = conversation.initials,
                        freelancerName = conversation.freelancerName,
                        sentTime = "${latest.sentAt.format(SentTime)} WIB",
                        versionLine = "${previousAttachment.versionLabel} → ${latestAttachment.versionLabel}"
                    )
                }
                Box(Modifier.padding(horizontal = 16.903.dp, vertical = 4.226.dp)) {
                    SideBySideComparison(
                        previous = DraftColumn(
                            headerLabel = "Sebelumnya (${previousAttachment.versionLabel})",
                            headerTrailing = previous.sentAt.format(ShortDate),
                            image = previousAttachment.previewImage,
                            previewTag = "Draf Lama",
                            noteTitle = "Catatan Draf Lalu",
                            note = previousAttachment.draftNote.orEmpty(),
                            markers = previousAttachment.issueMarkers,
                            latest = false
                        ),
                        latest = DraftColumn(
                            headerLabel = "Perbaikan (${latestAttachment.versionLabel})",
                            headerTrailing = "Terbaru",
                            image = latestAttachment.previewImage,
                            previewTag = latestAttachment.previewTag.orEmpty(),
                            noteTitle = "Telah Disempurnakan",
                            note = latestAttachment.draftNote.orEmpty(),
                            markers = previousAttachment.issueMarkers,
                            latest = true
                        )
                    )
                }
                Spacer(Modifier.height(118.318.dp))
            }
        }
        if (conversation != null && latest != null && previous != null) {
            CompareDecisionTray(
                onDiscuss = onOpenChat,
                onRequestRevision = { toast("Minta revisi belum tersedia di versi ini.") },
                onApprove = {
                    val status = conversation.projectId?.let { UmkmProjectStore.project(it)?.status }
                    if (!canApproveFinalDraft(status)) {
                        toast("Draf bisa disetujui setelah project berstatus Menunggu Review (hasil akhir dikirim freelancer).")
                    } else {
                        onApproveFinal?.invoke() ?: toast("Persetujuan hasil & pelepasan dana belum tersedia di versi ini.")
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
