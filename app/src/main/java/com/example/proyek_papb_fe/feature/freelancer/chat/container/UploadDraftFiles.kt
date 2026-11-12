package com.example.proyek_papb_fe.feature.freelancer.chat.container

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.proyek_papb_fe.core.data.model.DraftFile
import com.example.proyek_papb_fe.core.data.model.WorkDraft
import com.example.proyek_papb_fe.feature.freelancer.chat.component.DraftFileKind
import com.example.proyek_papb_fe.feature.freelancer.chat.component.DraftFileUi
import com.example.proyek_papb_fe.feature.freelancer.chat.component.UploadDraftUiState
import java.util.Locale

/** Figma 17:5174: "Mendukung AI, PSD, PDF, ZIP, PNG (Maks. 50 MB)". */
private val SupportedExtensions = setOf("ai", "psd", "pdf", "zip", "png")
private const val MaxDraftFileBytes = 50L * 1024 * 1024

/** Hasil memilih file: yang lolos cek dan nama file yang ditolak (format / ukuran). */
internal data class PickedDraftFiles(val accepted: List<DraftFile>, val rejected: List<String>)

/**
 * Baca nama & ukuran tiap file dari pemilih dokumen sistem lalu cek format & ukuran.
 * Tidak ada yang diunggah: hanya metadata lokal untuk ditampilkan di modal.
 */
internal fun Context.readDraftFiles(uris: List<Uri>): PickedDraftFiles {
    val accepted = mutableListOf<DraftFile>()
    val rejected = mutableListOf<String>()
    for (uri in uris) {
        var name: String? = null
        var size: Long? = null
        contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                name = cursor.getString(0)
                size = if (cursor.isNull(1)) null else cursor.getLong(1)
            }
        }
        val file = DraftFile(uri = uri.toString(), name = name ?: uri.lastPathSegment ?: "File", sizeBytes = size)
        val tooLarge = (file.sizeBytes ?: 0L) > MaxDraftFileBytes
        if (file.extension in SupportedExtensions && !tooLarge) accepted += file else rejected += file.name
    }
    return PickedDraftFiles(accepted, rejected)
}

/** Satuan desimal seperti pemilih file Android: 12_400_000 → "12.4 MB"; 850_000 → "850 KB". */
private fun formatSize(bytes: Long): String = when {
    bytes >= 1_000_000L -> String.format(Locale.US, "%.1f MB", bytes / 1_000_000.0)
    bytes >= 1_000L -> "${bytes / 1_000} KB"
    else -> "$bytes B"
}

private fun DraftFile.toUi(): DraftFileUi {
    val (kind, label) = when (extension) {
        "pdf" -> DraftFileKind.PDF to "PDF"
        "ai" -> DraftFileKind.VECTOR to "Adobe Illustrator"
        "psd" -> DraftFileKind.IMAGE to "Photoshop"
        "png" -> DraftFileKind.IMAGE to "PNG"
        else -> DraftFileKind.ARCHIVE to extension.uppercase().ifEmpty { "File" }
    }
    return DraftFileUi(id = uri, name = name, sizeLabel = sizeBytes?.let(::formatSize), typeLabel = label, kind = kind)
}

internal fun WorkDraft.toUiState(projectTitle: String, contactName: String) = UploadDraftUiState(
    projectTitle = projectTitle,
    contactFirstName = contactName.substringBefore(' '),
    version = version,
    files = files.map { it.toUi() },
    note = note
)
