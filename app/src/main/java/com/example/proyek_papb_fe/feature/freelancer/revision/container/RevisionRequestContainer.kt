package com.example.proyek_papb_fe.feature.freelancer.revision.container

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.proyek_papb_fe.core.data.RevisionRequestStore
import com.example.proyek_papb_fe.feature.freelancer.revision.component.RevisionRequestScreen

/**
 * Container Permintaan Revisi (Figma 17:5737) untuk [projectId] (mis. 101 = Desain Rebranding
 * Logo & Kemasan Pouch Kopi, Kedai Kopi Senja). Unggah draf dan diskusi berlangsung di ruang
 * chat project yang sama (tempat fitur Unggah Draf sudah ada), jadi ketiga aksi membuka chat
 * itu. Bila project tidak punya permintaan revisi, langsung kembali.
 */
@Composable
fun RevisionRequestContainer(
    projectId: Int,
    onBack: () -> Unit,
    onOpenProjectChat: (projectId: Int) -> Unit
) {
    val context = LocalContext.current
    // Permintaan terbaru project ini (contoh awal atau kiriman UMKM dari Review Hasil Project).
    val request = RevisionRequestStore.forProject(projectId)
    if (request == null) {
        LaunchedEffect(projectId) { onBack() }
        return
    }
    RevisionRequestScreen(
        request = request,
        onBack = onBack,
        onUploadRevisedDraft = { onOpenProjectChat(projectId) },
        onOpenChat = { onOpenProjectChat(projectId) },
        onAskClarification = { onOpenProjectChat(projectId) },
        onOpenReferenceFile = {
            Toast.makeText(context, "Pratinjau ${request.referenceFileName} belum tersedia", Toast.LENGTH_SHORT).show()
        }
    )
}
